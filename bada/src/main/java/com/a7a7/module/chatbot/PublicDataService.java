package com.a7a7.module.chatbot;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.util.UriComponentsBuilder;
import reactor.core.publisher.Mono;

import java.net.URI;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class PublicDataService {

    @Value("${sea_api_key}")
    private String seaInfoApiKey;

    @Value("${restaurant_api_key}")
    private String restaurantApiKey;

    @Value("${accom_api_key}")
    private String accommodationApiKey;

    private final WebClient webClient;
    private final ObjectMapper objectMapper;

    // 해양정보 API에서 유효한 지역명 목록
    private static final List<String> VALID_SEA_LOCATIONS = Arrays.asList(
            "부산", "인천", "제주", "울산", "여수", "군산", "목포", "속초", "동해", "포항",
            "강릉", "서산", "태안", "보령", "신안", "완도", "통영", "거제", "고성", "삼척",
            "부안", "울릉도", "독도", "강화도", "덕적도", "소청도", "백령도", "어청도", "가거도",
            "칠발도", "거문도", "초도", "상추자도", "하추자도", "성산포", "서귀포", "마라도", "이어도"
    );

    public PublicDataService(WebClient.Builder webClientBuilder, ObjectMapper objectMapper) {
        this.webClient = webClientBuilder.build();
        this.objectMapper = objectMapper;
    }

    @SuppressWarnings("unchecked")
    private Mono<List<Map<String, String>>> callApi(String baseUrl, String serviceKey, Map<String, String> params, String itemsPath) {
        String encodedServiceKey = serviceKey;

        UriComponentsBuilder uriBuilder = UriComponentsBuilder.fromUriString(baseUrl)
                .queryParam("ServiceKey", encodedServiceKey);

        // 한국관광공사 API 공통 파라미터 추가
        if (baseUrl.contains("KorService1")) {
            uriBuilder.queryParam("_type", "json")
                    .queryParam("numOfRows", "100")
                    .queryParam("pageNo", "1")
                    .queryParam("MobileOS", "ETC")
                    .queryParam("MobileApp", "AppTest")
                    .queryParam("listYN", "Y")
                    .queryParam("arrange", "A");
        // 해양정보 API 고정 파라미터 추가
        } else if (baseUrl.contains("fcstSeaTrip")) {
            uriBuilder.queryParam("type", "json")
                    .queryParam("pageNo", "1")
                    .queryParam("numOfRows", "300")
                    // Postman에서 성공한 'include' 파라미터와 동일하게 유지
                    .queryParam("include", "lastScr,sareaDtlNm,lat,lot,predcYmd,predcNoonSeCd,avgArtmp,avgWspd,avgWtem,avgWvhgt,avgCrsp,weather,totalIndex");
        }

        // 동적 파라미터 추가
        params.forEach((key, value) -> {
            try {
                // areaCode, contentTypeId 처럼 인코딩이 불필요한 파라미터는 바로 추가
                if (key.equals("areaCode") || key.equals("contentTypeId")) {
                    uriBuilder.queryParam(key, value);
                } else { // 그 외 (한글 등) 인코딩이 필요한 파라미터만 인코딩
                    uriBuilder.queryParam(key, URLEncoder.encode(value, StandardCharsets.UTF_8.toString()));
                }
            } catch (Exception e) {
                System.err.println("ERROR: 쿼리 파라미터 인코딩 오류: " + key + "=" + value + " - " + e.getMessage());
            }
        });

        URI finalUri = uriBuilder.build(true).toUri(); // build(true)는 이미 인코딩된 값을 다시 인코딩하지 않습니다.

        System.out.println("DEBUG: API 호출 최종 URI: " + finalUri.toString()); // 디버깅을 위한 최종 URI 출력

        return webClient.get()
                .uri(finalUri)
                .retrieve()
                .bodyToMono(String.class)
                .flatMap(jsonResponse -> {
                    // API 응답 전체를 여기에 출력하여 정확한 응답 구조를 확인합니다.
                    System.out.println("DEBUG: API 원본 응답 (Base URL: " + baseUrl + "): " + jsonResponse);
                    try {
                        JsonNode root = objectMapper.readTree(jsonResponse);
                        JsonNode header = root.path("response").path("header");
                        String resultCode = header.path("resultCode").asText();
                        String resultMsg = header.path("resultMsg").asText();

                        // API 응답이 성공이 아닌 경우 (XML 응답도 여기서 걸러짐)
                        if (!"00".equals(resultCode) && !"0000".equals(resultCode)) {
                            System.err.println("ERROR: 공공데이터 API 응답 오류 (Result Code: " + resultCode + ", Message: " + resultMsg + ")");
                            // 원본 응답은 이미 위에서 출력했으므로 중복 출력하지 않습니다.
                            if (jsonResponse.trim().startsWith("<")) {
                                return Mono.error(new RuntimeException("공공데이터 API 응답이 XML 형식입니다. (API 키 오류 또는 잘못된 파라미터일 수 있음)"));
                            }
                            return Mono.just(Collections.<Map<String, String>>emptyList());
                        }

                        JsonNode itemsNode = root.path("response").path("body").path("items").path(itemsPath);

                        List<Map<String, String>> resultList = new ArrayList<>();

                        if (itemsNode.isArray()) {
                            for (JsonNode item : itemsNode) {
                                Map<String, String> itemMap = new HashMap<>();
                                item.fields().forEachRemaining(entry -> {
                                    itemMap.put(entry.getKey(), entry.getValue().asText(""));
                                });
                                resultList.add(itemMap);
                            }
                        } else if (itemsNode.isObject()) { // 단일 객체일 경우도 처리
                            Map<String, String> itemMap = new HashMap<>();
                            itemsNode.fields().forEachRemaining(entry -> {
                                itemMap.put(entry.getKey(), entry.getValue().asText(""));
                            });
                            resultList.add(itemMap);
                        } else {
                            System.err.println("WARN: 공공데이터 API 응답에 'items' 또는 지정된 itemPath 노드가 없거나 배열/객체가 아닙니다.");
                            // 원본 응답은 이미 위에서 출력했으므로 중복 출력하지 않습니다.
                            return Mono.just(Collections.<Map<String, String>>emptyList());
                        }
                        return Mono.just(resultList);
                    } catch (Exception e) {
                        System.err.println("ERROR: 공공데이터 API 응답 파싱 오류: " + e.getMessage());
                        // 원본 응답은 이미 위에서 출력했으므로 중복 출력하지 않습니다.
                        if (jsonResponse.trim().startsWith("<")) {
                            return Mono.error(new RuntimeException("공공데이터 API 응답이 XML 형식입니다. (API 키 오류 또는 잘못된 파라미터일 수 있음)"));
                        }
                        return Mono.error(new RuntimeException("공공데이터 API 응답 파싱 실패"));
                    }
                })
                .onErrorResume(e -> {
                    System.err.println("ERROR: 공공데이터 API 호출 중 오류 발생 (Base URL: " + baseUrl + "): " + e.getMessage());
                    return Mono.just(Collections.<Map<String, String>>emptyList());
                });
    }

    // 해양정보 API 호출 (날씨, 총체 지수 등)
    public Mono<List<Map<String, String>>> getSeaInfo(Map<String, String> params) {
        String baseUrl = "https://apis.data.go.kr/1192136/fcstSeaTrip/GetFcstSeaTripApiService";
        Map<String, String> apiParams = new HashMap<>();

        String location = params.getOrDefault("location", "");
        // 유효한 해양 지역인지 검사합니다.
        if (location.isEmpty() || VALID_SEA_LOCATIONS.stream().noneMatch(loc -> loc.equals(location))) {
            System.out.println("DEBUG: PublicDataService - 해양정보 API는 '" + location + "' 지역의 정보를 제공하지 않거나 유효하지 않습니다. 호출 스킵.");
            return Mono.just(Collections.<Map<String, String>>emptyList());
        }

        // Postman에서 성공했던 조합을 정확히 재현 (sareadtlnm와 reqDate를 항상 보냄)
        // reqDate는 YYYYMMDD 형식으로, 없으면 오늘 날짜를 보냄
        String reqDateFromParams = params.get("date");
        String formattedDate;
        if (reqDateFromParams == null || reqDateFromParams.isEmpty()) {
            formattedDate = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyyMMdd")); // 오늘 날짜
            System.out.println("DEBUG: PublicDataService - 날짜 미지정 요청: 오늘 날짜(" + formattedDate + ") 사용");
        } else {
            formattedDate = reqDateFromParams.replace("-", "");
            System.out.println("DEBUG: PublicDataService - 날짜 지정 요청: " + formattedDate + " 사용");
        }

        apiParams.put("sareadtlnm", location); // 지역명은 항상 보냄
        apiParams.put("reqDate", formattedDate); // 요청 날짜도 항상 보냄

        System.out.println("DEBUG: PublicDataService - getSeaInfo - API 호출 파라미터: " + apiParams); // API 호출 전 파라미터 확인

        return callApi(baseUrl, seaInfoApiKey, apiParams, "item")
            .map(seaData -> {
                System.out.println("DEBUG: PublicDataService - getSeaInfo - callApi에서 반환된 원본 seaData (필터링 전): " + seaData);

                if (!seaData.isEmpty()) {
                    // 정확히 일치하는 날짜와 지역의 데이터를 찾기
                    // 현재는 predcNoonSeCd (오전/오후)를 고려하지 않고, 해당 날짜의 첫 번째 데이터만 가져오는 것으로 단순화
                    List<Map<String, String>> filteredData = seaData.stream()
                        .filter(item ->
                            item.containsKey("sareaDtlNm") && item.get("sareaDtlNm").equals(location) &&
                            item.containsKey("predcYmd") && item.get("predcYmd").equals(formattedDate)
                        )
                        .collect(Collectors.toList());

                    System.out.println("DEBUG: PublicDataService - getSeaInfo - 필터링된 seaData: " + filteredData);

                    if (!filteredData.isEmpty()) {
                        // 가장 적합한 단일 항목 (현재는 필터링된 것 중 첫 번째) 반환
                        return Collections.singletonList(filteredData.get(0));
                    } else {
                        System.out.println("DEBUG: PublicDataService - 지정된 날짜(" + formattedDate + ")와 지역(" + location + ")에 해당하는 필터링된 데이터 없음.");
                        // 만약 특정 날짜 데이터가 없으면, 전체 데이터 중 최신 날짜의 해당 지역 데이터를 가져오도록 폴백 로직 추가
                        List<Map<String, String>> fallbackData = seaData.stream()
                                .filter(item -> item.containsKey("sareaDtlNm") && item.get("sareaDtlNm").equals(location))
                                .sorted((d1, d2) -> d2.getOrDefault("predcYmd", "").compareTo(d1.getOrDefault("predcYmd", ""))) // 최신 날짜 순 정렬
                                .collect(Collectors.toList());
                        if (!fallbackData.isEmpty()) {
                            System.out.println("DEBUG: PublicDataService - 지정된 날짜 데이터가 없어 최신 날짜의 데이터로 폴백: " + fallbackData.get(0));
                            return Collections.singletonList(fallbackData.get(0));
                        }
                    }
                }
                System.out.println("DEBUG: PublicDataService - seaData가 비어있거나 필터링 후 데이터 없음.");
                return Collections.<Map<String, String>>emptyList(); // 날씨 정보 없으면 빈 리스트 반환
            });
    }

    // 식당 정보 API 호출
    public Mono<List<Map<String, String>>> getRestaurantInfo(Map<String, String> params) {
        String baseUrl = "http://apis.data.go.kr/B551011/KorService1/areaBasedList1";
        Map<String, String> apiParams = new HashMap<>();

        apiParams.put("contentTypeId", "39"); // 콘텐츠 타입 ID: 39 (음식점)

        String locationName = params.getOrDefault("location", "서울"); // 기본값 서울
        String areaCode = convertLocationToAreaCode(locationName);
        apiParams.put("areaCode", areaCode);

        return callApi(baseUrl, restaurantApiKey, apiParams, "item");
    }

    // 숙박업소 정보 API 호출
    public Mono<List<Map<String, String>>> getAccommodationInfo(Map<String, String> params) {
        String baseUrl = "http://apis.data.go.kr/B551011/KorService1/searchStay1";
        Map<String, String> apiParams = new HashMap<>();

        String locationName = params.getOrDefault("location", "서울"); // 기본값 서울
        String areaCode = convertLocationToAreaCode(locationName);
        apiParams.put("areaCode", areaCode);

        return callApi(baseUrl, accommodationApiKey, apiParams, "item");
    }

    // 지역 이름을 한국관광공사 API의 areaCode로 변환하는 헬퍼 함수
    private String convertLocationToAreaCode(String locationName) {
        switch (locationName) {
            case "서울": return "1"; case "인천": return "2"; case "대전": return "3";
            case "대구": return "4"; case "광주": return "5"; case "부산": return "6";
            case "울산": return "7"; case "세종": return "8"; case "경기": return "31";
            case "강원": return "32"; case "충북": return "33"; case "충남": return "34";
            case "경북": return "35"; case "경남": return "36"; case "전북": return "37";
            case "전남": return "38"; case "제주": return "39";
        }
        if (locationName.contains("부안") || locationName.equals("전주") || locationName.equals("군산") ||
            locationName.equals("익산") || locationName.equals("정읍") || locationName.equals("남원") ||
            locationName.equals("김제") || locationName.equals("완주") || locationName.equals("진안") ||
            locationName.equals("무주") || locationName.equals("장수") || locationName.equals("임실") ||
            locationName.equals("순창") || locationName.equals("고창")) {
            return "37";
        }
        if (locationName.contains("강릉") || locationName.contains("속초") || locationName.contains("동해") || locationName.contains("삼척") ||
            locationName.equals("춘천") || locationName.equals("원주") || locationName.equals("태백") ||
            locationName.equals("홍천") || locationName.equals("횡성") || locationName.equals("영월") ||
            locationName.equals("평창") || locationName.equals("정선") || locationName.equals("철원") ||
            locationName.equals("화천") || locationName.equals("양구") || locationName.equals("인제") ||
            locationName.equals("고성") || locationName.equals("양양")) {
            return "32";
        }
        return "1";
    }
}