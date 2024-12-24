package com.TechnicalChallenge.UniqueIDTracker_Service.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;

import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;


@Service
public class KafkaService {

    private static final Logger log = LoggerFactory.getLogger(KafkaService.class);
    //private static final String KAFKA_TOPIC = "Unique-requests";

    private final KafkaTemplate<String, String> kafkaTemplate;

    @Value("${kafka.topic.unique-requests}")
    private String kafkaTopic;

    public KafkaService(KafkaTemplate<String, String> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }


    @Async
    public void sendCountToKafka(int count) {
        kafkaTemplate.send(kafkaTopic, String.valueOf(count))
                .thenAccept(result -> {
                    log.info("Successfully sent count '{}' to Kafka topic '{}'", count, kafkaTopic);
                })
                .exceptionally(ex -> {
                    log.error("Failed to send count '{}' to Kafka topic '{}': {}", count, kafkaTopic, ex.toString(), ex);
                    return null;
                });
    }
}
