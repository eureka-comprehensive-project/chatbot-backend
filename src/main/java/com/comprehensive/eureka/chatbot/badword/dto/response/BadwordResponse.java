package com.comprehensive.eureka.chatbot.badword.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class BadwordResponse {
    private String word;
    private String message;
}
