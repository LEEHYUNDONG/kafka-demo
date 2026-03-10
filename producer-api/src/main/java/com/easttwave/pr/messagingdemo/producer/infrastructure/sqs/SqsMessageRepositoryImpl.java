package com.easttwave.pr.messagingdemo.producer.infrastructure.sqs;

import com.easttwave.pr.messagingdemo.producer.domain.sqs.repository.SqsMessageRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.awspring.cloud.sqs.operations.SqsTemplate;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component
public class SqsMessageRepositoryImpl implements SqsMessageRepository {
    @Value("${aws.sqs.queue-name:messaging-demo-queue}")
    private String queueName;

    private final ObjectMapper objectMapper;
    private final SqsTemplate sqsTemplate;

    @Override
    public void sendMessage(String queueUrl, String messageBody) {
        try {
            sqsTemplate.sendAsync(queueName, messageBody);
        } catch (Exception e) {
            throw new RuntimeException("Failed to send message to SQS", e);
        }
    }
}
