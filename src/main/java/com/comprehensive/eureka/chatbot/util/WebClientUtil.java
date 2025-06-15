package com.comprehensive.eureka.chatbot.util;

import com.comprehensive.eureka.chatbot.common.dto.BaseResponseDto;
import lombok.RequiredArgsConstructor;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.util.MultiValueMap;
import org.springframework.web.reactive.function.client.WebClient;

@Component
@RequiredArgsConstructor
public class WebClientUtil {
    private final WebClient webClient;

    public <R> R get(String url, Class<R> responseType, String bearerToken) {
        return webClient.get()
                .uri(url)
                .retrieve()
                .bodyToMono(responseType)
                .block();
    }

    public <T, R> BaseResponseDto<R> post(String url, T requestBody, ParameterizedTypeReference<BaseResponseDto<R>> responseType) {
        return webClient.post()
                .uri(url)
                .bodyValue(requestBody)
                .retrieve()
                .bodyToMono(responseType)
                .block();
    }
    public <T> String postSentiment(String url, T requestBody) {
        return webClient.post()
                .uri(url)
                .bodyValue(requestBody)
                .retrieve()
                .bodyToMono(String.class)
                .block();
    }


    public <T, R> BaseResponseDto<R> put(String url, T requestBody, ParameterizedTypeReference<BaseResponseDto<R>> responseType) {
        return webClient.put()
                .uri(url)
                .bodyValue(requestBody)
                .retrieve()
                .bodyToMono(responseType)
                .block();
    }

    public <R> R postFormUrlEncoded(String url, MultiValueMap<String, String> formData, Class<R> responseType) {
        return webClient.post()
                .uri(url)
                .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_FORM_URLENCODED_VALUE)
                .bodyValue(formData)
                .retrieve()
                .bodyToMono(responseType)
                .block();
    }
}