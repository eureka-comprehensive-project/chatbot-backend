package com.comprehensive.eureka.chatbot.client;

import com.comprehensive.eureka.chatbot.badword.dto.request.UserForbiddenWordsChatCreateRequestDto;
import com.comprehensive.eureka.chatbot.constant.DomainConstant;
import com.comprehensive.eureka.chatbot.util.WebClientUtil;
import com.comprehensive.eureka.chatbot.common.dto.BaseResponseDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

@Component
@RequiredArgsConstructor
@Slf4j
public class AdminClient {
    private final WebClientUtil webClientUtil;

    public BaseResponseDto<Object> insertForbiddenWordRecord(UserForbiddenWordsChatCreateRequestDto userForbiddenWordsChatCreateRequestDto) {
        String url = DomainConstant.ADMIN_DOMAIN;
        log.info("url:"+ url);
        return webClientUtil.post(
                url+"/admin/forbidden-words/chats",
                userForbiddenWordsChatCreateRequestDto,
                new ParameterizedTypeReference<>() {
                }
        );
    }
}