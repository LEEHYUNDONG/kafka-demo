package com.easttwave.pr.kafkademo.message.dto;

public record Message(
        Long id,
        String topic,
        String message
) {
}
