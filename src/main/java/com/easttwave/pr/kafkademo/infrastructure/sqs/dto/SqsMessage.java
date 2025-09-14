package com.easttwave.pr.kafkademo.infrastructure.sqs.dto;

public record SqsMessage(
        Long id,
        String payload
) {
}
