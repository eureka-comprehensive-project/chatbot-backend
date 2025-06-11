package com.comprehensive.eureka.chatbot.prompt.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PromptDto {

    private Long promptId;
    private int sentimentCode;     // 감정 코드 => 0: "공포", 1: "놀람", 2: "분노", 3: "슬픔", 4: "중립", 5: "행복", 6: "혐오"
    private String name;
    private String scenario;        // 해당 감정에 맞는 프롬프트 시나리오
}
