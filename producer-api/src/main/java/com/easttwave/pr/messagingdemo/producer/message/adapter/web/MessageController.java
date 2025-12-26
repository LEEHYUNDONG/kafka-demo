package com.easttwave.pr.messagingdemo.producer.message.adapter.web;

import com.easttwave.pr.messagingdemo.producer.message.adapter.web.dto.MessageWebRequest;
import com.easttwave.pr.messagingdemo.producer.message.application.dto.PublishMessageRequest;
import com.easttwave.pr.messagingdemo.producer.message.application.dto.PublishMessageResponse;
import com.easttwave.pr.messagingdemo.producer.message.application.usecase.PublishMessageUseCase;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/message")
@RequiredArgsConstructor
@ConditionalOnProperty(name = "application.mode", havingValue = "producer", matchIfMissing = true)
public class MessageController {

    private final PublishMessageUseCase publishMessageUseCase;

    @PostMapping("/publish")
    ResponseEntity<PublishMessageResponse> publishMessage(
        @RequestBody MessageWebRequest request
    ) {
        PublishMessageRequest useCaseRequest = new PublishMessageRequest(
            request.id(),
            request.topic(),
            request.message()
        );

        PublishMessageResponse response = publishMessageUseCase.execute(useCaseRequest);
        return ResponseEntity.ok(response);
    }
}
