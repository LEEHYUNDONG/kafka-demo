package com.easttwave.pr.messagingdemo.producer.presentation.kafka;

import com.easttwave.pr.messagingdemo.producer.application.kafka.KafkaMessageService;
import com.easttwave.pr.messagingdemo.producer.application.kafka.dto.PublishMessageCommand;
import com.easttwave.pr.messagingdemo.producer.application.kafka.dto.PublishMessageResult;
import com.easttwave.pr.messagingdemo.producer.presentation.kafka.dto.KafkaMessageRequest;
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
public class KafkaMessageController {

    private final KafkaMessageService kafkaMessageService;

    @PostMapping("/publish")
    ResponseEntity<PublishMessageResult> publishMessage(
        @RequestBody KafkaMessageRequest request
    ) {
        PublishMessageCommand command = new PublishMessageCommand(
            request.id(),
            request.topic(),
            request.message()
        );

        PublishMessageResult result = kafkaMessageService.publishMessage(command);
        return ResponseEntity.ok(result);
    }
}
