package com.easttwave.pr.kafkademo.producer.sqs.domain.repository;

import com.easttwave.pr.kafkademo.platform.sqs.dto.SqsMessage;

public interface MessageSender {
    void sendAsyncMessage(SqsMessage message);
}
