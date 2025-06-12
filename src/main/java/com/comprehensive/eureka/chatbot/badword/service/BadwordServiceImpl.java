package com.comprehensive.eureka.chatbot.badword.service;

import com.comprehensive.eureka.chatbot.badword.dto.request.BadwordRequest;
import com.comprehensive.eureka.chatbot.badword.dto.response.BadwordResponse;
import com.vane.badwordfiltering.BadWordFiltering;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class BadwordServiceImpl implements BadwordService {

    private final BadWordFiltering badwordFiltering;

    // CREATE
    @Override
    public BadwordResponse createBadWord(BadwordRequest badwordRequest) {
        System.out.println(badwordRequest.getBadword() + "단어가 금칙어 리스트에 추가 되었습니다.");
        try{
            badwordFiltering.add(badwordRequest.getBadword());
        }catch(Exception e){
            e.printStackTrace();
            System.out.println("chatbot module : 금칙어 리스트에 단어 추가하는 중에 오류가 발생");
        }

        return new BadwordResponse(badwordRequest.getBadword(), "단어가 추가되었습니다.");
    }

    // READ ALL
    @Override
    public List<BadwordResponse> getAllBadWord() {
        List<BadwordResponse> result = new ArrayList<>();
        for (String word : badwordFiltering) {
            result.add(new BadwordResponse(word, "존재하는 필터링 단어입니다."));
        }
        return result;
    }

    // DELETE
    @Override
    public void deleteBadWordResponse(String word) {
        badwordFiltering.remove(word);
    }

    // 추가: 문자열로 삭제
    public BadwordResponse deleteBadwordByString(String word) {
        if (badwordFiltering.remove(word)) {
            return new BadwordResponse(word, "단어가 삭제되었습니다.");
        } else {
            return new BadwordResponse(word, "단어를 찾을 수 없습니다.");
        }
    }
}
