package com.comprehensive.eureka.chatbot.client;

import com.comprehensive.eureka.chatbot.client.dto.request.GetByIdRequestDto;
import com.comprehensive.eureka.chatbot.client.dto.request.LoginUserRequestDto;
import com.comprehensive.eureka.chatbot.client.dto.response.GetUserProfileDetailResponseDto;
import com.comprehensive.eureka.chatbot.client.dto.response.LoginUserResponseDto;
import com.comprehensive.eureka.chatbot.common.dto.BaseResponseDto;
import com.comprehensive.eureka.chatbot.constant.DomainConstant;
import com.comprehensive.eureka.chatbot.util.WebClientUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class AuthClient {
    private final WebClientUtil webClientUtil;

    public BaseResponseDto<LoginUserResponseDto> verifyPassword(LoginUserRequestDto requestDto) {
        String url = DomainConstant.AUTH_DOMAIN;
        log.info(url + "/auth/validateUser");
        return webClientUtil.post(
                url+"/auth/validateUser",
                requestDto,
                new ParameterizedTypeReference<>() {
                }
        );
    }
}
