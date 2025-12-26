package com.easttwave.pr.kafkademo.producer.message.application.dto;

public record PublishMessageRequest(
    Long id,
    String topic,
    String message
) {}
