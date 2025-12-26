package com.easttwave.pr.messagingdemo.producer;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;

@SpringBootApplication(scanBasePackages = {
    "com.easttwave.pr.messagingdemo.producer",
    "com.easttwave.pr.messagingdemo.platform"
})
@ConfigurationPropertiesScan("com.easttwave.pr.messagingdemo.platform.config")
public class ProducerApplication {

    public static void main(String[] args) {
        SpringApplication.run(ProducerApplication.class, args);
    }

}
