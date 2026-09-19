package com.practice.samplespringboot.config;

import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.TopicBuilder;

@Configuration
public class KafkaTopicConfig {

    public static final String TRANSACTIONS_TOPIC = "transactions";
    public static final String ALERTS_TOPIC = "alerts";

    @Bean
    public NewTopic transactionsTopic() {
        return TopicBuilder.name(TRANSACTIONS_TOPIC)
                .partitions(1)
                .replicas(1)
                .build();
    }

    @Bean
    public NewTopic alertsTopic() {
        return TopicBuilder.name(ALERTS_TOPIC)
                .partitions(1)
                .replicas(1)
                .build();
    }
}
