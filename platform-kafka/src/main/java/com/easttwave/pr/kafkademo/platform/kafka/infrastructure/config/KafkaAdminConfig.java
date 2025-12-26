package com.easttwave.pr.kafkademo.platform.kafka.infrastructure.config;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.admin.AdminClient;
import org.apache.kafka.clients.admin.AdminClientConfig;
import org.apache.kafka.clients.admin.NewTopic;
import org.apache.kafka.clients.admin.TopicDescription;
import org.apache.kafka.common.KafkaFuture;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.event.EventListener;
import org.springframework.kafka.config.TopicBuilder;
import org.springframework.kafka.core.KafkaAdmin;

import java.util.Collections;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.concurrent.ExecutionException;

@Slf4j
@Configuration
@RequiredArgsConstructor
public class KafkaAdminConfig {

    private final KafkaProperties kafkaProperties;

    @Bean
    public KafkaAdmin kafkaAdmin() {
        Map<String, Object> configs = Map.of(
                AdminClientConfig.BOOTSTRAP_SERVERS_CONFIG, kafkaProperties.getBootstrapServers(),
                AdminClientConfig.CLIENT_ID_CONFIG, kafkaProperties.getClientId() + "-admin"
        );
        return new KafkaAdmin(configs);
    }

    @Bean
    public NewTopic kafkaDemoTopic() {
        return TopicBuilder.name(kafkaProperties.getTopic().getName())
                .partitions(kafkaProperties.getTopic().getPartitions())
                .replicas(kafkaProperties.getTopic().getReplicationFactor())
                .configs(kafkaProperties.getTopic().getConfigs())
                .build();
    }

    @Bean
    public AdminClient adminClient() {
        Properties props = new Properties();
        props.put(AdminClientConfig.BOOTSTRAP_SERVERS_CONFIG, kafkaProperties.getBootstrapServers());
        props.put(AdminClientConfig.CLIENT_ID_CONFIG, kafkaProperties.getClientId() + "-admin-client");
        return AdminClient.create(props);
    }

    @EventListener(ApplicationReadyEvent.class)
    public void initializeTopics() {
        try (AdminClient adminClient = adminClient()) {
            String topicName = kafkaProperties.getTopic().getName();

            // 토픽 존재 여부 확인
            Set<String> existingTopics = adminClient.listTopics().names().get();

            if (!existingTopics.contains(topicName)) {
                log.info("Creating topic: {}", topicName);
                NewTopic newTopic = kafkaDemoTopic();
                adminClient.createTopics(Collections.singletonList(newTopic)).all().get();
                log.info("Topic {} created successfully", topicName);
            } else {
                log.info("Topic {} already exists", topicName);

                // 토픽 정보 조회
                KafkaFuture<TopicDescription> topicDescription =
                    adminClient.describeTopics(Collections.singletonList(topicName))
                              .topicNameValues()
                              .get(topicName);

                TopicDescription description = topicDescription.get();
                log.info("Topic {} has {} partitions", topicName, description.partitions().size());
            }

        } catch (InterruptedException | ExecutionException e) {
            log.error("Failed to initialize topics", e);
            Thread.currentThread().interrupt();
        }
    }
}
