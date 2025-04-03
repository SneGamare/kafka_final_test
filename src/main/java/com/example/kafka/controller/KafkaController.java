package com.example.kafka.controller;

import com.example.kafka.ReactiveKafkaConsumer;
import com.example.kafka.service.KafkaProducerService;
import com.example.kafka.entity.TransactionData;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.ResponseEntity;
import snehal.commonlibs.avro.PlutusFinacleData;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.HashMap;

@RestController
@RequestMapping("/api/kafka")
public class KafkaController {
    private static final Logger log = LoggerFactory.getLogger(KafkaController.class);

    private final ReactiveKafkaConsumer consumer;
    private final KafkaProducerService producerService;

    public KafkaController(ReactiveKafkaConsumer consumer, KafkaProducerService producerService) {
        this.consumer = consumer;
        this.producerService = producerService;
    }

    @GetMapping("/health")
    public ResponseEntity<String> health() {
        return ResponseEntity.ok("Kafka service is healthy");
    }

    @PostMapping("/send")
    public ResponseEntity<String> sendMessage() {
        PlutusFinacleData message = PlutusFinacleData.newBuilder()
            .setFORACID("1234567890")
            .setACCTNAME("Test Account")
            .setTRANAMT(1000.0)
            .setTRANDATE(LocalDate.now().toString())
            .build();

        try {
            producerService.sendMessage(message);
            return ResponseEntity.ok("Message sent successfully");
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body("Failed to send message: " + e.getMessage());
        }
    }

    @GetMapping("/messages")
    public ResponseEntity<?> getMessages() {
        try {
            List<TransactionData> transactions = consumer.getMessages();
            return ResponseEntity.ok(transactions);
        } catch (Exception e) {
            Map<String, String> errorResponse = new HashMap<>();
            errorResponse.put("error", "Failed to retrieve messages: " + e.getMessage());
            return ResponseEntity.internalServerError().body(errorResponse);
        }
    }

    @PostMapping("/test")
    public ResponseEntity<?> testProducerConsumer(@RequestBody PlutusFinacleData message) {
        Map<String, Object> response = new HashMap<>();
        try {
            log.info("Received test request with message: {}", message);
            
            // Send message through producer
            producerService.sendMessage(message);
            response.put("producerStatus", "Message sent successfully");
            
            // Get messages from consumer
            List<TransactionData> transactions = consumer.getMessages();
            response.put("consumerStatus", "Messages retrieved successfully");
            response.put("transactions", transactions);
            
            log.info("Test completed successfully");
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("Error in test request: {}", e.getMessage(), e);
            response.put("error", "Test failed: " + e.getMessage());
            return ResponseEntity.internalServerError().body(response);
        }
    }
} 