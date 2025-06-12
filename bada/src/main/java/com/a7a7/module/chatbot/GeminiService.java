package com.a7a7.module.chatbot;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import com.a7a7.common.config.ApiKeysConfig;
import com.fasterxml.jackson.databind.JsonNode;

import reactor.core.publisher.Mono;

@Service
public class GeminiService {

//    @Value("${gemini_api_key}")
//    private String geminiApiKey;
	
	@Autowired
	ApiKeysConfig apiKeysConfig;

    private final WebClient webClient;

    public GeminiService(WebClient.Builder webClientBuilder) {
        this.webClient = webClientBuilder.baseUrl("https://generativelanguage.googleapis.com/v1beta")
                                       .build();
    }

    public Mono<String> askGemini(String prompt) {
        Map<String, Object> requestBody = new HashMap<>();
        Map<String, Object> part = new HashMap<>();
        part.put("text", prompt);
        Map<String, Object> content = new HashMap<>();
        content.put("parts", Collections.singletonList(part));
        requestBody.put("contents", Collections.singletonList(content));

        String geminiApiKey = apiKeysConfig.getGeminiApiKey();
        String uri = String.format("/models/gemini-2.0-flash:generateContent?key=%s", geminiApiKey);

        return webClient.post()
                .uri(uri)
                .bodyValue(requestBody)
                .retrieve()
                .bodyToMono(String.class)
                .map(this::parseGeminiResponse)
                .onErrorResume(e -> {
                    System.err.println("Gemini API 호출 오류: " + e.getMessage());
                    return Mono.just("Gemini API 호출 중 오류가 발생했습니다.");
                });
    }

    // Gemini API 응답에서 텍스트만 추출하는 메서드
    private String parseGeminiResponse(String jsonResponse) {
        try {
            com.fasterxml.jackson.databind.ObjectMapper mapper = new com.fasterxml.jackson.databind.ObjectMapper();
            JsonNode root = mapper.readTree(jsonResponse);

            JsonNode textNode = root.path("candidates").path(0).path("content").path("parts").path(0).path("text");

            if (textNode.isTextual()) {
                return textNode.asText();
            } else {
                System.err.println("Gemini 응답에서 텍스트를 찾을 수 없거나 형식이 올바르지 않습니다: " + jsonResponse);
                return "Gemini 응답 형식이 올바르지 않습니다.";
            }
        } catch (Exception e) {
            System.err.println("Gemini 응답 파싱 오류: " + e.getMessage() + "\n원본 응답: " + jsonResponse);
            return "Gemini 응답 파싱 중 오류가 발생했습니다.";
        }
    }
}