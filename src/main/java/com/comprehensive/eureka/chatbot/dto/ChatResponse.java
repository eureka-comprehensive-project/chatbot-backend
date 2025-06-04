package com.comprehensive.eureka.chatbot.dto;

import lombok.Data;

@Data
public class ChatResponse {

    private String response;

    public ChatResponse(String response) {
        this.response = response;
    }
}
