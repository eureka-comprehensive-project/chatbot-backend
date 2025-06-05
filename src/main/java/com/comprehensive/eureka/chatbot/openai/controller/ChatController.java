package com.comprehensive.eureka.chatbot.openai.controller;

import com.comprehensive.eureka.chatbot.openai.service.ChatService;
import com.comprehensive.eureka.chatbot.common.dto.ChatRequest;
import com.comprehensive.eureka.chatbot.common.dto.ChatResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/chat")
@RequiredArgsConstructor
public class ChatController {

    private final ChatService chatService;

    @PostMapping
    public ChatResponse chat(@RequestBody ChatRequest request) {
        String reply = chatService.generateReply(request.getUserId(), request.getMessage());
        return new ChatResponse(reply);
    }
}
