package com.easttwave.pr.kafkademo.infrastructure.sqs;


import com.easttwave.pr.kafkademo.infrastructure.sqs.dto.SqsMessage;
import io.awspring.cloud.sqs.annotation.SqsListener;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class AwsSqsMessageReceiver {

    @SqsListener("test-sqs")
    public void receiveMessage(String message) {
        log.info("Received SQS Message: {}", message);
    }

}
