package com.easttwave.pr.messagingdemo.platform.kafka.adapter.in.dto;

public record Message(
        Long id,
        String topic,
        String message
) {
}
