package com.comprehensive.eureka.chatbot.chatroom.service;

import com.comprehensive.eureka.chatbot.chatroom.dto.ChatRoomListResponseDto;
import com.comprehensive.eureka.chatbot.chatroom.dto.CreateChatRoomResponseDto;

public interface ChatRoomService {
    ChatRoomListResponseDto getChatRoomList(Long userId, Long chatRoomId, int size);
    CreateChatRoomResponseDto createChatRoom(Long userId);
}
