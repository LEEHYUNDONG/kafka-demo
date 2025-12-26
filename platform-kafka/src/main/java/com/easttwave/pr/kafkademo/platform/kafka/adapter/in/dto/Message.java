<<<<<<<< HEAD:src/main/java/com/easttwave/pr/kafkademo/message/kafka/dto/Message.java
package com.easttwave.pr.kafkademo.message.kafka.dto;
========
package com.easttwave.pr.kafkademo.platform.kafka.adapter.in.dto;
>>>>>>>> origin/main:platform-kafka/src/main/java/com/easttwave/pr/kafkademo/platform/kafka/adapter/in/dto/Message.java

public record Message(
        Long id,
        String topic,
        String message
) {
}
