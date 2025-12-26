package com.easttwave.pr.kafkademo.producer.message.adapter.web.dto;

public record MessageWebRequest(
    Long id,
    String topic,
    String message
) {}
