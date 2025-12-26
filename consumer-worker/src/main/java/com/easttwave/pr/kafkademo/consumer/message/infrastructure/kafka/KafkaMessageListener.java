package com.easttwave.pr.kafkademo.consumer.message.infrastructure.kafka;

import com.easttwave.pr.kafkademo.platform.kafka.adapter.in.dto.Message;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class KafkaMessageListener {

    @KafkaListener(
        topics = "${application.kafka.topic.name}",
        groupId = "${application.kafka.consumer.group-id}",
        concurrency = "${application.kafka.consumer.concurrency}"
    )
    public void listen(ConsumerRecord<String, Message> record) {
        Message message = record.value();
        log.info("Received Message - ID: {}, Topic: {}, Content: {}",
                 message.id(), message.topic(), message.message());
    }
}
