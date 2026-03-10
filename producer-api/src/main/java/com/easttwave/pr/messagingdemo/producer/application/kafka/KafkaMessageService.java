package com.easttwave.pr.messagingdemo.producer.application.kafka;

import com.easttwave.pr.messagingdemo.producer.application.kafka.dto.PublishMessageCommand;
import com.easttwave.pr.messagingdemo.producer.application.kafka.dto.PublishMessageResult;
import com.easttwave.pr.messagingdemo.producer.domain.kafka.model.MessageContent;
import com.easttwave.pr.messagingdemo.producer.domain.kafka.model.MessageId;
import com.easttwave.pr.messagingdemo.producer.domain.kafka.repository.KafkaMessageRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class KafkaMessageService {

    private final KafkaMessageRepository kafkaMessageRepository;

    public PublishMessageResult publishMessage(PublishMessageCommand command) {
        MessageContent messageContent = new MessageContent(
            new MessageId(command.id()),
            command.topic(),
            command.message()
        );

        kafkaMessageRepository.publish(messageContent);

        return PublishMessageResult.success();
    }
}
