package com.comprehensive.eureka.chatbot.langchain.controller;

import com.comprehensive.eureka.chatbot.langchain.service.ChatService;
import com.comprehensive.eureka.chatbot.langchain.dto.ChatRequestDto;
import com.comprehensive.eureka.chatbot.langchain.dto.ChatResponseDto;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/chatbot/api/chat")
@RequiredArgsConstructor
public class ChatController {

    private final ChatService chatService;

    @PostMapping
    public ChatResponseDto chat(@RequestBody ChatRequestDto request) {
        String reply = chatService.generateReply(request.getUserId(), request.getMessage());
        return new ChatResponseDto(reply);
    }
}
