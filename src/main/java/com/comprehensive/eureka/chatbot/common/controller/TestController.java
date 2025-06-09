package com.comprehensive.eureka.chatbot.common.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class TestController {

    @GetMapping("/chatbot/hello")
    public String hello() {
        return "Hello World";
    }
}
