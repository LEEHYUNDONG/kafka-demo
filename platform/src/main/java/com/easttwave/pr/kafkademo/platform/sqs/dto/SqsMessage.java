package com.easttwave.pr.kafkademo.platform.sqs.dto;

public record SqsMessage(
        Long id,
        String payload
) {
}
