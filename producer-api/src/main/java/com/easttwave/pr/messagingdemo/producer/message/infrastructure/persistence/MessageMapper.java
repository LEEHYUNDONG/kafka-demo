package com.easttwave.pr.messagingdemo.producer.message.infrastructure.persistence;

import com.easttwave.pr.messagingdemo.producer.message.domain.model.MessageContent;
import com.easttwave.pr.messagingdemo.producer.message.domain.model.MessageId;
import com.easttwave.pr.messagingdemo.platform.kafka.adapter.in.dto.Message;
import org.springframework.stereotype.Component;

@Component
public class MessageMapper {

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
