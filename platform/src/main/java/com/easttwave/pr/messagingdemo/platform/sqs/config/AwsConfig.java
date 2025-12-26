package com.easttwave.pr.messagingdemo.platform.sqs.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import software.amazon.awssdk.auth.credentials.AwsCredentialsProvider;
import software.amazon.awssdk.auth.credentials.ProfileCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.sqs.SqsClient;

@Configuration
public class AwsConfig {

    @Value("${spring.cloud.aws.credentials.profile-name}")
    private String profileName;

    @Bean
    public AwsCredentialsProvider awsCredentialsProvider() {
        return ProfileCredentialsProvider.create(profileName);
    }

    @Bean
    public SqsClient sqsClient(AwsCredentialsProvider credentialsProvider) {
        return SqsClient.builder()
                .credentialsProvider(credentialsProvider)
                .region(Region.AP_NORTHEAST_2)
                .build();
    }
}
