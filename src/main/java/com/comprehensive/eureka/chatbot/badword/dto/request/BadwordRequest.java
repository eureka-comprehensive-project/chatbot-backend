package com.comprehensive.eureka.chatbot.badword.dto.request;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
public class BadwordRequest {
    private String badword;
    private List<String> badwords;
}
