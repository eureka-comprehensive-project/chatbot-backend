package com.comprehensive.eureka.chatbot.badword.dto.request;

import lombok.*;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@Builder
public class UserForbiddenWordsChatCreateRequestDto {
    private Long userId;
    private Long chatMessageId;
    private List<String> forbiddenWords;

}
