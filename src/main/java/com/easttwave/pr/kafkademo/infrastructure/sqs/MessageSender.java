package com.easttwave.pr.kafkademo.infrastructure.sqs;

import com.easttwave.pr.kafkademo.infrastructure.sqs.dto.SqsMessage;

public interface MessageSender {
    void sendAsyncMessage(SqsMessage message);
}
