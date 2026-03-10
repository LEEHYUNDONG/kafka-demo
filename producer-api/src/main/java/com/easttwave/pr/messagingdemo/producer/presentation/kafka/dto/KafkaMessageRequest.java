package com.easttwave.pr.messagingdemo.producer.presentation.kafka.dto;

public record KafkaMessageRequest(
    Long id,
    String topic,
    String message
) {}
