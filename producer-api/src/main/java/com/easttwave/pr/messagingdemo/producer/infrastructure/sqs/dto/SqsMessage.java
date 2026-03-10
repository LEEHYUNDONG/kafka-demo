package com.easttwave.pr.messagingdemo.producer.infrastructure.sqs.dto;

public record SqsMessage(
        Long id,
        String payload
) {
}
