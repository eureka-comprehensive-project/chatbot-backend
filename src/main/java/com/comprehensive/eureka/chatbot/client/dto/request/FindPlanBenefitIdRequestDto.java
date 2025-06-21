package com.comprehensive.eureka.chatbot.client.dto.request;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class FindPlanBenefitIdRequestDto {
    private Long planId;
    private Long benefitGroupId;
}
