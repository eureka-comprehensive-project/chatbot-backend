package com.comprehensive.eureka.chatbot.langchain.service;

public interface ChatService {

    String generateReply(Long userId, Long chatRoomId, String message);
}
