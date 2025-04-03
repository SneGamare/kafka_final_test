package com.example.kafka;

import com.example.kafka.entity.TransactionData;
import com.example.kafka.repository.TransactionRepository;
import snehal.commonlibs.avro.PlutusFinacleData;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDate;
import java.util.List;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
@DirtiesContext
public class KafkaIntegrationTest {

    @Autowired
    private KafkaTemplate<String, PlutusFinacleData> kafkaTemplate;

    @Autowired
    private TransactionRepository transactionRepository;

    @Autowired
    private ReactiveKafkaConsumer reactiveKafkaConsumer;

    @Test
    public void testKafkaMessageFlow() throws Exception {
        // Create and send test message
        PlutusFinacleData message = PlutusFinacleData.newBuilder()
            .setFORACID("1234567890")
            .setACCTNAME("Test Account")
            .setTRANAMT(1000.0)
            .setTRANDATE(LocalDate.now().toString())
            .build();

        kafkaTemplate.send("plutus-finacle-topic", message);

        // Wait for message to be processed
        TimeUnit.SECONDS.sleep(5);

        // Verify message was processed and saved
        List<TransactionData> transactions = reactiveKafkaConsumer.getMessages();
        assertFalse(transactions.isEmpty(), "No transactions found in database");
        
        TransactionData savedTransaction = transactions.get(0);
        assertEquals("1234567890", savedTransaction.getForacid());
        assertEquals("Test Account", savedTransaction.getAccountName());
        assertEquals(1000.0, savedTransaction.getTransactionAmount());
        assertEquals(LocalDate.now(), savedTransaction.getTransactionDate());
    }
} 