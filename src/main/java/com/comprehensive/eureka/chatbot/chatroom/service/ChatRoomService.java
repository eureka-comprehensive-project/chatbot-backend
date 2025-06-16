package com.comprehensive.eureka.chatbot.chatroom.service;

import com.comprehensive.eureka.chatbot.chatroom.dto.ChatRoomListResponseDto;

public interface ChatRoomService {
    ChatRoomListResponseDto getChatRoomList(Long userId, Long chatRoomId, int size);
}
