package com.comprehensive.eureka.chatbot.langchain.dto;

import lombok.Data;

import java.util.List;

@Data
public class TelecomProfile {

    private int dataUsageGB;
    private int callTimeMin;
    private int smsCount;
    private int age;
    private String gender;
    private List<String> preferredServices;
}
//        preferenceDataUsage": 100,
//        "preferenceDataUsageUnit": "GB",
//        "preferenceSharedDataUsage": 10,
//        "preferenceSharedDataUsageUnit": "GB",
//        "preferencePrice": 69000,
//        "preferenceBenefit": "넷플릭스",
//        "isPreferenceFamilyData": true,
//        "preferenceValueAddedCallUsage": 300