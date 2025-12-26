package com.easttwave.pr.messagingdemo.producer.message.adapter.web.dto;

public record MessageWebRequest(
    Long id,
    String topic,
    String message
) {}
