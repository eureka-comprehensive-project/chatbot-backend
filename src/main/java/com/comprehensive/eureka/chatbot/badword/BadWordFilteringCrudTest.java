package com.comprehensive.eureka.chatbot.badword;

import com.vane.badwordfiltering.BadWordFiltering;

import java.util.*;

public class BadWordFilteringCrudTest {
    public static void main(String[] args) {
        BadWordFiltering badWordFilter = new BadWordFiltering();

        System.out.println("씨발은 욕설 : " + badWordFilter.check("씨발"));
        //check 함수의 동작
        System.out.println("안녕은 욕설 : " + badWordFilter.check("안녕"));

        System.out.println("새로운 나쁜말을 욕설 리스트에 추가");
        badWordFilter.add("새로운 나쁜말");
        System.out.println("새로운 나쁜말은 욕설 : " + badWordFilter.check("새로운 나쁜말"));

        System.out.println("새로운 나쁜말을 욕설 리스트에서 삭제");
        badWordFilter.remove("새로운 나쁜말");
        System.out.println("새로운 나쁜말은 욕설 : " + badWordFilter.check("새로운 나쁜말"));


        String[] words = {"bad", "worse", "새로운 나쁜말 2"};
        badWordFilter.addAll(Arrays.asList(words)); 
        
        Set<String> set = Set.of("horrible", "nasty");
        badWordFilter.addAll(set);

        System.out.println("리스트로 추가한 새로운 나쁜말2는 욕설 : " + badWordFilter.check("새로운 나쁜말 2"));

        for (String word : badWordFilter) {
            System.out.println(word);
        }
    }
}
