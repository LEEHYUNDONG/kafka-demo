package com.easttwave.pr.messagingdemo.producer.message.application.usecase;

import com.easttwave.pr.messagingdemo.producer.message.application.dto.PublishMessageRequest;
import com.easttwave.pr.messagingdemo.producer.message.application.dto.PublishMessageResponse;

public interface PublishMessageUseCase {
    PublishMessageResponse execute(PublishMessageRequest request);
}
