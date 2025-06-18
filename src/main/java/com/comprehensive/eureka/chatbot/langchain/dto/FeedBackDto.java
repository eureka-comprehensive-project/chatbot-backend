package com.comprehensive.eureka.chatbot.langchain.dto;

import lombok.*;

@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class FeedBackDto {

    private Long sentimentCode;
    private Long detailCode;
}