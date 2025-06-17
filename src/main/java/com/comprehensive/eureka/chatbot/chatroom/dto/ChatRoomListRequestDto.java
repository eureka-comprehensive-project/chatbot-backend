package com.comprehensive.eureka.chatbot.chatroom.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ChatRoomListRequestDto {
    private Long userId;
    private Long chatRoomId;
    private int size = 10;
}
