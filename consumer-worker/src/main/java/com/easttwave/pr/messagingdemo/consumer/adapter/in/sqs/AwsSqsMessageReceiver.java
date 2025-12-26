package com.easttwave.pr.messagingdemo.consumer.adapter.in.sqs;


import com.easttwave.pr.messagingdemo.platform.sqs.dto.SqsMessage;
import io.awspring.cloud.sqs.annotation.SqsListener;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class AwsSqsMessageReceiver {

    @SqsListener("test-sqs.fifo")
    public void receiveMessage(String message) {
        log.info("Received SQS Message: {}", message);
    }

}
