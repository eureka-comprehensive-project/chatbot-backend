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
public class CreateChatRoomResponseDto {
    private Long chatRoomId;
    private LocalDateTime createdAt;
}
