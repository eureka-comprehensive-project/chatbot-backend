package com.comprehensive.eureka.chatbot.openai.service;

public interface ChatService {

    String generateReply(String userId, String message);
}
