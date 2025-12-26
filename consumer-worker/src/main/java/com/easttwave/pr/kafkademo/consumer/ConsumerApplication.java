package com.easttwave.pr.kafkademo.consumer;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;

@SpringBootApplication(scanBasePackages = {
    "com.easttwave.pr.kafkademo.consumer",
    "com.easttwave.pr.kafkademo.platform"
})
@ConfigurationPropertiesScan("com.easttwave.pr.kafkademo.platform.kafka.infrastructure.config")
public class ConsumerApplication {

    public static void main(String[] args) {
        SpringApplication.run(ConsumerApplication.class, args);
    }

}
