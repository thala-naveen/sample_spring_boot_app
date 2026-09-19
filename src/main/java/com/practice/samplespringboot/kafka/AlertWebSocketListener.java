package com.practice.samplespringboot.kafka;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.practice.samplespringboot.config.KafkaTopicConfig;
import com.practice.samplespringboot.entity.Alert;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Component;

@Component
public class AlertWebSocketListener {

    private static final Logger log = LoggerFactory.getLogger(AlertWebSocketListener.class);

    private final SimpMessagingTemplate messagingTemplate;
    private final ObjectMapper objectMapper;

    public AlertWebSocketListener(SimpMessagingTemplate messagingTemplate, ObjectMapper objectMapper) {
        this.messagingTemplate = messagingTemplate;
        this.objectMapper = objectMapper;
    }

    @KafkaListener(topics = KafkaTopicConfig.ALERTS_TOPIC, groupId = "sentinel-ws-group")
    public void onAlertReceived(String alertJson) {
        try {
            log.info("Kafka Listener consumed alert from topic. Dispatching to STOMP WebSocket /topic/alerts...");
            Alert alert = objectMapper.readValue(alertJson, Alert.class);
            messagingTemplate.convertAndSend("/topic/alerts", alert);
        } catch (Exception e) {
            log.error("Error dispatching alert to WebSocket", e);
        }
    }
}
