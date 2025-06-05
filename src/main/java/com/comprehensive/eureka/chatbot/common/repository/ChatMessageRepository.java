package com.comprehensive.eureka.chatbot.common.repository;

import com.comprehensive.eureka.chatbot.common.entity.ChatMessage;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ChatMessageRepository extends JpaRepository<ChatMessage, Long> {
}
