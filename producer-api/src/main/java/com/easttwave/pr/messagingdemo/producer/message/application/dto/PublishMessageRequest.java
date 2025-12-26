package com.easttwave.pr.messagingdemo.producer.message.application.dto;

public record PublishMessageRequest(
    Long id,
    String topic,
    String message
) {}
