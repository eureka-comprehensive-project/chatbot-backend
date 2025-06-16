package com.comprehensive.eureka.chatbot.langchain.entity.enums;

public enum DataPeriod {
    DAY("일"),
    MONTH("월");

    DataPeriod(String label) {}

    public DataPeriod[] getAllDataPeriod() {
        return DataPeriod.values();
    }
}