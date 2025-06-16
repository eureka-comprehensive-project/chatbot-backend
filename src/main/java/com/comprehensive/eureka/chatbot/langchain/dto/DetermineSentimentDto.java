package com.comprehensive.eureka.chatbot.langchain.dto;

import lombok.Data;

@Data
public class DetermineSentimentDto {
    String message;
    public DetermineSentimentDto(String message){
        this.message = message;
    }
}
