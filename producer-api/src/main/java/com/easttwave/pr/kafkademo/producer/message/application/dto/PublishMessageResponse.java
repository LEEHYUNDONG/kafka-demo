package com.easttwave.pr.kafkademo.producer.message.application.dto;

public record PublishMessageResponse(
    String status,
    String message
) {
    public static PublishMessageResponse success() {
        return new PublishMessageResponse("SUCCESS", "Message published successfully");
    }
}
