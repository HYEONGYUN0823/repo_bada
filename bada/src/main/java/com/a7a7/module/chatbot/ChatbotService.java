package com.a7a7.module.chatbot;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

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
        String todayDate = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));

        // Gemini 프롬프트 수정: intent를 배열로 받을 수 있도록 변경
        String intentPrompt = String.format("""
            사용자의 질문을 분석하여 JSON 형식으로 응답해 주세요.
            응답에는 'intents' (의도: ['여행지_추천', '식당_추천', '숙소_추천', '날씨_정보_요청', '상세_정보_요청', '인사', '기타'] 중 하나 또는 여러 개),
            'entities' (핵심 정보: '장소', '음식_유형', '테마', '가격대' 등),
            그리고 'api_params' (공공데이터 API 호출에 필요한 파라미터)를 포함해 주세요.
            'api_params'는 key-value 쌍으로, 'location' 같은 지역 정보는 반드시 포함해 주세요.
            날짜가 명시되면 'date' 파라미터도 추가해 주세요. (없으면 오늘의 날짜: %s 를 기본으로 생각하거나 비워두세요)
            이해하지 못하는 질문이면 'intents'를 ['기타']로 설정하고 'response_text'에 "무엇을 도와드릴까요?"라고 답변해 주세요.

            사용자 질문: "%s"

            JSON 응답:
            """, todayDate, userMessage);

        return geminiService.askGemini(intentPrompt)
                .flatMap(geminiResponseJson -> {
                    String cleanGeminiResponseJson = cleanJsonString(geminiResponseJson);

                    try {
                        Map<String, Object> geminiParsed = objectMapper.readValue(cleanGeminiResponseJson, Map.class);
                        // intent를 List<String>으로 받도록 변경
                        List<String> intents = (List<String>) geminiParsed.getOrDefault("intents", Collections.singletonList("기타"));
                        Map<String, String> originalApiParams = (Map<String, String>) geminiParsed.getOrDefault("api_params", new HashMap<>());

                        Map<String, String> finalApiParams = new HashMap<>(originalApiParams);
                        // 날짜 파라미터가 없으면 오늘 날짜를 기본으로 설정 (gemini 프롬프트에서 이미 처리하도록 유도하지만, 만일을 위해 한 번 더)
                        finalApiParams.putIfAbsent("date", todayDate);

                        String responseTextFallback = (String) geminiParsed.getOrDefault("response_text", "죄송합니다. 요청을 처리할 수 없습니다.");
                        String location = finalApiParams.get("location");

                        // 최종 응답을 위한 StringBuilder
                        StringBuilder finalResponseBuilder = new StringBuilder();

                        // 모든 추천/날씨 관련 인텐트에서 날씨 정보를 먼저 가져오는 공통 로직
                        Mono<String> weatherInfoMono;
                        if (location != null && !location.isEmpty()) {
                            System.out.println("DEBUG: ChatbotService - 날씨 정보 요청 시작. Location: " + location + ", finalApiParams: " + finalApiParams);
                            weatherInfoMono = publicDataService.getSeaInfo(finalApiParams)
                                    .map(seaData -> {
                                        System.out.println("DEBUG: ChatbotService - PublicDataService.getSeaInfo 반환된 raw seaData: " + seaData);
                                        if (!seaData.isEmpty()) {
                                            // PublicDataService에서 이미 필터링된 단일 항목을 반환한다고 가정
                                            Map<String, String> item = seaData.get(0); // 첫 번째 항목만 사용
                                            return String.format(
                                                "%s의 %s %s 해양 날씨는 %s이고, 평균 기온은 %s°C, 평균 수온은 %s°C, 종합 지수는 '%s'입니다.",
                                                item.getOrDefault("sareaDtlNm", location),
                                                item.getOrDefault("predcYmd", "날짜 미상"),
                                                item.getOrDefault("predcNoonSeCd", ""),
                                                item.getOrDefault("weather", "알 수 없음"),
                                                item.getOrDefault("avgArtmp", "알 수 없음"),
                                                item.getOrDefault("avgWtem", "알 수 없음"),
                                                item.getOrDefault("totalIndex", "알 수 없음")
                                            );
                                        }
                                        System.out.println("DEBUG: ChatbotService - getSeaInfo에서 반환된 seaData가 비어있음.");
                                        return ""; // 날씨 정보 없으면 빈 문자열 반환
                                    }).onErrorResume(e -> {
                                        System.err.println("ERROR: ChatbotService - 해양 날씨 정보 조회 오류: " + e.getMessage());
                                        return Mono.just("날씨 정보를 가져오는 데 문제가 발생했습니다.");
                                    });
                        } else {
                            System.out.println("DEBUG: ChatbotService - 날씨 정보 요청 스킵됨. Location이 null 또는 비어있음.");
                            weatherInfoMono = Mono.just("");
                        }

                        // 여러 의도를 처리하기 위한 Mono 리스트
                        List<Mono<String>> responseMonos = new ArrayList<>();

                        // 각 의도에 따라 API 호출 및 응답 생성 로직 추가
                        for (String currentIntent : intents) {
                            switch (currentIntent) {
                                case "여행지_추천": // 여행지_추천은 해양 정보 API로 대체되어 날씨와 동일하게 처리
                                case "날씨_정보_요청":
                                    if (location != null && !location.isEmpty()) {
                                        // weatherInfoMono의 결과를 사용하여 응답 생성
                                        responseMonos.add(weatherInfoMono.map(weatherText -> {
                                            if (weatherText.isEmpty() || weatherText.contains("문제가 발생했습니다.")) {
                                                return String.format("죄송합니다. '%s'의 날씨/해양 정보를 찾을 수 없거나 가져오는 데 문제가 발생했습니다.", location);
                                            }
                                            return String.format("'%s'의 날씨/해양 정보는 다음과 같습니다:\n%s", location, weatherText);
                                        }));
                                    } else {
                                        responseMonos.add(Mono.just("어떤 지역의 날씨가 궁금하신가요?"));
                                    }
                                    break;

                                case "식당_추천":
                                case "숙소_추천":
                                    if (location != null && !location.isEmpty()) {
                                        final String contentType;
                                        Mono<List<Map<String, String>>> dataMono;

                                        if (currentIntent.equals("식당_추천")) {
                                            contentType = "식당";
                                            dataMono = publicDataService.getRestaurantInfo(finalApiParams);
                                        } else { // 숙소_추천
                                            contentType = "숙소";
                                            dataMono = publicDataService.getAccommodationInfo(finalApiParams);
                                        }

                                        // 각 추천/정보 요청에 대한 Mono 생성 및 추가
                                        responseMonos.add(dataMono.flatMap(dataList -> {
                                            if (!dataList.isEmpty()) {
                                                String dataJson;
                                                try {
                                                    // 응답 크기를 줄이기 위해 처음 5개 항목만 사용
                                                    List<Map<String, String>> top5Items = dataList.stream().limit(5).collect(Collectors.toList());
                                                    dataJson = objectMapper.writeValueAsString(top5Items);
                                                } catch (JsonProcessingException e) {
                                                    System.err.println("데이터를 JSON으로 변환 중 오류: " + e.getMessage());
                                                    dataJson = "[]";
                                                }

                                                String recommendationPrompt = String.format("""
                                                    사용자가 요청한 '%s'에 대한 %s 추천 데이터가 있습니다: %s.
                                                    이 정보를 바탕으로 사용자에게 친근하고 매력적인 %s 추천 답변을 5개 이내로 생성해 주세요.
                                                    정보는 간결하게 제공하고, 너무 많은 내용을 담지 마세요.
                                                    """, location, contentType, dataJson, contentType);

                                                return geminiService.askGemini(recommendationPrompt);
                                            } else {
                                                return Mono.just(String.format("죄송합니다. '%s' 지역에 대한 %s 정보를 찾을 수 없습니다.", location, contentType));
                                            }
                                        }).onErrorResume(e -> {
                                            System.err.println(contentType + " 정보 호출 중 오류 발생: " + e.getMessage());
                                            return Mono.just(String.format("죄송합니다. %s 정보를 가져오는 중 오류가 발생했습니다.", contentType));
                                        }));
                                    } else {
                                        responseMonos.add(Mono.just(String.format("어떤 지역의 %s를 추천해 드릴까요?", currentIntent.replace("_추천", ""))));
                                    }
                                    break;

                                case "인사":
                                    responseMonos.add(Mono.just("안녕하세요! 해양정보(날씨, 총체 지수), 식당, 숙소 정보를 알려드릴 수 있어요. 무엇을 도와드릴까요?"));
                                    break;

                                case "상세_정보_요청":
                                    responseMonos.add(Mono.just("어떤 항목의 상세 정보가 궁금하신가요? (아직 구현되지 않은 기능입니다)"));
                                    break;

                                default: // '기타' 의도 또는 알 수 없는 의도
                                    responseMonos.add(Mono.just(responseTextFallback));
                                    break;
                            }
                        }

                        // 모든 응답 Mono를 합쳐서 최종 응답을 생성
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