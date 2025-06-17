package com.comprehensive.eureka.chatbot.chatroom.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ChatRoomInfoDto {
    private Long chatRoomId;
    private Long userId;
    private LocalDateTime createdAt;
    private String firstMessage;
}
