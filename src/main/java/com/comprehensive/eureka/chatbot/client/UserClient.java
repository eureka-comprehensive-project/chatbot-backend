package com.comprehensive.eureka.chatbot.client;

import com.comprehensive.eureka.chatbot.badword.dto.request.UserForbiddenWordsChatCreateRequestDto;
import com.comprehensive.eureka.chatbot.client.dto.request.GetByIdRequestDto;
import com.comprehensive.eureka.chatbot.client.dto.response.GetUserProfileDetailResponseDto;
import com.comprehensive.eureka.chatbot.common.dto.BaseResponseDto;
import com.comprehensive.eureka.chatbot.constant.DomainConstant;
import com.comprehensive.eureka.chatbot.util.WebClientUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class UserClient {
    private final WebClientUtil webClientUtil;

    public BaseResponseDto<GetUserProfileDetailResponseDto> getUserProfile(GetByIdRequestDto getByIdRequestDto) {
        String url = DomainConstant.USER_DOMAIN;
        return webClientUtil.post(
                url+"/user/profile",
                getByIdRequestDto,
                new ParameterizedTypeReference<>() {
                }
        );
    }

    public BaseResponseDto<Object> get(UserForbiddenWordsChatCreateRequestDto userForbiddenWordsChatCreateRequestDto) {
        String url = DomainConstant.USER_DOMAIN;
        return webClientUtil.post(
                url+"/user/profile",
                userForbiddenWordsChatCreateRequestDto,
                new ParameterizedTypeReference<>() {
                }
        );
    }

}