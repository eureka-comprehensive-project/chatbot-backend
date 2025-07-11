package com.comprehensive.eureka.chatbot.client.dto;


import java.util.List;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class PlanDto {

    private Integer planId;
    private String planName;
    private String planCategory;
    private Integer dataAllowance;
    private String dataAllowanceUnit;
    private DataPeriod dataPeriod;
    private Integer tetheringDataAmount;
    private String tetheringDataUnit;
    private Integer familyDataAmount;
    private String familyDataUnit;
    private Integer voiceAllowance;
    private Integer additionalCallAllowance;
    private Integer monthlyFee;
    private List<Long> benefitIdList;
}