package com.easttwave.pr.kafkademo.message.kafka.dto;

public record Message(
        Long id,
        String topic,
        String message
) {
}
