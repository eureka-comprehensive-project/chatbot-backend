package com.comprehensive.eureka.chatbot.chatroom.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ChatRoom {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="chat_room_id")
    private Long chatRoomId;

    @Column(name="user_id", nullable = false)
    private Long userId;

    @Column(name="created_at")
    private LocalDateTime createdAt;
}