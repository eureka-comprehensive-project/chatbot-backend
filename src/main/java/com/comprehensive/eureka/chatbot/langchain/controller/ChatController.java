package com.comprehensive.eureka.chatbot.langchain.controller;

import com.comprehensive.eureka.chatbot.langchain.service.ChatService;
import com.comprehensive.eureka.chatbot.langchain.dto.ChatRequestDto;
import com.comprehensive.eureka.chatbot.langchain.dto.ChatResponseDto;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import dev.langchain4j.model.chat.request.ChatRequest;
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
// @RequestMapping("/api/chat")
@RequestMapping("/chatbot/api/chat")
@RequiredArgsConstructor
public class ChatController {

    private final ChatService chatService;

//    @PostMapping
//    public ChatResponseDto chat(@RequestBody ChatRequestDto request) {
//        String reply = chatService.generateReply(request.getUserId(), request.getMessage());
//        return new ChatResponseDto(reply);
//    }
    @PostMapping
     public ChatResponseDto chat(HttpServletRequest httpRequest) throws JsonProcessingException {
         ContentCachingRequestWrapper requestWrapper = (ContentCachingRequestWrapper) httpRequest;
         String body = new String(requestWrapper.getContentAsByteArray(), StandardCharsets.UTF_8);
         ObjectMapper objectMapper = new ObjectMapper();
         ChatRequestDto requestDto = objectMapper.readValue(body, ChatRequestDto.class);

         String reply = chatService.generateReply(requestDto.getUserId(), requestDto.getMessage());
         return new ChatResponseDto(reply);
     }

}
