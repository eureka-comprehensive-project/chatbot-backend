package com.comprehensive.eureka.chatbot.langchain.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ChatResponseDto {
    public static ChatResponseDto of(String message,Long chatRoomId,Long userId){
        return ChatResponseDto.builder()
                .message(message)
                .timestamp(LocalDateTime.now())
                .chatRoomId(chatRoomId)
                .userId(userId)
                .isBot(true)
                .build();
    }
    private Long messageId;
    private Long userId;
    private Long chatRoomId;
    private String message;
    private boolean isBot;
    private LocalDateTime timestamp;
    private Boolean isRecommended;
    private String recommendationReason;
}