package com.example.kafka.service;

import snehal.commonlibs.avro.PlutusFinacleData;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.support.SendResult;
import org.springframework.util.concurrent.ListenableFuture;
import org.springframework.util.concurrent.ListenableFutureCallback;
import org.springframework.kafka.support.KafkaNull;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.kafka.annotation.KafkaHandler;
import org.springframework.beans.factory.annotation.Autowired;
import com.example.kafka.repository.TransactionDataRepository;
import com.example.kafka.entity.TransactionData;
import java.time.LocalDate;

import java.util.ArrayList;
import java.util.List;
import java.util.Collections;
import java.util.concurrent.CopyOnWriteArrayList;

@Service
public class KafkaService {

    private static final Logger logger = LoggerFactory.getLogger(KafkaService.class);
    private static final String TOPIC = "plutus-finacle-topic";
    private final KafkaTemplate<String, PlutusFinacleData> kafkaTemplate;
    private final List<PlutusFinacleData> receivedMessages = new CopyOnWriteArrayList<>();
    @Autowired
    private TransactionDataRepository transactionDataRepository;

    public KafkaService(KafkaTemplate<String, PlutusFinacleData> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
        logger.info("KafkaService initialized with KafkaTemplate");
    }

    public String sendMessage(PlutusFinacleData message) {
        if (message == null) {
            logger.warn("Attempting to send null message, ignoring");
            return "Cannot send null message";
        }

        logger.info("Sending message to topic: {}", TOPIC);
        logger.info("Message details - FORACID: {}, ACCT_NAME: {}, TRAN_AMT: {}, TRAN_DATE: {}",
            message.getFORACID(), message.getACCTNAME(), message.getTRANAMT(), message.getTRANDATE());

        try {
            ListenableFuture<SendResult<String, PlutusFinacleData>> future = 
                kafkaTemplate.send(TOPIC, message);
            
            future.addCallback(new ListenableFutureCallback<SendResult<String, PlutusFinacleData>>() {
                @Override
                public void onSuccess(SendResult<String, PlutusFinacleData> result) {
                    logger.info("Message sent successfully: topic={}, partition={}, offset={}",
                        result.getRecordMetadata().topic(),
                        result.getRecordMetadata().partition(),
                        result.getRecordMetadata().offset());
                }

                @Override
                public void onFailure(Throwable ex) {
                    logger.error("Failed to send message: {}", ex.getMessage(), ex);
                }
            });
            return "Message sent to Kafka topic";
        } catch (Exception e) {
            logger.error("Error sending message to Kafka: {}", e.getMessage(), e);
            return "Error sending message: " + e.getMessage();
        }
    }

    @KafkaListener(topics = TOPIC, groupId = "plutus-finacle-consumer-group", containerFactory = "kafkaListenerContainerFactory")
    public void handleMessage(@Payload PlutusFinacleData message, Acknowledgment acknowledgment) {
        logger.info("Consumer received a message");
        
        if (message == null) {
            logger.warn("Received null message, acknowledging and ignoring");
            acknowledgment.acknowledge();
            return;
        }

        try {
            logger.info("Processing message - FORACID: {}, ACCT_NAME: {}, TRAN_AMT: {}, TRAN_DATE: {}",
                message.getFORACID(),
                message.getACCTNAME(),
                message.getTRANAMT(),
                message.getTRANDATE());
            
            receivedMessages.add(message);
            logger.info("Message added to receivedMessages. Current size: {}", receivedMessages.size());
            
            // Acknowledge the message immediately after processing
            acknowledgment.acknowledge();
            logger.info("Message acknowledged successfully");
            
            processMessage(message);
        } catch (Exception e) {
            logger.error("Error processing received message: {}", e.getMessage(), e);
            // In case of error, still acknowledge to prevent message being stuck
            acknowledgment.acknowledge();
        }
    }

    public List<PlutusFinacleData> getMessages() {
        logger.info("Retrieving messages. Current count: {}", receivedMessages.size());
        return new ArrayList<>(receivedMessages);
    }

    public void processMessage(PlutusFinacleData message) {
        logger.info("Processing message: {}", message);
        try {
            TransactionData transactionData = new TransactionData();
            transactionData.setForacid(message.getFORACID());
            transactionData.setAccountName(message.getACCTNAME());
            transactionData.setTransactionDate(LocalDate.parse(message.getTRANDATE()));
            transactionData.setTransactionAmount(message.getTRANAMT());
            transactionData.setStatus("PROCESSED");
            transactionData.setCreatedAt(LocalDate.now());

            logger.info("Saving transaction to database: {}", transactionData);
            transactionDataRepository.save(transactionData);
            logger.info("Transaction saved successfully with ID: {}", transactionData.getId());
        } catch (Exception e) {
            logger.error("Error processing message: {}", e.getMessage(), e);
            throw new RuntimeException("Failed to process message", e);
        }
    }
} 