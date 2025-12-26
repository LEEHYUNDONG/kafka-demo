package com.easttwave.pr.kafkademo.platform.kafka.infrastructure.config;

import com.easttwave.pr.kafkademo.platform.kafka.adapter.in.dto.Message;
import org.springframework.context.annotation.Bean;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
public class KafkaProducerTemplateFactory {

    @Bean
    public KafkaTemplate<String, Message> messageKafkaTemplate(KafkaProducerConfig kafkaProducerConfig) {
        return new KafkaTemplate<>(kafkaProducerConfig.producerFactory());
    }

}
