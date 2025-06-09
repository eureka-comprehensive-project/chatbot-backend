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
