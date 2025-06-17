package com.comprehensive.eureka.chatbot.chatroom.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ChatRoomListResponseDto {
    private List<ChatRoomInfoDto> chatRooms;
    private boolean hasNext;
}
