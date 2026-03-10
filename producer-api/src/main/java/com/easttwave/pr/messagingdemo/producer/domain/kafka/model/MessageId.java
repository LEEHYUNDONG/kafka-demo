package com.easttwave.pr.messagingdemo.producer.domain.kafka.model;

public record MessageId(Long value) {
    public MessageId {
        if (value == null || value <= 0) {
            throw new IllegalArgumentException("Message ID must be positive");
        }
    }
}
