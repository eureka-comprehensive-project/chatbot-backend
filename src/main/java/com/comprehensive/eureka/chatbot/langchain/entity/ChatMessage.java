package com.comprehensive.eureka.chatbot.langchain.entity;

import com.comprehensive.eureka.chatbot.chatroom.entity.ChatRoom;
import jakarta.persistence.*;
import lombok.Data;

@Entity
@Data
public class ChatMessage {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long userId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "chat_room_id")
    private ChatRoom chatRoom;

    @Lob
    private String message;

    private boolean isBot;

    private Long timestamp;
}
