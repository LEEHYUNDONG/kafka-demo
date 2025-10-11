//package com.easttwave.pr.kafkademo.common.core.kafka;
//
//import com.easttwave.pr.kafkademo.message.kafka.dto.Message;
//import org.springframework.context.annotation.Bean;
//import org.springframework.kafka.core.KafkaTemplate;
//import org.springframework.stereotype.Component;
//
//@Component
//public class KafkaProducerTemplateFactory {
//
//    @Bean
//    public KafkaTemplate<String, Message> messageKafkaTemplate(KafkaProducerConfig kafkaProducerConfig) {
//        return new KafkaTemplate<>(kafkaProducerConfig.producerFactory());
//    }
//
//}
