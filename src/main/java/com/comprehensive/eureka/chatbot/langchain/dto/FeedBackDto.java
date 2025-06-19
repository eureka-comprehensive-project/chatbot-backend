package com.comprehensive.eureka.chatbot.langchain.dto;

import lombok.*;

@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class FeedBackDto {
    private String keyword;
    private Long sentimentCode;
    private Long detailCode;
}