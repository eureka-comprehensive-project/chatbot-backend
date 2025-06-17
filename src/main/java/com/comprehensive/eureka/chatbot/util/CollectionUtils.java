package com.comprehensive.eureka.chatbot.util;

import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import java.util.Map;

public class CollectionUtils {
    public static MultiValueMap<String, String> toMultiValueMap(Map<String, Object> paramMap) {
        MultiValueMap<String, String> result = new LinkedMultiValueMap<>();
        if (paramMap != null) {
            for (Map.Entry<String, Object> entry : paramMap.entrySet()) {
                if (entry.getValue() != null) {
                    result.add(entry.getKey(), entry.getValue().toString());
                }
            }
        }
        return result;
    }
}
