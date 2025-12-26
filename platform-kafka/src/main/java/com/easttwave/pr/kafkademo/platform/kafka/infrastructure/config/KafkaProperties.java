package com.easttwave.pr.kafkademo.platform.kafka.infrastructure.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.util.HashMap;
import java.util.Map;

@Getter
@Setter
@Configuration
@ConfigurationProperties(prefix = "application.kafka")
public class KafkaProperties {
    private String bootstrapServers;
    private String clientId;

    // MSK 보안 설정
    private String securityProtocol = "PLAINTEXT";
    private String saslMechanism;
    private String saslJaasConfig;

    private Consumer consumer = new Consumer();
    private Producer producer = new Producer();
    private Topic topic = new Topic();

    @Getter
    @Setter
    public static class Consumer {
        private String groupId;
        private String autoOffsetReset;
        private Integer concurrency;
    }

    @Getter
    @Setter
    public static class Producer {
        private Boolean enableAdaptivePartitioning;
        private String acks = "all";
        private Integer retries = 3;
        private Integer maxInFlightRequests = 5;
    }

    @Getter
    @Setter
    public static class Topic {
        private String name;
        private Integer partitions;
        private Integer replicationFactor;
        private Map<String, String> configs = new HashMap<>();
    }
}