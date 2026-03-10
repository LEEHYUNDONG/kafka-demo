package com.easttwave.pr.messagingdemo.producer.application.kafka.dto;

public record PublishMessageCommand(
    Long id,
    String topic,
    String message
) {}
