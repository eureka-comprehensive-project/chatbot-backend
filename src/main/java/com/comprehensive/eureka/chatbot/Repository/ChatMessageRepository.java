package com.comprehensive.eureka.chatbot.Repository;

import com.comprehensive.eureka.chatbot.entity.ChatMessage;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ChatMessageRepository extends JpaRepository<ChatMessage, Long> {
}
