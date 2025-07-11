package com.comprehensive.eureka.chatbot.langchain.repository;

import com.comprehensive.eureka.chatbot.langchain.entity.ChatMessage;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

import java.util.List;

@Repository
public interface ChatMessageRepository extends JpaRepository<ChatMessage, Long> {
    ChatMessage findTopByOrderByIdDesc();

    @Query("SELECT cm FROM ChatMessage cm WHERE cm.chatRoom.chatRoomId = :chatRoomId AND cm.userId = :userId AND cm.id < :lastMessageId ORDER BY cm.timestamp DESC")
    List<ChatMessage> findPriorMessages(
            @Param("chatRoomId") Long chatRoomId,
            @Param("userId") Long userId,
            @Param("lastMessageId") Long lastMessageId,
            Pageable pageable
    );

    @Query("SELECT cm FROM ChatMessage cm WHERE cm.chatRoom.chatRoomId = :chatRoomId AND cm.userId = :userId ORDER BY cm.timestamp DESC")
    List<ChatMessage> findRecentMessages(
            @Param("chatRoomId") Long chatRoomId,
            @Param("userId") Long userId,
            Pageable pageable
    );

    @Query("SELECT m FROM ChatMessage m WHERE m.userId = :userId AND m.isBot = false AND m.timestamp = (" +
            "SELECT MIN(m2.timestamp) FROM ChatMessage m2 WHERE m2.chatRoom.chatRoomId = m.chatRoom.chatRoomId AND m2.userId = :userId AND m2.isBot = false" +
            ") AND m.chatRoom.chatRoomId IN :chatRoomIds")
    List<ChatMessage> findFirstUserMessagesByChatRoomIds(@Param("userId") Long userId,
                                                         @Param("chatRoomIds") List<Long> chatRoomIds);

}
