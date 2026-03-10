package com.easttwave.pr.messagingdemo.producer.application.kafka.dto;

public record PublishMessageResult(
    String status,
    String message
) {
    public static PublishMessageResult success() {
        return new PublishMessageResult("SUCCESS", "Message published successfully");
    }
}
