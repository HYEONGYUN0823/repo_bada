package com.a7a7.module.chatbot;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

import java.util.Map;

@RestController
public class ChatController {

    private final ChatbotService chatbotService;

    public ChatController(ChatbotService chatbotService) {
        this.chatbotService = chatbotService;
    }

    @PostMapping("/chat")
    public Mono<Map<String, String>> chat(@RequestBody Map<String, String> request) {
        String userMessage = request.get("message");
        if (userMessage == null || userMessage.trim().isEmpty()) {
            return Mono.just(Map.of("response", "메시지를 입력해주세요."));
        }

        return chatbotService.processUserMessage(userMessage)
                .map(response -> Map.of("response", response))
                .onErrorResume(e -> {
                    System.err.println("ChatbotService 처리 중 오류: " + e.getMessage());
                    return Mono.just(Map.of("response", "죄송합니다. 챗봇 처리 중 오류가 발생했습니다."));
                });
    }
}