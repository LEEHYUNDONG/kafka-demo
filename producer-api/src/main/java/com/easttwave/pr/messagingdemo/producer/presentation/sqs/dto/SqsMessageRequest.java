package com.easttwave.pr.messagingdemo.producer.presentation.sqs.dto;

public record SqsMessageRequest(
    String queueUrl,
    Object messageBody
) {}
