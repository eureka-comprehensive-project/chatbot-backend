package com.comprehensive.eureka.chatbot.badword.dto.request;

import lombok.*;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@Builder
public class BadwordRecordRequest {
    Long userId;
    Long chatMessageId;
    List<String> forbiddenWords;

}
