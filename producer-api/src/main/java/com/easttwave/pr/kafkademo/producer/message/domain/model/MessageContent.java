package com.easttwave.pr.kafkademo.producer.message.domain.model;

public record MessageContent(
    MessageId id,
    String topic,
    String content
) {
    public MessageContent {
        if (topic == null || topic.isBlank()) {
            throw new IllegalArgumentException("Topic cannot be empty");
        }
        if (content == null || content.isBlank()) {
            throw new IllegalArgumentException("Content cannot be empty");
        }
    }
}
