package com.easttwave.pr.kafkademo.common.core.sqs;

import io.awspring.cloud.sqs.config.SqsMessageListenerContainerFactory;
import io.awspring.cloud.sqs.operations.SqsTemplate;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import software.amazon.awssdk.auth.credentials.AwsCredentials;
import software.amazon.awssdk.auth.credentials.AwsCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.sqs.SqsAsyncClient;

@Configuration
@RequiredArgsConstructor
public class AmazonSqsConfig {

    private final AwsCredentialsProvider credentialsProvider;

        // 클라이언트 설정
        @Bean
        public SqsAsyncClient sqsAsyncClient() {
            return SqsAsyncClient.builder()
                    .credentialsProvider(credentialsProvider)
                    .region(Region.AP_NORTHEAST_2)
                    .build();
        }

        // Listener Factory 설정
        @Bean
        public SqsMessageListenerContainerFactory<Object> defaultSqsListenerContainerFactory() {
            return SqsMessageListenerContainerFactory
                    .builder()
                    .sqsAsyncClient(sqsAsyncClient())
                    .build();
        }

        // 메세지 발송을 위한 SQS 템플릿 설정
        @Bean
        public SqsTemplate sqsTemplate() {
            return SqsTemplate.newTemplate(sqsAsyncClient());
        }

}
