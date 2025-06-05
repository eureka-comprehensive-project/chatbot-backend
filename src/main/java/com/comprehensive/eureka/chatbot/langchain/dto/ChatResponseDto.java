package com.comprehensive.eureka.chatbot.langchain.dto;

import lombok.Data;

@Data
public class ChatResponseDto {

    private String response;

    public ChatResponseDto(String response) {
        this.response = response;
    }
}
