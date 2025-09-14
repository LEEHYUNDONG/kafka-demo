package com.easttwave.pr.kafkademo.message.kafka.controller;


import com.easttwave.pr.kafkademo.message.kafka.dto.Message;
import org.springframework.http.ResponseEntity;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/message")
public record MessageController(
        KafkaTemplate<String, Message> messageKafkaTemplate
) {

    @PostMapping("/publish")
    ResponseEntity<?> publishMessage(@RequestBody Message message) {
        // send data
        messageKafkaTemplate.send(message.topic(), message);
        return ResponseEntity.ok("Message published");
    }

}
