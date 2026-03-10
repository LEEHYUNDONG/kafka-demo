package com.easttwave.pr.messagingdemo.producer.application.sqs;

import com.easttwave.pr.messagingdemo.producer.domain.sqs.repository.SqsMessageRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class SqsMessageService {

    private final SqsMessageRepository sqsMessageRepository;
    private final ObjectMapper objectMapper;

    public void sendMessage(String queueUrl, Object messageBody) {
        try {
            String jsonMessage = objectMapper.writeValueAsString(messageBody);
            sqsMessageRepository.sendMessage(queueUrl, jsonMessage);
        } catch (Exception e) {
            throw new RuntimeException("Failed to serialize message", e);
        }
    }
}
