package com.comprehensive.eureka.chatbot.langchain.dto;

import lombok.*;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Getter
@Setter
public class ChatResponseDto {
    private Long messageId;
    private Long userId;
    private Long chatRoomId;
    private String message;
    @Builder.Default
    private boolean isBot = false;
    @Builder.Default
    private LocalDateTime timestamp = LocalDateTime.now();
    @Builder.Default
    private Boolean isRecommended = false;
    @Builder.Default
    private Boolean isPlanShow = false;
    @Builder.Default
    private String recommendationReason = "mockReason";

    public static ChatResponseDto of(String message, Long chatRoomId, Long userId) {
        return ChatResponseDto.builder()
                .message(message)
                .timestamp(LocalDateTime.now())
                .chatRoomId(chatRoomId)
                .userId(userId)
                .isBot(true)
                .build();
    }

    public static ChatResponseDto fail(String message, ChatResponseDto chatResponseDto) {
        return ChatResponseDto.builder()
                .message(message)
                .timestamp(LocalDateTime.now())
                .chatRoomId(chatResponseDto.getChatRoomId())
                .userId(chatResponseDto.getUserId())
                .isBot(true)
                .build();
    }
}