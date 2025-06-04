package com.comprehensive.eureka.chatbot.Service;

import dev.langchain4j.model.openai.OpenAiChatModel;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ChatServiceImpl implements ChatService {

    private final OpenAiChatModel openAiChatModel;

    @Override
    public String generateReply(String userId, String message) {
        String response = openAiChatModel.chat(message);

        System.out.println("[USER] " + userId + ": " + message);
        System.out.println("[BOT] → " + response);

        return response;
    }
}
