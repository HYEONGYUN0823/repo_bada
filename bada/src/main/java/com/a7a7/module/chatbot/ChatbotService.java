package com.a7a7.module.chatbot;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import reactor.core.publisher.Mono;

@Service
public class ChatbotService {

    private final PublicDataService publicDataService;
    private final GeminiService geminiService;
    private final ObjectMapper objectMapper;

    public ChatbotService(PublicDataService publicDataService, GeminiService geminiService, ObjectMapper objectMapper) {
        this.publicDataService = publicDataService;
        this.geminiService = geminiService;
        this.objectMapper = objectMapper;
    }

    public Mono<String> processUserMessage(String userMessage) {
        String intentPrompt = String.format("""
            # 지침
            사용자의 질문을 분석하여 JSON 형식으로 응답해 주세요.
            응답에는 'intents' (의도 배열), 'entities' (핵심 정보 객체), 'api_params' (API 호출용 파라미터 객체)를 포함해야 합니다.
            - 'intents'는 ['숙소_추천', '식당_추천', '날씨_정보_요청', '상세_정보_요청', '인사', '기타'] 중에서 선택하세요.
            - 'api_params'에는 'location' 키가 존재하면 반드시 지역 정보를 포함해야 합니다.
            
            # 규칙
            1. 사용자가 지역명만 언급하거나 의도가 불분명하면 'intents'를 ['숙소_추천']으로 설정하세요.
            2. 사용자가 명확히 '날씨'에 대해 물으면, 다른 의도를 포함하지 말고 **오직 'intents'를 ['날씨_정보_요청']으로만 설정**해야 합니다. 이것이 가장 중요한 규칙입니다.
            3. 이해할 수 없는 질문은 'intents'를 ['기타']로 설정하고, 'response_text'에 "어떤 지역의 숙소를 추천해 드릴까요?"라고 답변하세요.

            # 예시
            - 사용자 질문: "서울 날씨 어때?"
            - JSON 응답: {"intents": ["날씨_정보_요청"], "api_params": {"location": "서울"}}

            - 사용자 질문: "강남 맛집 찾아줘"
            - JSON 응답: {"intents": ["식당_추천"], "api_params": {"location": "강남"}}

            - 사용자 질문: "제주도 숙소"
            - JSON 응답: {"intents": ["숙소_추천"], "api_params": {"location": "제주도"}}
            
            - 사용자 질문: "부산"
            - JSON 응답: {"intents": ["숙소_추천"], "api_params": {"location": "부산"}}

            - 사용자 질문: "안녕"
            - JSON 응답: {"intents": ["인사"]}

            # 처리할 사용자 질문
            "%s"

            # JSON 응답:
            """, userMessage);

        return geminiService.askGemini(intentPrompt)
                .flatMap(geminiResponseJson -> {
                    String cleanGeminiResponseJson = cleanJsonString(geminiResponseJson);
                    
                    // ✨ 디버깅을 위해 AI의 원본 응답을 로그로 출력합니다.
                    System.out.println("DEBUG: Gemini Raw JSON Response: " + cleanGeminiResponseJson);

                    try {
                        Map<String, Object> geminiParsed = objectMapper.readValue(cleanGeminiResponseJson, Map.class);
                        List<String> intents = (List<String>) geminiParsed.getOrDefault("intents", Collections.singletonList("숙소_추천"));
                        Map<String, String> apiParams = (Map<String, String>) geminiParsed.getOrDefault("api_params", new HashMap<>());
                        String responseTextFallback = (String) geminiParsed.getOrDefault("response_text", "어떤 지역의 숙소를 추천해 드릴까요?");
                        String location = apiParams.get("location");

                        StringBuilder finalResponseBuilder = new StringBuilder();

                        if (intents.isEmpty() || intents.contains("기타")) {
                            intents = Collections.singletonList("숙소_추천");
                        }

                        List<Mono<String>> responseMonos = new ArrayList<>();

                        for (String currentIntent : intents) {
                            switch (currentIntent) {
                                case "여행지_추천":
                                case "숙소_추천":
                                    if (location != null && !location.isEmpty()) {
                                        System.out.println("DEBUG: ChatbotService - 숙소 추천 요청. Location: " + location + ", apiParams: " + apiParams);
                                        responseMonos.add(publicDataService.getAccommodationInfo(apiParams)
                                                .flatMap(dataList -> {
                                                    if (!dataList.isEmpty()) {
                                                        String dataJson;
                                                        try {
                                                            List<Map<String, String>> top5Items = dataList.stream().limit(5).collect(Collectors.toList());
                                                            dataJson = objectMapper.writeValueAsString(top5Items);
                                                        } catch (JsonProcessingException e) {
                                                            System.err.println("ERROR: ChatbotService - 숙소 데이터를 JSON으로 변환 중 오류: " + e.getMessage());
                                                            dataJson = "[]";
                                                        }
                                                        String recommendationPrompt = String.format("""
                                                            사용자가 요청한 '%s'에 대한 숙소 추천 데이터가 있습니다: %s.
                                                            이 정보를 바탕으로 사용자에게 친근하고 매력적인 숙소 추천 답변을 5개 이내로 생성해 주세요.
                                                            정보는 간결하게 제공하고, 너무 많은 내용을 담지 마세요.
                                                            """, location, dataJson);
                                                        return geminiService.askGemini(recommendationPrompt);
                                                    } else {
                                                        return Mono.just(String.format("죄송합니다. '%s' 지역에 대한 숙소 정보를 찾을 수 없습니다.", location));
                                                    }
                                                })
                                                .onErrorResume(e -> {
                                                    System.err.println("ERROR: ChatbotService - 숙소 정보 호출 중 오류: " + e.getMessage());
                                                    return Mono.just("죄송합니다. 숙소 정보를 가져오는 중 오류가 발생했습니다.");
                                                }));
                                    } else {
                                        responseMonos.add(Mono.just("어떤 지역의 숙소를 추천해 드릴까요?"));
                                    }
                                    break;

                                case "식당_추천":
                                    if (location != null && !location.isEmpty()) {
                                        System.out.println("DEBUG: ChatbotService - 식당 추천 요청. Location: " + location + ", apiParams: " + apiParams);
                                        responseMonos.add(publicDataService.getRestaurantInfo(apiParams)
                                                .flatMap(dataList -> {
                                                    if (!dataList.isEmpty()) {
                                                        String dataJson;
                                                        try {
                                                            List<Map<String, String>> top5Items = dataList.stream().limit(5).collect(Collectors.toList());
                                                            dataJson = objectMapper.writeValueAsString(top5Items);
                                                        } catch (JsonProcessingException e) {
                                                            System.err.println("ERROR: ChatbotService - 식당 데이터를 JSON으로 변환 중 오류: " + e.getMessage());
                                                            dataJson = "[]";
                                                        }
                                                        String recommendationPrompt = String.format("""
                                                            사용자가 요청한 '%s'에 대한 식당 추천 데이터가 있습니다: %s.
                                                            이 정보를 바탕으로 사용자에게 친근하고 매력적인 식당 추천 답변을 5개 이내로 생성해 주세요.
                                                            정보는 간결하게 제공하고, 너무 많은 내용을 담지 마세요.
                                                            """, location, dataJson);
                                                        return geminiService.askGemini(recommendationPrompt);
                                                    } else {
                                                        return Mono.just(String.format("죄송합니다. '%s' 지역에 대한 식당 정보를 찾을 수 없습니다.", location));
                                                    }
                                                })
                                                .onErrorResume(e -> {
                                                    System.err.println("ERROR: ChatbotService - 식당 정보 호출 중 오류: " + e.getMessage());
                                                    return Mono.just("죄송합니다. 식당 정보를 가져오는 중 오류가 발생했습니다.");
                                                }));
                                    } else {
                                        responseMonos.add(Mono.just("어떤 지역의 식당을 추천해 드릴까요?"));
                                    }
                                    break;
                                    
                                case "날씨_정보_요청":
                                    responseMonos.add(Mono.just("날씨 정보는 현재 준비 중인 기능이에요. 곧 멋진 날씨 정보를 알려드릴 수 있도록 준비할게요!"));
                                    break;

                                case "인사":
                                    responseMonos.add(Mono.just("안녕하세요! 식당이나 숙소 정보를 알려드릴 수 있어요. 어떤 지역의 숙소를 먼저 추천해 드릴까요?"));
                                    break;

                                case "상세_정보_요청":
                                    responseMonos.add(Mono.just("어떤 항목의 상세 정보가 궁금하신가요? (아직 구현되지 않은 기능입니다)"));
                                    break;

                                default: // '기타' 의도
                                    responseMonos.add(Mono.just(responseTextFallback));
                                    break;
                            }
                        }

                        return Mono.zip(responseMonos, objects -> {
                            for (Object obj : objects) {
                                if (obj instanceof String) {
                                    finalResponseBuilder.append(obj).append("\n\n");
                                }
                            }
                            return finalResponseBuilder.toString().trim();
                        }).onErrorResume(e -> {
                            System.err.println("ERROR: ChatbotService - 최종 응답 조합 중 오류 발생: " + e.getMessage());
                            return Mono.just("죄송합니다. 여러 정보를 동시에 처리하는 중 오류가 발생했습니다.");
                        });

                    } catch (JsonProcessingException e) {
                        System.err.println("ERROR: ChatbotService - Gemini 응답 JSON 파싱 오류: " + e.getMessage() + "\n원본 응답: " + geminiResponseJson);
                        return Mono.just("죄송합니다. AI 응답을 이해할 수 없습니다. 다시 시도해 주세요.");
                    } catch (Exception e) {
                        System.err.println("ERROR: ChatbotService - 챗봇 로직 처리 중 예상치 못한 오류: " + e.getMessage());
                        return Mono.just("죄송합니다. 챗봇 서비스 내부 오류가 발생했습니다.");
                    }
                })
                .onErrorResume(e -> {
                    System.err.println("ERROR: ChatbotService - GeminiService 호출 실패 또는 네트워크 오류: " + e.getMessage());
                    return Mono.just("현재 AI 서비스에 연결할 수 없습니다. 잠시 후 다시 시도해 주세요.");
                });
    }

    private String cleanJsonString(String json) {
        String cleanJson = json.trim();
        if (cleanJson.startsWith("```json")) {
            cleanJson = cleanJson.substring("```json".length());
        } else if (cleanJson.startsWith("```")) {
            cleanJson = cleanJson.substring("```".length());
        }
        if (cleanJson.endsWith("```")) {
            cleanJson = cleanJson.substring(0, cleanJson.length() - "```".length());
        }
        return cleanJson.trim();
    }
}