package com.comprehensive.eureka.chatbot.langchain.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class UserPreferenceDto {

    private Integer preferenceDataUsage;
    private String preferenceDataUsageUnit;
    private Integer preferenceSharedDataUsage;
    private String preferenceSharedDataUsageUnit;
    private Integer preferencePrice;
    private Long preferenceBenefitGroupId;
    private boolean isPreferenceFamilyData;
    private Integer preferenceValueAddedCallUsage;
}