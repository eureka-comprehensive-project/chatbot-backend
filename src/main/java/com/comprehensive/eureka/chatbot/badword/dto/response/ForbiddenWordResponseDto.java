package com.comprehensive.eureka.chatbot.badword.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ForbiddenWordResponseDto {
    private Long id;
    private String word;
    private boolean status;
}
