package com.comprehensive.eureka.chatbot.chatroom.repository;

import com.comprehensive.eureka.chatbot.chatroom.entity.ChatRoom;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ChatRoomRepository extends JpaRepository<ChatRoom, Long> {

    @Query("SELECT r FROM ChatRoom r " +
            "WHERE r.userId = :userId " +
            "AND (:lastChatRoomId IS NULL OR r.chatRoomId < :lastChatRoomId) " +
            "ORDER BY r.chatRoomId DESC")
    List<ChatRoom> findByUserIdWithPaging(@Param("userId") Long userId,
                                          @Param("lastChatRoomId") Long lastChatRoomId,
                                          Pageable pageable);
}
