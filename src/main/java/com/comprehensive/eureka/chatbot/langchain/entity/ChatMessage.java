package com.comprehensive.eureka.chatbot.langchain.entity;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Data
public class ChatMessage {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long userId;

    @Lob
    private String message;

    private boolean isBot;

    private Long timestamp;
}
