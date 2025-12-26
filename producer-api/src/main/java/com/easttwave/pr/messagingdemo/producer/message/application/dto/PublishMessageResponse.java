package com.easttwave.pr.messagingdemo.producer.message.application.dto;

public record PublishMessageResponse(
    String status,
    String message
) {
    public static PublishMessageResponse success() {
        return new PublishMessageResponse("SUCCESS", "Message published successfully");
    }
}
