package com.easttwave.pr.messagingdemo.platform.kafka.infrastructure.config;

import com.easttwave.pr.messagingdemo.platform.kafka.adapter.in.dto.Message;
import lombok.RequiredArgsConstructor;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.support.serializer.JsonDeserializer;

import java.util.HashMap;

@EnableKafka
@Configuration
@RequiredArgsConstructor
public class KafkaConsumerConfig {

    private final KafkaProperties kafkaProperties;

    @Bean
    DefaultKafkaConsumerFactory<String, Message> consumerFactory() {
        HashMap<String, Object> props = new HashMap<>();
        JsonDeserializer<Message> deserializer = new JsonDeserializer<>(Message.class, false);

        props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, kafkaProperties.getBootstrapServers());
        props.put(ConsumerConfig.GROUP_ID_CONFIG, kafkaProperties.getConsumer().getGroupId());
        props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
        props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, JsonDeserializer.class);
        props.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, kafkaProperties.getConsumer().getAutoOffsetReset());

        // MSK 보안 설정
        props.put("security.protocol", kafkaProperties.getSecurityProtocol());
        if (kafkaProperties.getSaslMechanism() != null && !kafkaProperties.getSaslMechanism().isEmpty()) {
            props.put("sasl.mechanism", kafkaProperties.getSaslMechanism());
        }
        if (kafkaProperties.getSaslJaasConfig() != null && !kafkaProperties.getSaslJaasConfig().isEmpty()) {
            props.put("sasl.jaas.config", kafkaProperties.getSaslJaasConfig());
        }

        return new DefaultKafkaConsumerFactory<>(props, new StringDeserializer(), deserializer);
    }

    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, Message> kafkaListenerContainerFactory() {
        ConcurrentKafkaListenerContainerFactory<String, Message> factory = new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConsumerFactory(consumerFactory());
        return factory;
    }

}
