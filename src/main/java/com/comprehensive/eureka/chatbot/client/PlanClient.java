package com.comprehensive.eureka.chatbot.client;

import com.comprehensive.eureka.chatbot.client.dto.request.PlanFilterRequestDto;
import com.comprehensive.eureka.chatbot.client.dto.response.FilterListResponseDto;
import com.comprehensive.eureka.chatbot.common.dto.BaseResponseDto;
import com.comprehensive.eureka.chatbot.common.exception.DomainException;
import com.comprehensive.eureka.chatbot.common.exception.ErrorCode;
import com.comprehensive.eureka.chatbot.constant.DomainConstant;
import com.comprehensive.eureka.chatbot.langchain.dto.BenefitRequestDto;
import com.comprehensive.eureka.chatbot.langchain.dto.PlanDto;
import com.comprehensive.eureka.chatbot.util.WebClientUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
public class PlanClient {

    private final WebClientUtil webClientUtil;
    public Long getBenefitIds(BenefitRequestDto requestDto) {

        String apiUrl = DomainConstant.PLAN_DOMAIN+"/plan/benefit/benefit-group";

        BaseResponseDto<Long> response = webClientUtil.post(
                apiUrl,
                requestDto,
                new ParameterizedTypeReference<>() {}
        );
        return response.getData();
    }

    public List<PlanDto> getAllPlans() {
        BaseResponseDto<List<PlanDto>> response;
        String apiUrl = DomainConstant.PLAN_DOMAIN+"/plan/";
        log.info("apiUrl : " + apiUrl);
        try{
            response = webClientUtil.get(
                    apiUrl,
                    new ParameterizedTypeReference<>() {}
            );
            log.info("response" + response);
        }catch(Exception e){
            throw new DomainException(ErrorCode.DOMAIN_NOT_CHANGED);
        }

        return response.getData();
    }
    public List<FilterListResponseDto> getPlansByCategoryId(Long categoryId) {
        log.info("category Id : " + categoryId);

        PlanFilterRequestDto planFilterRequestDto = PlanFilterRequestDto.builder()
                .categoryIds(List.of(categoryId))
                .allCategoriesSelected(false) // 명시적으로 false로 설정하는 것이 안전
                .build();

        log.info("plan모듈로 보내는 category id 값: " + planFilterRequestDto.getCategoryIds());

        String apiUrl = DomainConstant.PLAN_DOMAIN + "/plan/filter/list";
        log.info("apiUrl : " + apiUrl);

        try {
            BaseResponseDto<List<FilterListResponseDto>> baseResponse = webClientUtil.post(
                    apiUrl,
                    planFilterRequestDto,
                    new ParameterizedTypeReference<BaseResponseDto<List<FilterListResponseDto>>>() {}
            );

            if (baseResponse == null || baseResponse.getData() == null) {
                throw new DomainException(ErrorCode.DATA_NOT_FOUND); // 필요 시 정의
            }

            log.info("response: " + baseResponse.getData());
            return baseResponse.getData();

        } catch (Exception e) {
            log.error("요금제 필터 요청 실패", e);
            throw new DomainException(ErrorCode.DOMAIN_NOT_CHANGED);
        }
    }

    public List<FilterListResponseDto> getPlansByBenefitsId(Long benefitsId) {
        log.info("benefitsId : " + benefitsId);

        PlanFilterRequestDto planFilterRequestDto = PlanFilterRequestDto.builder()
                .benefitIds(List.of(benefitsId))
                .noBenefitsSelected(false) // 명시적으로 false로 설정하는 것이 안전
                .build();

        log.info("plan모듈로 보내는 category id 값: " + planFilterRequestDto.getBenefitIds());

        String apiUrl = DomainConstant.PLAN_DOMAIN + "/plan/filter/list";
        log.info("apiUrl : " + apiUrl);

        try {
            BaseResponseDto<List<FilterListResponseDto>> baseResponse = webClientUtil.post(
                    apiUrl,
                    planFilterRequestDto,
                    new ParameterizedTypeReference<BaseResponseDto<List<FilterListResponseDto>>>() {}
            );

            if (baseResponse == null || baseResponse.getData() == null) {
                throw new DomainException(ErrorCode.DATA_NOT_FOUND); // 필요 시 정의
            }

            log.info("response: " + baseResponse.getData());
            return baseResponse.getData();

        } catch (Exception e) {
            log.error("요금제 필터 요청 실패", e);
            throw new DomainException(ErrorCode.DOMAIN_NOT_CHANGED);
        }
    }



}