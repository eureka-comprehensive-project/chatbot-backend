package com.comprehensive.eureka.chatbot.langchain.entity;

import com.comprehensive.eureka.chatbot.chatroom.entity.ChatRoom;
import io.micrometer.common.lang.Nullable;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Data
@Getter @Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
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

    @Nullable
    private boolean isRecommend;
    @Nullable
    private boolean isPlanShow;
    @Nullable
    private String recommendReason;
}