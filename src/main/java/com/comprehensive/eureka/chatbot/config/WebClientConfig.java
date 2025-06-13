//package com.comprehensive.eureka.chatbot.config;
//
//import org.springframework.beans.factory.annotation.Qualifier;
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//import org.springframework.web.reactive.function.client.WebClient;
//
//@Configuration
//public class WebClientConfig {
//
//    @Bean
//    @Qualifier("adminClient")
//    public WebClient adminClient(WebClient.Builder builder) {
//        return builder
//                .baseUrl("http://localhost:8086/") // 혹은 환경 변수로 관리
//                .defaultHeader("Content-Type", "application/json")
//                .build();
//    }
//
//}