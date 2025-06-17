package com.comprehensive.eureka.chatbot.langchain.repository;

import com.comprehensive.eureka.chatbot.langchain.entity.ChatMessage;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ChatMessageRepository extends JpaRepository<ChatMessage, Long> {
    ChatMessage findTopByOrderByIdDesc();

    @Query("SELECT cm FROM ChatMessage cm WHERE cm.chatRoom.chatRoomId = :chatRoomId AND cm.id < :lastMessageId ORDER BY cm.id DESC")
    List<ChatMessage> findPriorMessages(Long chatRoomId, Long lastMessageId, Pageable pageable);

    @Query("SELECT cm FROM ChatMessage cm WHERE cm.chatRoom.chatRoomId = :chatRoomId ORDER BY cm.id DESC")
    List<ChatMessage> findRecentMessages(Long chatRoomId, Pageable pageable);
}
