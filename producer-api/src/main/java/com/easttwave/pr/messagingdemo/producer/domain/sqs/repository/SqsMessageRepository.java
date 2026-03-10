package com.easttwave.pr.messagingdemo.producer.domain.sqs.repository;

public interface SqsMessageRepository {
    void sendMessage(String queueUrl, String messageBody);
}
