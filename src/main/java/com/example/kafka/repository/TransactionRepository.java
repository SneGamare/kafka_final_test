package com.example.kafka.repository;

import com.example.kafka.entity.TransactionData;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TransactionRepository extends JpaRepository<TransactionData, Long> {
} 