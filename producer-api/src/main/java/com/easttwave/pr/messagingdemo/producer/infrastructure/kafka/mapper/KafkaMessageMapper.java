package com.easttwave.pr.messagingdemo.producer.infrastructure.kafka.mapper;

import com.easttwave.pr.messagingdemo.platform.kafka.adapter.in.dto.Message;
import com.easttwave.pr.messagingdemo.producer.domain.kafka.model.MessageContent;
import com.easttwave.pr.messagingdemo.producer.domain.kafka.model.MessageId;
import org.springframework.stereotype.Component;

@Component
public class KafkaMessageMapper {

    public Message toInfrastructure(MessageContent domain) {
        return new Message(
            domain.id().value(),
            domain.topic(),
            domain.content()
        );
    }

    public MessageContent toDomain(Message infrastructure) {
        return new MessageContent(
            new MessageId(infrastructure.id()),
            infrastructure.topic(),
            infrastructure.message()
        );
    }
}
