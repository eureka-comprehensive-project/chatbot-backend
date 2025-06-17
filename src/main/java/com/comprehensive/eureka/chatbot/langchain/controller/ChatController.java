package com.comprehensive.eureka.chatbot.langchain.controller;

import com.comprehensive.eureka.chatbot.common.dto.BaseResponseDto;
import com.comprehensive.eureka.chatbot.langchain.dto.ChatHistoryRequestDto;
import com.comprehensive.eureka.chatbot.langchain.dto.ChatHistoryResponseDto;
import com.comprehensive.eureka.chatbot.langchain.service.ChatService;
import com.comprehensive.eureka.chatbot.langchain.dto.ChatRequestDto;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/chatbot/api/chat")
@RequiredArgsConstructor
public class ChatController {

    private final ChatService chatService;

    // 채팅 보내기
    @PostMapping("/reply")
    public ResponseEntity<BaseResponseDto<String>> generateChatReply(@RequestBody ChatRequestDto request) {
        String reply = chatService.generateReply(request.getUserId(), request.getChatRoomId(), request.getMessage());
        return ResponseEntity.ok(BaseResponseDto.success(reply));
    }

    // 채팅 내역 불러오기
    @GetMapping("/history")
    public ResponseEntity<BaseResponseDto<List<ChatHistoryResponseDto>>> getChatHistory(
            @RequestParam Long chatRoomId,
            @RequestParam Long userId,
            @RequestParam(required = false) Long lastMessageId,
            @RequestParam(defaultValue = "10") int pageSize
    ) {
        ChatHistoryRequestDto requestDto = new ChatHistoryRequestDto(chatRoomId, userId, lastMessageId, pageSize);
        List<ChatHistoryResponseDto> chatHistory = chatService.getChatHistory(requestDto);
        return ResponseEntity.ok(BaseResponseDto.success(chatHistory));
    }

}
