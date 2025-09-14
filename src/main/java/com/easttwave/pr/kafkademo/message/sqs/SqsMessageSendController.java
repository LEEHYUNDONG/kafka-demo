package com.easttwave.pr.kafkademo.message.sqs;

import com.easttwave.pr.kafkademo.infrastructure.sqs.AwsSqsMessageSender;
import com.easttwave.pr.kafkademo.infrastructure.sqs.MessageSender;
import com.easttwave.pr.kafkademo.infrastructure.sqs.dto.SqsMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/sqs-message")
public class SqsMessageSendController {

    private final MessageSender messageSender;

    @PostMapping("/send")
    ResponseEntity<?> sendMessage(@RequestBody SqsMessage message) {
        messageSender.sendAsyncMessage(message);
        return ResponseEntity.noContent().build();
    }

}
