package com.comprehensive.eureka.chatbot.client.dto.response;


import com.comprehensive.eureka.chatbot.langchain.entity.enums.DataPeriod;
import lombok.*;

import java.util.List;


@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class FilterListResponseDto {

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