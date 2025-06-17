package com.comprehensive.eureka.chatbot.langchain.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ChatHistoryResponseDto {
    private Long messageId;
    private String content;
    private Long senderUserId;
    private Long chatRoomId;
    private boolean isBot;
    private LocalDateTime timestamp;
}
