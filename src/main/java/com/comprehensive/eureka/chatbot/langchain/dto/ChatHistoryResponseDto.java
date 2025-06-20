package com.comprehensive.eureka.chatbot.langchain.dto;

import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ChatHistoryResponseDto {
    private Long messageId;
    private String content;
    private Long senderUserId;
    private Long chatRoomId;
    private boolean isBot;
    private LocalDateTime timestamp;
    private boolean isRecommended;
    private boolean isPlanShow;
    private String recommendReason;
}
