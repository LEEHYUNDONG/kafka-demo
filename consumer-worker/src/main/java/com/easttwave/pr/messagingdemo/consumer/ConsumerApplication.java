package com.easttwave.pr.messagingdemo.consumer;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;

@SpringBootApplication(scanBasePackages = {
    "com.easttwave.pr.messagingdemo.consumer",
    "com.easttwave.pr.messagingdemo.platform"
})
@ConfigurationPropertiesScan("com.easttwave.pr.messagingdemo.platform.kafka.infrastructure.config")
public class ConsumerApplication {

    public static void main(String[] args) {
        SpringApplication.run(ConsumerApplication.class, args);
    }

}
