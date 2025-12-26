package com.easttwave.pr.messagingdemo.producer.message.infrastructure.kafka;

import com.easttwave.pr.messagingdemo.producer.message.domain.model.MessageContent;
import com.easttwave.pr.messagingdemo.producer.message.domain.repository.MessagePublisher;
import com.easttwave.pr.messagingdemo.platform.kafka.adapter.in.dto.Message;
import com.easttwave.pr.messagingdemo.producer.message.infrastructure.persistence.MessageMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class KafkaMessagePublisher implements MessagePublisher {

    private final KafkaTemplate<String, Message> kafkaTemplate;
    private final MessageMapper messageMapper;

    @Override
    public void publish(MessageContent message) {
        Message kafkaMessage = messageMapper.toInfrastructure(message);
        kafkaTemplate.send(message.topic(), kafkaMessage);
    }
}
