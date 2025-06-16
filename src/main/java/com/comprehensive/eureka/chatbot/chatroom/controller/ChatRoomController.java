package com.comprehensive.eureka.chatbot.chatroom.controller;

import com.comprehensive.eureka.chatbot.chatroom.dto.ChatRoomListResponseDto;
import com.comprehensive.eureka.chatbot.chatroom.service.ChatRoomService;
import com.comprehensive.eureka.chatbot.common.dto.BaseResponseDto;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/chatbot")
@RequiredArgsConstructor
public class ChatRoomController {
    private final ChatRoomService chatRoomService;

    @GetMapping("/chat-room-list")
    public BaseResponseDto<ChatRoomListResponseDto> getChatRoomList(
            @RequestParam Long userId,
            @RequestParam(required = false) Long chatRoomId,
            @RequestParam(defaultValue = "10") int size) {

        ChatRoomListResponseDto result = chatRoomService.getChatRoomList(userId, chatRoomId, size);
        return BaseResponseDto.success(result);
    }

}
