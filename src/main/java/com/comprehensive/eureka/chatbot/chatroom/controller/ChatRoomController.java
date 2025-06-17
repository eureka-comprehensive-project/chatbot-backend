package com.comprehensive.eureka.chatbot.chatroom.controller;

import com.comprehensive.eureka.chatbot.chatroom.dto.ChatRoomListRequestDto;
import com.comprehensive.eureka.chatbot.chatroom.dto.ChatRoomListResponseDto;
import com.comprehensive.eureka.chatbot.chatroom.dto.CreateChatRoomRequestDto;
import com.comprehensive.eureka.chatbot.chatroom.dto.CreateChatRoomResponseDto;
import com.comprehensive.eureka.chatbot.chatroom.service.ChatRoomService;
import com.comprehensive.eureka.chatbot.common.dto.BaseResponseDto;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/chatbot")
@RequiredArgsConstructor
public class ChatRoomController {
    private final ChatRoomService chatRoomService;

    @PostMapping("/chat-room-list")
    public BaseResponseDto<ChatRoomListResponseDto> getChatRoomList(@RequestBody ChatRoomListRequestDto requestDto) {
        ChatRoomListResponseDto result = chatRoomService.getChatRoomList(
                requestDto.getUserId(),
                requestDto.getChatRoomId(),
                requestDto.getSize()
        );
        return BaseResponseDto.success(result);
    }

    @PostMapping("/create-chat-room")
    public BaseResponseDto<CreateChatRoomResponseDto> createChatRoom(@RequestBody CreateChatRoomRequestDto request) {
        CreateChatRoomResponseDto result = chatRoomService.createChatRoom(request.getUserId());
        return BaseResponseDto.success(result);
    }
}
