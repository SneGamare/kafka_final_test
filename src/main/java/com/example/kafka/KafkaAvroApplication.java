package com.example.kafka;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.web.client.RestTemplate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@SpringBootApplication
@ComponentScan(basePackages = {
    "com.example.kafka",
    "com.example.kafka.config",
    "com.example.kafka.controller",
    "com.example.kafka.service",
    "com.example.kafka.repository",
    "com.example.kafka.entity"
})
public class KafkaAvroApplication {
    private static final Logger log = LoggerFactory.getLogger(KafkaAvroApplication.class);

    public static void main(String[] args) {
        try {
            log.info("Starting Kafka Avro Application...");
            SpringApplication application = new SpringApplication(KafkaAvroApplication.class);
            application.setAdditionalProfiles("default");
            application.run(args);
            log.info("Kafka Avro Application started successfully");
        } catch (Exception e) {
            log.error("Failed to start Kafka Avro Application: {}", e.getMessage(), e);
            System.exit(1);
        }
    }

    @Bean
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }
} 