package com.easttwave.pr.messagingdemo.producer.infrastructure.kafka;

import com.easttwave.pr.messagingdemo.platform.kafka.adapter.in.dto.Message;
import com.easttwave.pr.messagingdemo.producer.domain.kafka.model.MessageContent;
import com.easttwave.pr.messagingdemo.producer.domain.kafka.repository.KafkaMessageRepository;
import com.easttwave.pr.messagingdemo.producer.infrastructure.kafka.mapper.KafkaMessageMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class KafkaMessageRepositoryImpl implements KafkaMessageRepository {

    private final KafkaTemplate<String, Message> kafkaTemplate;
    private final KafkaMessageMapper messageMapper;

    @Override
    public void publish(MessageContent message) {
        Message kafkaMessage = messageMapper.toInfrastructure(message);
        kafkaTemplate.send(message.topic(), kafkaMessage);
    }
}
