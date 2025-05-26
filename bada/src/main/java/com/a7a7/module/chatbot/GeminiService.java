package com.a7a7.module.chatbot;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class GeminiService {

    @Value("${gemini_api_key}") // application.properties에서 Gemini API 키를 가져와요.
    private String geminiApiKey;

    private final WebClient webClient;

    public GeminiService(WebClient.Builder webClientBuilder) {
        // Gemini API 엔드포인트를 기본 URL로 설정해요.
        this.webClient = webClientBuilder.baseUrl("https://generativelanguage.googleapis.com/v1beta")
                                       .build();
    }

    // Gemini 모델에 질문을 보내고 응답을 받는 메서드
    public Mono<String> askGemini(String prompt) {
        // 요청 본문(request body)을 JSON 형태로 만들어요.
        // 이 구조는 Gemini API 문서에 따라 정의됩니다.
        Map<String, Object> requestBody = new HashMap<>();
        Map<String, Object> part = new HashMap<>();
        part.put("text", prompt);
        Map<String, Object> content = new HashMap<>();
        content.put("parts", Collections.singletonList(part)); // 단일 텍스트 파트
        requestBody.put("contents", Collections.singletonList(content));

        // API 호출을 위한 URI (모델 이름과 API 키 포함)
        String uri = String.format("/models/gemini-2.0-flash:generateContent?key=%s", geminiApiKey);

        // WebClient를 사용하여 Gemini API에 POST 요청을 보내고 응답을 받아요.
        return webClient.post()
                .uri(uri)
                .bodyValue(requestBody) // 요청 본문 설정
                .retrieve() // 응답을 받아요.
                .bodyToMono(String.class) // 응답 본문을 String 형태로 받아요.
                .map(this::parseGeminiResponse) // 받은 JSON 응답에서 실제 텍스트만 추출해요.
                .onErrorResume(e -> { // 오류 발생 시
                    System.err.println("Gemini API 호출 오류: " + e.getMessage());
                    return Mono.just("Gemini API 호출 중 오류가 발생했습니다.");
                });
    }

    // Gemini API 응답에서 텍스트만 추출하는 메서드
    private String parseGeminiResponse(String jsonResponse) {
        try {
            // ObjectMapper를 사용하여 JSON 문자열을 Map으로 파싱해요.
            // 이 과정은 Gemini API의 JSON 응답 구조에 따라 달라질 수 있습니다.
            // 일반적으로 응답은 candidates[0].content.parts[0].text 경로에 텍스트가 있어요.
            com.fasterxml.jackson.databind.ObjectMapper mapper = new com.fasterxml.jackson.databind.ObjectMapper();
            JsonNode root = mapper.readTree(jsonResponse);

            // 응답 경로에 따라 텍스트를 추출해요.
            JsonNode textNode = root.path("candidates").path(0).path("content").path("parts").path(0).path("text");

            if (textNode.isTextual()) {
                return textNode.asText(); // 텍스트가 있다면 그대로 반환
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