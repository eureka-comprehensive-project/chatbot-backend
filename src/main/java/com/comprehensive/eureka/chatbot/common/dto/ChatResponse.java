package com.comprehensive.eureka.chatbot.openai.dto;

import lombok.Data;

@Data
public class ChatResponse {

    private String response;

    public ChatResponse(String response) {
        this.response = response;
    }
}
