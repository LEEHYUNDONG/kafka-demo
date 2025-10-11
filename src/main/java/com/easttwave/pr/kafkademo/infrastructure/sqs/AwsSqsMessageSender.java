package com.easttwave.pr.kafkademo.infrastructure.sqs;

import com.easttwave.pr.kafkademo.infrastructure.sqs.dto.SqsMessage;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.awspring.cloud.sqs.operations.SqsTemplate;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component
public class AwsSqsMessageSender implements MessageSender{
    private static final String queueName="test-sqs";

    private final ObjectMapper objectMapper;

    private final SqsTemplate sqsTemplate;


    @Override
    public void sendAsyncMessage(SqsMessage message) {
        try {
            String jsonMessage = objectMapper.writeValueAsString(message);
            sqsTemplate.sendAsync(queueName, jsonMessage);
        } catch (Exception e) {
            throw new RuntimeException("Failed to send message to SQS", e);
        }
    }
}
