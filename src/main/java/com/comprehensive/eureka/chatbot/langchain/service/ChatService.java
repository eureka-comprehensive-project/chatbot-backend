package com.comprehensive.eureka.chatbot.langchain.service;

import com.comprehensive.eureka.chatbot.langchain.dto.ChatHistoryRequestDto;
import com.comprehensive.eureka.chatbot.langchain.dto.ChatHistoryResponseDto;
import com.comprehensive.eureka.chatbot.langchain.dto.ChatResponseDto;
import com.comprehensive.eureka.chatbot.langchain.dto.PlanDto;
import com.fasterxml.jackson.core.JsonProcessingException;

import java.util.List;

public interface ChatService {

    ChatResponseDto generateReply(Long userId, Long chatRoomId, String message) throws JsonProcessingException;
    List<ChatHistoryResponseDto> getChatHistory(ChatHistoryRequestDto request);
}
