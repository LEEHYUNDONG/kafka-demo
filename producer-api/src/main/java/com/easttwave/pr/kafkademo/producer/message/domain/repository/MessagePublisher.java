package com.easttwave.pr.kafkademo.producer.message.domain.repository;

import com.easttwave.pr.kafkademo.producer.message.domain.model.MessageContent;

public interface MessagePublisher {
    void publish(MessageContent message);
}
