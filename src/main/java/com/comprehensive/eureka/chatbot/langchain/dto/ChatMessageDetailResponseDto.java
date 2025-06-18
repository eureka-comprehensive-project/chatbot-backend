package com.comprehensive.eureka.chatbot.langchain.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ChatMessageDetailResponseDto {
    private Long id;
    private String message;
    private Long sentAt;
}