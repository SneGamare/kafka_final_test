package com.example.kafka;

import snehal.commonlibs.avro.PlutusFinacleData;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.springframework.stereotype.Component;
import reactor.kafka.receiver.KafkaReceiver;
import reactor.kafka.receiver.ReceiverOptions;
import reactor.kafka.receiver.ReceiverRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import javax.annotation.PostConstruct;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.List;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import io.confluent.kafka.serializers.KafkaAvroDeserializer;
import io.confluent.kafka.serializers.KafkaAvroDeserializerConfig;
import com.example.kafka.entity.TransactionData;
import com.example.kafka.repository.TransactionRepository;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.dao.DataAccessException;
import reactor.core.publisher.Flux;
import reactor.util.retry.Retry;
import java.time.Duration;
import org.apache.kafka.clients.admin.AdminClient;
import org.apache.kafka.clients.admin.AdminClientConfig;
import org.apache.kafka.clients.admin.CreateTopicsResult;
import org.apache.kafka.clients.admin.DescribeTopicsResult;
import org.apache.kafka.clients.admin.NewTopic;

@Component
public class ReactiveKafkaConsumer {
    private static final Logger log = LoggerFactory.getLogger(ReactiveKafkaConsumer.class);
    private static final String BOOTSTRAP_SERVERS = "localhost:9092";
    private static final String SCHEMA_REGISTRY_URL = "http://localhost:8081";
    private static final String TOPIC = "plutus-finacle-topic";
    private static final String GROUP_ID = "plutus-finacle-consumer-group";

    private final KafkaReceiver<String, PlutusFinacleData> receiver;
    private final TransactionRepository transactionRepository;
    private final AdminClient adminClient;

    public ReactiveKafkaConsumer(TransactionRepository transactionRepository) {
        this.transactionRepository = transactionRepository;
        
        try {
            // Create AdminClient to manage topics
            Map<String, Object> adminProps = new HashMap<>();
            adminProps.put(AdminClientConfig.BOOTSTRAP_SERVERS_CONFIG, BOOTSTRAP_SERVERS);
            this.adminClient = AdminClient.create(adminProps);
            
            // Create topic if it doesn't exist
            createTopicIfNotExists();
            
            // Consumer configuration
            Map<String, Object> props = new HashMap<>();
            props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, BOOTSTRAP_SERVERS);
            props.put(ConsumerConfig.GROUP_ID_CONFIG, GROUP_ID);
            props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
            props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, KafkaAvroDeserializer.class);
            props.put(KafkaAvroDeserializerConfig.SCHEMA_REGISTRY_URL_CONFIG, SCHEMA_REGISTRY_URL);
            props.put(KafkaAvroDeserializerConfig.SPECIFIC_AVRO_READER_CONFIG, true);
            props.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest"); // Changed to earliest to ensure we don't miss messages
            props.put(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, false);
            props.put(ConsumerConfig.MAX_POLL_RECORDS_CONFIG, 100);
            props.put(ConsumerConfig.FETCH_MIN_BYTES_CONFIG, 1);
            props.put(ConsumerConfig.FETCH_MAX_WAIT_MS_CONFIG, 100);
            props.put(ConsumerConfig.SESSION_TIMEOUT_MS_CONFIG, 30000);
            props.put(ConsumerConfig.HEARTBEAT_INTERVAL_MS_CONFIG, 10000);
            props.put(ConsumerConfig.MAX_POLL_INTERVAL_MS_CONFIG, 300000);

            log.info("Initializing Kafka consumer with properties: {}", props);

            ReceiverOptions<String, PlutusFinacleData> receiverOptions = ReceiverOptions.<String, PlutusFinacleData>create(props)
                .subscription(Collections.singleton(TOPIC))
                .addAssignListener(partitions -> log.info("onPartitionsAssigned {}", partitions))
                .addRevokeListener(partitions -> log.info("onPartitionsRevoked {}", partitions));

            this.receiver = KafkaReceiver.create(receiverOptions);
            log.info("KafkaReceiver created successfully");
        } catch (Exception e) {
            log.error("Error initializing Kafka consumer: {}", e.getMessage(), e);
            throw new RuntimeException("Failed to initialize Kafka consumer", e);
        }
    }

    private void createTopicIfNotExists() {
        try {
            DescribeTopicsResult describeTopicsResult = adminClient.describeTopics(Collections.singleton(TOPIC));
            describeTopicsResult.topicNameValues().get(TOPIC).get();
            log.info("Topic {} already exists", TOPIC);
        } catch (Exception e) {
            log.info("Topic {} does not exist, creating it", TOPIC);
            NewTopic newTopic = new NewTopic(TOPIC, 1, (short) 1);
            CreateTopicsResult result = adminClient.createTopics(Collections.singleton(newTopic));
            try {
                result.all().get();
                log.info("Topic {} created successfully", TOPIC);
            } catch (Exception ex) {
                log.error("Failed to create topic {}: {}", TOPIC, ex.getMessage(), ex);
            }
        }
    }

    @PostConstruct
    public void start() {
        try {
            log.info("Starting Kafka consumer subscription");
            receiver.receive()
                .doOnNext(record -> {
                    try {
                        PlutusFinacleData value = record.value();
                        if (value == null) {
                            log.warn("Received null value for key: {}", record.key());
                            record.receiverOffset().acknowledge();
                            return;
                        }

                        // Validate required fields
                        if (value.getFORACID() == null || value.getACCTNAME() == null || 
                            value.getTRANAMT() == null || value.getTRANDATE() == null) {
                            log.error("Message missing required fields: FORACID={}, ACCT_NAME={}, TRAN_AMT={}, TRAN_DATE={}",
                                value.getFORACID(), value.getACCTNAME(), value.getTRANAMT(), value.getTRANDATE());
                            record.receiverOffset().acknowledge();
                            return;
                        }

                        log.info("Received message: key={}", record.key());
                        log.info("FORACID: {}", value.getFORACID());
                        log.info("ACCT_NAME: {}", value.getACCTNAME());
                        log.info("TRAN_AMT: {}", value.getTRANAMT());
                        log.info("TRAN_DATE: {}", value.getTRANDATE());
                        
                        // Store in H2 database
                        saveToDatabase(value);
                        
                        // Acknowledge the message only after successful processing
                        record.receiverOffset().acknowledge();
                        log.info("Message processed and acknowledged successfully");
                    } catch (Exception e) {
                        log.error("Error processing message: key={}, error={}", record.key(), e.getMessage(), e);
                        // Don't acknowledge on error to allow retry
                    }
                })
                .doOnError(error -> {
                    log.error("Error in Kafka consumer: {}", error.getMessage(), error);
                })
                .retryWhen(Retry.backoff(3, Duration.ofSeconds(1)))
                .subscribe();
            log.info("Kafka consumer subscription started successfully");
        } catch (Exception e) {
            log.error("Error starting Kafka consumer: {}", e.getMessage(), e);
            throw new RuntimeException("Failed to start Kafka consumer", e);
        }
    }

    @Transactional
    protected void saveToDatabase(PlutusFinacleData message) {
        try {
            log.info("Attempting to save transaction to database");
            TransactionData transaction = new TransactionData();
            transaction.setForacid(message.getFORACID().toString());
            transaction.setAccountName(message.getACCTNAME().toString());
            transaction.setTransactionAmount(message.getTRANAMT());
            transaction.setTransactionDate(LocalDate.parse(message.getTRANDATE().toString(), DateTimeFormatter.ISO_DATE));
            transaction.setStatus("PROCESSED");
            transaction.setCreatedAt(LocalDate.now());
            
            TransactionData savedTransaction = transactionRepository.save(transaction);
            log.info("Transaction saved successfully with ID: {}", savedTransaction.getId());
        } catch (DataAccessException e) {
            log.error("Database error while saving transaction: {}", e.getMessage(), e);
            throw e;
        } catch (Exception e) {
            log.error("Unexpected error while saving transaction: {}", e.getMessage(), e);
            throw new RuntimeException("Failed to save transaction", e);
        }
    }

    public List<TransactionData> getMessages() {
        try {
            log.info("Retrieving all transactions from database");
            List<TransactionData> transactions = transactionRepository.findAll();
            log.info("Retrieved {} transactions from database", transactions.size());
            if (transactions.isEmpty()) {
                log.warn("No transactions found in database");
            } else {
                transactions.forEach(t -> log.info("Found transaction: ID={}, FORACID={}, ACCT_NAME={}, AMT={}, DATE={}",
                    t.getId(), t.getForacid(), t.getAccountName(), t.getTransactionAmount(), t.getTransactionDate()));
            }
            return transactions;
        } catch (DataAccessException e) {
            log.error("Database error while retrieving transactions: {}", e.getMessage(), e);
            throw e;
        } catch (Exception e) {
            log.error("Unexpected error while retrieving transactions: {}", e.getMessage(), e);
            throw new RuntimeException("Failed to retrieve transactions", e);
        }
    }
} 