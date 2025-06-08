package com.comprehensive.eureka.chatbot.langchain.dto;

import lombok.Data;

@Data
public class ChatRequestDto {

    private Long userId;
    private String message;
}
