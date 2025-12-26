package com.easttwave.pr.kafkademo.producer;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;

@SpringBootApplication(scanBasePackages = {
    "com.easttwave.pr.kafkademo.producer",
    "com.easttwave.pr.kafkademo.platform"
})
@ConfigurationPropertiesScan("com.easttwave.pr.kafkademo.platform.config")
public class ProducerApplication {

    public static void main(String[] args) {
        SpringApplication.run(ProducerApplication.class, args);
    }

}
