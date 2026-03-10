package com.easttwave.pr.messagingdemo.producer.domain.kafka.repository;

import com.easttwave.pr.messagingdemo.producer.domain.kafka.model.MessageContent;

public interface KafkaMessageRepository {
    void publish(MessageContent message);
}
