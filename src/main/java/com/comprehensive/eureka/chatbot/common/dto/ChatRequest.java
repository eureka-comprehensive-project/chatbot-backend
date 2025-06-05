package com.comprehensive.eureka.chatbot.common.dto;

import lombok.Data;

@Data
public class ChatRequest {

    private String userId;
    private String message;
}
