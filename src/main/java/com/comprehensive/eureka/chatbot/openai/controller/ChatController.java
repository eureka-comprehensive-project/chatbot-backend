package com.comprehensive.eureka.chatbot.openai.controller;

import com.comprehensive.eureka.chatbot.openai.service.ChatService;
import com.comprehensive.eureka.chatbot.common.dto.ChatRequest;
import com.comprehensive.eureka.chatbot.common.dto.ChatResponse;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.util.ContentCachingRequestWrapper;

import java.nio.charset.StandardCharsets;

@RestController
@RequestMapping("/api/chat")
@RequiredArgsConstructor
public class ChatController {

    private final ChatService chatService;

    @PostMapping
    public ChatResponse chat(HttpServletRequest httpRequest) throws JsonProcessingException {
        ContentCachingRequestWrapper requestWrapper = (ContentCachingRequestWrapper) httpRequest;
        String body = new String(requestWrapper.getContentAsByteArray(), StandardCharsets.UTF_8);
        ObjectMapper objectMapper = new ObjectMapper();
        ChatRequest request = objectMapper.readValue(body, ChatRequest.class);

        String reply = chatService.generateReply(request.getUserId(), request.getMessage());
        return new ChatResponse(reply);
    }
}
