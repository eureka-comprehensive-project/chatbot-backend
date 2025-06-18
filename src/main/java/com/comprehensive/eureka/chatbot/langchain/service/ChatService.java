package com.comprehensive.eureka.chatbot.langchain.service;

import com.comprehensive.eureka.chatbot.langchain.dto.*;
import com.fasterxml.jackson.core.JsonProcessingException;

import java.util.List;

public interface ChatService {

    ChatResponseDto generateReply(Long userId, Long chatRoomId, String message) throws JsonProcessingException;
    List<ChatHistoryResponseDto> getChatHistory(ChatHistoryRequestDto request);
    ChatMessageDetailResponseDto getChatMessageDetail(Long messageId);
}
