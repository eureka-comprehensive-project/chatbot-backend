package com.comprehensive.eureka.chatbot.prompt.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Prompt {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long promptId;

    private int sentimentCode;      // 감정 코드 => 0: "공포", 1: "놀람", 2: "분노", 3: "슬픔", 4: "중립", 5: "행복", 6: "혐오"
    private String name;            // 감정 이름
    private String scenario;        // 해당 감정에 맞는 프롬프트 시나리오
}
