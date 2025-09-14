package com.easttwave.pr.kafkademo.message.kafka;

import com.easttwave.pr.kafkademo.message.kafka.dto.Message;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
public class KafkaMessageConsumer {

    @KafkaListener(topics = "kafka-demo-topic", groupId = "kafka-demo-group", concurrency = "10")
    public void listen(ConsumerRecord<String, Message> data) {
        System.out.println("Received Message: " + data.value().toString());
    }

}
