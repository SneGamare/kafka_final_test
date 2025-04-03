package com.example.kafka.service;

import snehal.commonlibs.avro.PlutusFinacleData;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
public class KafkaProducerService {
    private static final Logger log = LoggerFactory.getLogger(KafkaProducerService.class);
    private static final String TOPIC = "plutus-finacle-topic";

    private final KafkaTemplate<String, PlutusFinacleData> kafkaTemplate;

    public KafkaProducerService(KafkaTemplate<String, PlutusFinacleData> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    public void sendMessage(PlutusFinacleData message) {
        try {
            log.info("Sending message to topic {}: {}", TOPIC, message);
            kafkaTemplate.send(TOPIC, message);
            log.info("Message sent successfully");
        } catch (Exception e) {
            log.error("Error sending message: {}", e.getMessage(), e);
            throw e;
        }
    }
} 