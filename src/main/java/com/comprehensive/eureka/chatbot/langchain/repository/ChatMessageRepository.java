package com.comprehensive.eureka.chatbot.langchain.repository;

import com.comprehensive.eureka.chatbot.langchain.entity.ChatMessage;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ChatMessageRepository extends JpaRepository<ChatMessage, Long> {
    ChatMessage findTopByOrderByIdDesc();
}
