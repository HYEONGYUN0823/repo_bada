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
        // 날짜 관련 로직 제거, 숙소 추천에 날짜 불필요
        // String todayDate = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));

        // Gemini 프롬프트 수정: 날씨 의도 제외, 숙소 추천 기본 우선, 의도 배열로 처리
        String intentPrompt = String.format("""
            사용자의 질문을 분석하여 JSON 형식으로 응답해 주세요.
            응답에는 'intents' (의도: ['여행지_추천', '식당_추천', '숙소_추천', '상세_정보_요청', '인사', '기타'] 중 하나 또는 여러 개),
            'entities' (핵심 정보: '장소', '음식_유형', '테마', '가격대' 등),
            그리고 'api_params' (공공데이터 API 호출에 필요한 파라미터)를 포함해 주세요.
            'api_params'는 key-value 쌍으로, 'location' 같은 지역 정보는 반드시 포함해 주세요.
            사용자가 지역명만 입력하거나 의도가 불명확하면 'intents'를 ['숙소_추천']으로 설정하고, 'api_params'에 'location'을 포함해 주세요.
            날씨 정보는 요청되지 않으므로 '날씨_정보_요청' 의도는 포함시키지 마세요.
            이해하지 못하는 질문이면 'intents'를 ['기타']로 설정하고 'response_text'에 "어떤 지역의 숙소를 추천해 드릴까요?"라고 답변해 주세요.

            사용자 질문: "%s"

            JSON 응답:
            """, userMessage);

        return geminiService.askGemini(intentPrompt)
                .flatMap(geminiResponseJson -> {
                    String cleanGeminiResponseJson = cleanJsonString(geminiResponseJson);

                    try {
                        Map<String, Object> geminiParsed = objectMapper.readValue(cleanGeminiResponseJson, Map.class);
                        // 의도를 List<String>으로 받음
                        List<String> intents = (List<String>) geminiParsed.getOrDefault("intents", Collections.singletonList("숙소_추천")); // 기본 의도를 숙소_추천으로 변경
                        Map<String, String> apiParams = (Map<String, String>) geminiParsed.getOrDefault("api_params", new HashMap<>());
                        String responseTextFallback = (String) geminiParsed.getOrDefault("response_text", "어떤 지역의 숙소를 추천해 드릴까요?");
                        String location = apiParams.get("location");

                        // 최종 응답을 위한 StringBuilder
                        StringBuilder finalResponseBuilder = new StringBuilder();

                        // 의도가 비어있거나 기타일 경우 숙소 추천으로 기본 처리
                        if (intents.isEmpty() || intents.contains("기타")) {
                            intents = Collections.singletonList("숙소_추천");
                        }

                        // 여러 의도를 처리하기 위한 Mono 리스트
                        List<Mono<String>> responseMonos = new ArrayList<>();

                        // 각 의도에 따라 API 호출 및 응답 생성
                        for (String currentIntent : intents) {
                            switch (currentIntent) {
                                case "여행지_추천":
                                    // 여행지 추천은 숙소 추천으로 대체
                                    // 의도적 낙찰
                                case "숙소_추천":
                                    if (location != null && !location.isEmpty()) {
                                        System.out.println("DEBUG: ChatbotService - 숙소 추천 요청. Location: " + location + ", apiParams: " + apiParams);
                                        responseMonos.add(publicDataService.getAccommodationInfo(apiParams)
                                                .flatMap(dataList -> {
                                                    if (!dataList.isEmpty()) {
                                                        String dataJson;
                                                        try {
                                                            // 응답 크기를 줄이기 위해 처음 5개 항목만 사용
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
                                                            // 응답 크기를 줄이기 위해 처음 5개 항목만 사용
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

                        // 모든 응답 Mono를 합쳐서 최종 응답 생성
                        return Mono.zip(responseMonos, objects -> {
                            for (Object obj : objects) {
                                if (obj instanceof String) {
                                    finalResponseBuilder.append(obj).append("\n\n");
                                }
                            }
                            return finalResponseBuilder.toString().trim(); // 마지막 개행 제거
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

    // Gemini 응답 JSON 문자열에서 코드 블록 마크다운을 제거하는 헬퍼 메서드
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