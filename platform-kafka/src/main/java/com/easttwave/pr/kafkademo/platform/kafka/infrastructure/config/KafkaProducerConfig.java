package com.easttwave.pr.kafkademo.platform.kafka.infrastructure.config;

import com.easttwave.pr.kafkademo.platform.kafka.adapter.in.dto.Message;
import lombok.RequiredArgsConstructor;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.StringSerializer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.ProducerFactory;
import org.springframework.kafka.support.serializer.JsonSerializer;

import java.util.HashMap;
import java.util.Map;

@Configuration
@EnableKafka
@RequiredArgsConstructor
public class KafkaProducerConfig {

    private final KafkaProperties kafkaProperties;

    @Bean
    Map<String, Object> producerProps() {
        HashMap<String, Object> props = new HashMap<>();
        props.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, kafkaProperties.getBootstrapServers());
        props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, JsonSerializer.class);
        props.put(ProducerConfig.CLIENT_ID_CONFIG, kafkaProperties.getClientId());

        // Producer 성능 및 안정성 설정
        if (kafkaProperties.getProducer().getEnableAdaptivePartitioning() != null) {
            props.put(ProducerConfig.PARTITIONER_ADPATIVE_PARTITIONING_ENABLE_CONFIG,
                      kafkaProperties.getProducer().getEnableAdaptivePartitioning());
        }
        props.put(ProducerConfig.ACKS_CONFIG, kafkaProperties.getProducer().getAcks());
        props.put(ProducerConfig.RETRIES_CONFIG, kafkaProperties.getProducer().getRetries());
        props.put(ProducerConfig.MAX_IN_FLIGHT_REQUESTS_PER_CONNECTION,
                  kafkaProperties.getProducer().getMaxInFlightRequests());

        // MSK 보안 설정
        props.put("security.protocol", kafkaProperties.getSecurityProtocol());
        if (kafkaProperties.getSaslMechanism() != null && !kafkaProperties.getSaslMechanism().isEmpty()) {
            props.put("sasl.mechanism", kafkaProperties.getSaslMechanism());
        }
        if (kafkaProperties.getSaslJaasConfig() != null && !kafkaProperties.getSaslJaasConfig().isEmpty()) {
            props.put("sasl.jaas.config", kafkaProperties.getSaslJaasConfig());
        }

        return props;
    }

    @Bean
    public ProducerFactory<String, Message> producerFactory() {
        return new DefaultKafkaProducerFactory<>(producerProps());
    }
}
