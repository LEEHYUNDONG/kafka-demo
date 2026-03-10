package com.easttwave.pr.messagingdemo.producer.presentation.sqs;

import com.easttwave.pr.messagingdemo.producer.application.sqs.SqsMessageService;
import com.easttwave.pr.messagingdemo.producer.presentation.sqs.dto.SqsMessageRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/sqs-message")
public class SqsMessageController {

    private final SqsMessageService sqsMessageService;

    @PostMapping("/send")
    ResponseEntity<?> sendMessage(@RequestBody SqsMessageRequest request) {
        sqsMessageService.sendMessage(request.queueUrl(), request.messageBody());
        return ResponseEntity.noContent().build();
    }

}
