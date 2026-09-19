package com.practice.samplespringboot.kafka;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.practice.samplespringboot.config.KafkaTopicConfig;
import com.practice.samplespringboot.entity.Transaction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
public class TransactionProducer {

    private static final Logger log = LoggerFactory.getLogger(TransactionProducer.class);

    private final KafkaTemplate<String, String> kafkaTemplate;
    private final ObjectMapper objectMapper;

    public TransactionProducer(KafkaTemplate<String, String> kafkaTemplate, ObjectMapper objectMapper) {
        this.kafkaTemplate = kafkaTemplate;
        this.objectMapper = objectMapper;
    }

    public void sendTransaction(Transaction transaction) {
        try {
            String json = objectMapper.writeValueAsString(transaction);
            kafkaTemplate.send(KafkaTopicConfig.TRANSACTIONS_TOPIC, transaction.getAccountId(), json);
            log.debug("Published transaction [{}] to Kafka topic [{}]", transaction.getTransactionId(), KafkaTopicConfig.TRANSACTIONS_TOPIC);
        } catch (Exception e) {
            log.error("Failed to publish transaction to Kafka", e);
        }
    }
}
