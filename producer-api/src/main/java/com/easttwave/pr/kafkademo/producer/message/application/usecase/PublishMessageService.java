package com.easttwave.pr.kafkademo.producer.message.application.usecase;

import com.easttwave.pr.kafkademo.producer.message.application.dto.PublishMessageRequest;
import com.easttwave.pr.kafkademo.producer.message.application.dto.PublishMessageResponse;
import com.easttwave.pr.kafkademo.producer.message.domain.model.MessageContent;
import com.easttwave.pr.kafkademo.producer.message.domain.model.MessageId;
import com.easttwave.pr.kafkademo.producer.message.domain.repository.MessagePublisher;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class PublishMessageService implements PublishMessageUseCase {

    private final MessagePublisher messagePublisher;

    @Override
    public PublishMessageResponse execute(PublishMessageRequest request) {
        MessageContent messageContent = new MessageContent(
            new MessageId(request.id()),
            request.topic(),
            request.message()
        );

        messagePublisher.publish(messageContent);

        return PublishMessageResponse.success();
    }
}
