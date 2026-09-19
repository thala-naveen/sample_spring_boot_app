package com.practice.samplespringboot.kafka;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.practice.samplespringboot.config.KafkaTopicConfig;
import com.practice.samplespringboot.entity.Alert;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
public class AlertProducer {

    private static final Logger log = LoggerFactory.getLogger(AlertProducer.class);

    private final KafkaTemplate<String, String> kafkaTemplate;
    private final ObjectMapper objectMapper;

    public AlertProducer(KafkaTemplate<String, String> kafkaTemplate, ObjectMapper objectMapper) {
        this.kafkaTemplate = kafkaTemplate;
        this.objectMapper = objectMapper;
    }

    public void sendAlert(Alert alert) {
        try {
            String json = objectMapper.writeValueAsString(alert);
            kafkaTemplate.send(KafkaTopicConfig.ALERTS_TOPIC, alert.getAlertId(), json);
            log.info("Published Alert [{}] (Risk Score: {}) to Kafka topic [{}]", alert.getAlertId(), alert.getRiskScore(), KafkaTopicConfig.ALERTS_TOPIC);
        } catch (Exception e) {
            log.error("Failed to publish alert to Kafka", e);
        }
    }
}
