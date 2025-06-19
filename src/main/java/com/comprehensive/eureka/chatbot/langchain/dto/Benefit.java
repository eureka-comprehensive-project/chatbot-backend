package com.comprehensive.eureka.chatbot.langchain.dto;

import com.comprehensive.eureka.chatbot.langchain.entity.enums.BenefitCategory;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Benefit {
    private Long id;
    private String name;
    private BenefitCategory category;
    private List<String> details;
}
