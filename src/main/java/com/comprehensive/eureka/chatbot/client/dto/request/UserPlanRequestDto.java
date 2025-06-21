package com.comprehensive.eureka.chatbot.client.dto.request;

import lombok.Builder;
import lombok.Data;

@Builder
@Data
public class UserPlanRequestDto {
    Long userId;
    Long planBenefitId;
}
