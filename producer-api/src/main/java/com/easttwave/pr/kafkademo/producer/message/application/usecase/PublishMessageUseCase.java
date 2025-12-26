package com.easttwave.pr.kafkademo.producer.message.application.usecase;

import com.easttwave.pr.kafkademo.producer.message.application.dto.PublishMessageRequest;
import com.easttwave.pr.kafkademo.producer.message.application.dto.PublishMessageResponse;

public interface PublishMessageUseCase {
    PublishMessageResponse execute(PublishMessageRequest request);
}
