package com.comprehensive.eureka.chatbot.langchain.service;

import com.comprehensive.eureka.chatbot.langchain.dto.ChatHistoryRequestDto;
import com.comprehensive.eureka.chatbot.langchain.dto.ChatHistoryResponseDto;
import com.comprehensive.eureka.chatbot.langchain.dto.PlanDto;

import java.util.List;

public interface ChatService {

    String generateReply(Long userId, Long chatRoomId, String message);
    List<ChatHistoryResponseDto> getChatHistory(ChatHistoryRequestDto request);
}
