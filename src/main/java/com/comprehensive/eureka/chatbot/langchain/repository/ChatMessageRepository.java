package com.comprehensive.eureka.chatbot.langchain.repository;

import com.comprehensive.eureka.chatbot.langchain.entity.ChatMessage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface ChatMessageRepository extends JpaRepository<ChatMessage, Long> {
    ChatMessage findTopByOrderByIdDesc();

    @Query("SELECT m FROM ChatMessage m WHERE m.chatRoom.chatRoomId = :chatRoomId AND m.userId = :userId AND m.isBot = false ORDER BY m.timestamp ASC")
    Optional<ChatMessage> findFirstUserMessage(@Param("chatRoomId") Long chatRoomId, @Param("userId") Long userId);

}
