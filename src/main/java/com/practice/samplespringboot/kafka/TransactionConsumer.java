package com.practice.samplespringboot.kafka;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.practice.samplespringboot.config.KafkaTopicConfig;
import com.practice.samplespringboot.entity.Transaction;
import com.practice.samplespringboot.service.DetectionEngineService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
public class TransactionConsumer {

    private static final Logger log = LoggerFactory.getLogger(TransactionConsumer.class);

    private final DetectionEngineService detectionEngineService;
    private final ObjectMapper objectMapper;

    public TransactionConsumer(DetectionEngineService detectionEngineService, ObjectMapper objectMapper) {
        this.detectionEngineService = detectionEngineService;
        this.objectMapper = objectMapper;
    }

    @KafkaListener(topics = KafkaTopicConfig.TRANSACTIONS_TOPIC, groupId = "sentinel-detection-group")
    public void consumeTransaction(String txnJson) {
        try {
            log.info("Kafka Transaction Consumer received message from topic [{}]. Deserializing...", KafkaTopicConfig.TRANSACTIONS_TOPIC);
            Transaction txn = objectMapper.readValue(txnJson, Transaction.class);
            detectionEngineService.processTransaction(txn);
        } catch (Exception e) {
            log.error("Failed to process transaction from Kafka", e);
        }
    }
}
