package com.easttwave.pr.messagingdemo.producer.sqs.domain.repository;

import com.easttwave.pr.messagingdemo.platform.sqs.dto.SqsMessage;

public interface MessageSender {
    void sendAsyncMessage(SqsMessage message);
}
