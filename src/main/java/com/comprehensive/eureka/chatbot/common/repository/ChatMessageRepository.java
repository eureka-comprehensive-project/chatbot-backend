package com.comprehensive.eureka.chatbot.openai.repository;

import com.comprehensive.eureka.chatbot.openai.entity.ChatMessage;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ChatMessageRepository extends JpaRepository<ChatMessage, Long> {
}
