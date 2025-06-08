package com.comprehensive.eureka.chatbot.config;

import jakarta.servlet.Filter;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class FilterConfig {

    @Bean
    public FilterRegistrationBean<Filter> cachingRequestBodyFilter() {
        FilterRegistrationBean<Filter> registrationBean = new FilterRegistrationBean<>();
        registrationBean.setFilter(new CachingRequestBodyFilter());
        registrationBean.setOrder(0); // 필터 우선순위 가장 높게
        registrationBean.addUrlPatterns("/chatbot/api/chat"); // 적용할 URL 패턴 설정
        return registrationBean;
    }
}
