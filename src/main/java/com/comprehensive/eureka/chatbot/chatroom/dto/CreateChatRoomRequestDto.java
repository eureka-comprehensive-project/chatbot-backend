package com.comprehensive.eureka.chatbot.chatroom.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
public class CreateChatRoomRequestDto {
    private Long userId;
}
