package com.easttwave.pr.messagingdemo.producer.message.domain.repository;

import com.easttwave.pr.messagingdemo.producer.message.domain.model.MessageContent;

public interface MessagePublisher {
    void publish(MessageContent message);
}
