package com.example.kafka;

import snehal.commonlibs.avro.PlutusFinacleData;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.StringSerializer;
import io.confluent.kafka.serializers.KafkaAvroSerializer;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Properties;

@Component
public class KafkaAvroProducer {
    private static final String BOOTSTRAP_SERVERS = "localhost:9092";
    private static final String SCHEMA_REGISTRY_URL = "http://localhost:8081";
    private static final String TOPIC = "plutus-finacle-topic";
    private static final Logger log = LoggerFactory.getLogger(KafkaAvroProducer.class);

    private final KafkaTemplate<String, PlutusFinacleData> kafkaTemplate;

    public KafkaAvroProducer(KafkaTemplate<String, PlutusFinacleData> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    public void sendMessage(PlutusFinacleData message) {
        try {
            kafkaTemplate.send(TOPIC, message).addCallback(
                result -> {
                    if (result != null) {
                        log.info("Message sent successfully: topic={}, partition={}, offset={}",
                                result.getRecordMetadata().topic(),
                                result.getRecordMetadata().partition(),
                                result.getRecordMetadata().offset());
                    }
                },
                ex -> log.error("Error sending message: {}", message, ex)
            );
        } catch (Exception e) {
            log.error("Error sending message: {}", message, e);
        }
    }

    public static void main(String[] args) {
        Properties properties = new Properties();
        properties.setProperty("bootstrap.servers", BOOTSTRAP_SERVERS);
        properties.setProperty("key.serializer", StringSerializer.class.getName());
        properties.setProperty("value.serializer", KafkaAvroSerializer.class.getName());
        properties.setProperty("schema.registry.url", SCHEMA_REGISTRY_URL);

        KafkaProducer<String, PlutusFinacleData> producer = new KafkaProducer<>(properties);

        // Create a PlutusFinacleData record
        PlutusFinacleData data = PlutusFinacleData.newBuilder()
                .setFORACID("1234567890")
                .setACCTNAME("Test Account")
                .setLASTTRANDATECR("2024-03-20")
                .setTRANDATE("2024-03-20")
                .setTRANID("T12345")
                .setPARTTRANSRLNUM("1")
                .setDELFLG("N")
                .setTRANTYPE("CR")
                .setTRANSUBTYPE("CASH")
                .setPARTTRANTYPE("CR")
                .setGLSUBHEADCODE("GL123")
                .setACID("AC123")
                .setVALUEDATE("2024-03-20")
                .setTRANAMT(1000.50)
                .setTRANPARTICULAR("Test Transaction")
                .setENTRYDATE("2024-03-20")
                .setPSTDDATE("2024-03-20")
                .setREFNUM("REF123")
                .setINSTRMNTTYPE("CASH")
                .setINSTRMNTDATE("2024-03-20")
                .setINSTRMNTNUM("INSTR123")
                .build();

        ProducerRecord<String, PlutusFinacleData> producerRecord = new ProducerRecord<>(
                TOPIC, "plutus-key", data
        );

        System.out.println("Sending message...");
        producer.send(producerRecord, (metadata, exception) -> {
            if (exception == null) {
                System.out.println("Message sent successfully!");
                System.out.println("Topic: " + metadata.topic());
                System.out.println("Partition: " + metadata.partition());
                System.out.println("Offset: " + metadata.offset());
            } else {
                exception.printStackTrace();
            }
        });

        producer.flush();
        producer.close();
    }
} 