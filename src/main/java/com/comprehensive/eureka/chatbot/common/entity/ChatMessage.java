package com.comprehensive.eureka.chatbot.common.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;

@Entity
@Data
public class ChatMessage {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String email;

    @Lob
    private String message;

    private boolean isBot;
    private LocalDateTime timestamp;
}
