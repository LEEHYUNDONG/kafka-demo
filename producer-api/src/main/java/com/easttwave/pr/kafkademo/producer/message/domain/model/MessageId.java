package com.easttwave.pr.kafkademo.producer.message.domain.model;

public record MessageId(Long value) {
    public MessageId {
        if (value == null || value <= 0) {
            throw new IllegalArgumentException("Message ID must be positive");
        }
    }
}
