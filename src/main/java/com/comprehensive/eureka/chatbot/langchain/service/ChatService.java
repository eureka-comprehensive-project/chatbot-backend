package com.comprehensive.eureka.chatbot.langchain.service;

public interface ChatService {

    String generateReply(Long userId, String message);
}
