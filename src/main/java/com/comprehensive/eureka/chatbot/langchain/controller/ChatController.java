package com.comprehensive.eureka.chatbot.langchain.controller;

import com.comprehensive.eureka.chatbot.common.dto.BaseResponseDto;
import com.comprehensive.eureka.chatbot.langchain.dto.*;
import com.comprehensive.eureka.chatbot.langchain.service.ChatService;
import com.fasterxml.jackson.core.JsonProcessingException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/chatbot/api/chat")
@RequiredArgsConstructor
@Slf4j
public class ChatController {

    private final ChatService chatService;

    // 채팅 보내기
    @PostMapping
    public ResponseEntity<BaseResponseDto<ChatResponseDto>> generateChatReply(@RequestBody ChatRequestDto request) throws JsonProcessingException {
        ChatResponseDto chatResponseDto = chatService.generateReply(request.getUserId(), request.getChatRoomId(), request.getMessage());
        return ResponseEntity.ok(BaseResponseDto.success(chatResponseDto));
    }

    // 채팅 내역 불러오기
    @PostMapping("/history")
    public ResponseEntity<BaseResponseDto<List<ChatHistoryResponseDto>>> getChatHistory(
            @RequestBody ChatHistoryRequestDto requestDto
    ) {
        List<ChatHistoryResponseDto> chatHistory = chatService.getChatHistory(requestDto);
        return ResponseEntity.ok(BaseResponseDto.success(chatHistory));
    }

    // 채팅 메시지 조회
    @PostMapping("/message")
    public BaseResponseDto<ChatMessageDetailResponseDto> getMessageDetail(
            @RequestBody ChatMessageRequestDto request
    ) {

        ChatMessageDetailResponseDto detail = chatService.getChatMessageDetail(request.getId());
        return BaseResponseDto.success(detail);
    }
}
