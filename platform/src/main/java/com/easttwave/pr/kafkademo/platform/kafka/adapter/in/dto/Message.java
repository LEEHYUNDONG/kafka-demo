package com.easttwave.pr.kafkademo.platform.kafka.adapter.in.dto;

public record Message(
        Long id,
        String topic,
        String message
) {
}
