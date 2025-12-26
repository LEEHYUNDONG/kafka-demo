package com.easttwave.pr.messagingdemo.platform.sqs.dto;

public record SqsMessage(
        Long id,
        String payload
) {
}
