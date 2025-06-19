package com.comprehensive.eureka.chatbot.client.dto.request;

import lombok.*;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder

public class PlanFilterRequestDto {
    @Builder.Default
    private List<Long> categoryIds = null;
    @Builder.Default
    private boolean allCategoriesSelected = false;
    @Builder.Default
    private List<String> priceRanges = null;
    @Builder.Default
    private boolean anyPriceSelected = true;
    @Builder.Default
    private List<String> dataOptions = null;
    @Builder.Default
    private boolean anyDataSelected = true;
    @Builder.Default
    private List<Long> benefitIds = null;
    @Builder.Default
    private boolean noBenefitsSelected = true;
}