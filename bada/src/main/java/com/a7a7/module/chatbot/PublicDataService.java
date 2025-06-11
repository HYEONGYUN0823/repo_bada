package com.a7a7.module.chatbot;

import java.net.URI;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.util.UriComponentsBuilder;

import com.a7a7.common.config.ApiKeysConfig;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import reactor.core.publisher.Mono;

@Service
public class PublicDataService {

//    @Value("${restaurant_api_key}")
//    private String restaurantApiKey;
//
//    @Value("${accom_api_key}")
//    private String accommodationApiKey;
    
    @Autowired
    ApiKeysConfig apiKeysConfig;

    private final WebClient webClient;
    private final ObjectMapper objectMapper;

    private static final Map<String, String> AREA_CODE_MAP; // 광역 지자체명 <-> 지역 코드
    private static final Map<String, String> CITY_TO_AREA_CODE_MAP; // 주요 시/군/구명 -> 광역 지역 코드

    static {
        AREA_CODE_MAP = new HashMap<>();
        AREA_CODE_MAP.put("서울", "1");
        AREA_CODE_MAP.put("서울특별시", "1");
        AREA_CODE_MAP.put("인천", "2");
        AREA_CODE_MAP.put("인천광역시", "2");
        AREA_CODE_MAP.put("대전", "3");
        AREA_CODE_MAP.put("대전광역시", "3");
        AREA_CODE_MAP.put("대구", "4");
        AREA_CODE_MAP.put("대구광역시", "4");
        AREA_CODE_MAP.put("광주", "5");
        AREA_CODE_MAP.put("광주광역시", "5");
        AREA_CODE_MAP.put("부산", "6");
        AREA_CODE_MAP.put("부산광역시", "6");
        AREA_CODE_MAP.put("울산", "7");
        AREA_CODE_MAP.put("울산광역시", "7");
        AREA_CODE_MAP.put("세종", "8");
        AREA_CODE_MAP.put("세종특별자치시", "8");
        AREA_CODE_MAP.put("경기", "31");
        AREA_CODE_MAP.put("경기도", "31");
        AREA_CODE_MAP.put("강원", "32");
        AREA_CODE_MAP.put("강원도", "32");
        AREA_CODE_MAP.put("강원특별자치도", "32");
        AREA_CODE_MAP.put("충북", "33");
        AREA_CODE_MAP.put("충청북도", "33");
        AREA_CODE_MAP.put("충남", "34");
        AREA_CODE_MAP.put("충청남도", "34");
        AREA_CODE_MAP.put("경북", "35");
        AREA_CODE_MAP.put("경상북도", "35");
        AREA_CODE_MAP.put("경남", "36");
        AREA_CODE_MAP.put("경상남도", "36");
        AREA_CODE_MAP.put("전북", "37");
        AREA_CODE_MAP.put("전라북도", "37");
        AREA_CODE_MAP.put("전북특별자치도", "37");
        AREA_CODE_MAP.put("전남", "38");
        AREA_CODE_MAP.put("전라남도", "38");
        AREA_CODE_MAP.put("제주", "39");
        AREA_CODE_MAP.put("제주도", "39");
        AREA_CODE_MAP.put("제주특별자치도", "39");

        CITY_TO_AREA_CODE_MAP = new HashMap<>();
        // 서울 (1)
        CITY_TO_AREA_CODE_MAP.put("강남", "1"); CITY_TO_AREA_CODE_MAP.put("강남구", "1");
        CITY_TO_AREA_CODE_MAP.put("종로", "1"); CITY_TO_AREA_CODE_MAP.put("종로구", "1");
        CITY_TO_AREA_CODE_MAP.put("마포", "1"); CITY_TO_AREA_CODE_MAP.put("마포구", "1");
        CITY_TO_AREA_CODE_MAP.put("서초", "1"); CITY_TO_AREA_CODE_MAP.put("서초구", "1");
        CITY_TO_AREA_CODE_MAP.put("송파", "1"); CITY_TO_AREA_CODE_MAP.put("송파구", "1");
        CITY_TO_AREA_CODE_MAP.put("영등포", "1"); CITY_TO_AREA_CODE_MAP.put("영등포구", "1");
        CITY_TO_AREA_CODE_MAP.put("용산", "1"); CITY_TO_AREA_CODE_MAP.put("용산구", "1");

        // 인천 (2)
        CITY_TO_AREA_CODE_MAP.put("부평", "2"); CITY_TO_AREA_CODE_MAP.put("부평구", "2");
        CITY_TO_AREA_CODE_MAP.put("남동", "2"); CITY_TO_AREA_CODE_MAP.put("남동구", "2");
        CITY_TO_AREA_CODE_MAP.put("연수", "2"); CITY_TO_AREA_CODE_MAP.put("연수구", "2");
        CITY_TO_AREA_CODE_MAP.put("강화", "2"); CITY_TO_AREA_CODE_MAP.put("강화군", "2");

        // 대전 (3)
        CITY_TO_AREA_CODE_MAP.put("유성", "3"); CITY_TO_AREA_CODE_MAP.put("유성구", "3");

        // 대구 (4)
        CITY_TO_AREA_CODE_MAP.put("수성", "4"); CITY_TO_AREA_CODE_MAP.put("수성구", "4");
        CITY_TO_AREA_CODE_MAP.put("달서", "4"); CITY_TO_AREA_CODE_MAP.put("달서구", "4");

        // 부산 (6)
        CITY_TO_AREA_CODE_MAP.put("해운대", "6"); CITY_TO_AREA_CODE_MAP.put("해운대구", "6");
        CITY_TO_AREA_CODE_MAP.put("부산진", "6"); CITY_TO_AREA_CODE_MAP.put("부산진구", "6");
        CITY_TO_AREA_CODE_MAP.put("기장", "6"); CITY_TO_AREA_CODE_MAP.put("기장군", "6");
        CITY_TO_AREA_CODE_MAP.put("수영", "6"); CITY_TO_AREA_CODE_MAP.put("수영구", "6");

        // 울산 (7)
        CITY_TO_AREA_CODE_MAP.put("울주", "7"); CITY_TO_AREA_CODE_MAP.put("울주군", "7");

        // 경기 (31)
        CITY_TO_AREA_CODE_MAP.put("수원", "31"); CITY_TO_AREA_CODE_MAP.put("수원시", "31");
        CITY_TO_AREA_CODE_MAP.put("용인", "31"); CITY_TO_AREA_CODE_MAP.put("용인시", "31");
        CITY_TO_AREA_CODE_MAP.put("고양", "31"); CITY_TO_AREA_CODE_MAP.put("고양시", "31");
        CITY_TO_AREA_CODE_MAP.put("성남", "31"); CITY_TO_AREA_CODE_MAP.put("성남시", "31");
        CITY_TO_AREA_CODE_MAP.put("화성", "31"); CITY_TO_AREA_CODE_MAP.put("화성시", "31");
        CITY_TO_AREA_CODE_MAP.put("부천", "31"); CITY_TO_AREA_CODE_MAP.put("부천시", "31");
        CITY_TO_AREA_CODE_MAP.put("남양주", "31"); CITY_TO_AREA_CODE_MAP.put("남양주시", "31");
        CITY_TO_AREA_CODE_MAP.put("안산", "31"); CITY_TO_AREA_CODE_MAP.put("안산시", "31");
        CITY_TO_AREA_CODE_MAP.put("평택", "31"); CITY_TO_AREA_CODE_MAP.put("평택시", "31");
        CITY_TO_AREA_CODE_MAP.put("안양", "31"); CITY_TO_AREA_CODE_MAP.put("안양시", "31");
        CITY_TO_AREA_CODE_MAP.put("파주", "31"); CITY_TO_AREA_CODE_MAP.put("파주시", "31");
        CITY_TO_AREA_CODE_MAP.put("의정부", "31"); CITY_TO_AREA_CODE_MAP.put("의정부시", "31");
        CITY_TO_AREA_CODE_MAP.put("김포", "31"); CITY_TO_AREA_CODE_MAP.put("김포시", "31");
        CITY_TO_AREA_CODE_MAP.put("광명", "31"); CITY_TO_AREA_CODE_MAP.put("광명시", "31");
        CITY_TO_AREA_CODE_MAP.put("경기광주", "31"); CITY_TO_AREA_CODE_MAP.put("광주시", "31");
        CITY_TO_AREA_CODE_MAP.put("군포", "31"); CITY_TO_AREA_CODE_MAP.put("군포시", "31");
        CITY_TO_AREA_CODE_MAP.put("이천", "31"); CITY_TO_AREA_CODE_MAP.put("이천시", "31");
        CITY_TO_AREA_CODE_MAP.put("오산", "31"); CITY_TO_AREA_CODE_MAP.put("오산시", "31");
        CITY_TO_AREA_CODE_MAP.put("하남", "31"); CITY_TO_AREA_CODE_MAP.put("하남시", "31");
        CITY_TO_AREA_CODE_MAP.put("시흥", "31"); CITY_TO_AREA_CODE_MAP.put("시흥시", "31");
        CITY_TO_AREA_CODE_MAP.put("양주", "31"); CITY_TO_AREA_CODE_MAP.put("양주시", "31");
        CITY_TO_AREA_CODE_MAP.put("구리", "31"); CITY_TO_AREA_CODE_MAP.put("구리시", "31");
        CITY_TO_AREA_CODE_MAP.put("안성", "31"); CITY_TO_AREA_CODE_MAP.put("안성시", "31");
        CITY_TO_AREA_CODE_MAP.put("포천", "31"); CITY_TO_AREA_CODE_MAP.put("포천시", "31");
        CITY_TO_AREA_CODE_MAP.put("의왕", "31"); CITY_TO_AREA_CODE_MAP.put("의왕시", "31");
        CITY_TO_AREA_CODE_MAP.put("여주", "31"); CITY_TO_AREA_CODE_MAP.put("여주시", "31");
        CITY_TO_AREA_CODE_MAP.put("동두천", "31"); CITY_TO_AREA_CODE_MAP.put("동두천시", "31");
        CITY_TO_AREA_CODE_MAP.put("과천", "31"); CITY_TO_AREA_CODE_MAP.put("과천시", "31");
        CITY_TO_AREA_CODE_MAP.put("가평", "31"); CITY_TO_AREA_CODE_MAP.put("가평군", "31");
        CITY_TO_AREA_CODE_MAP.put("양평", "31"); CITY_TO_AREA_CODE_MAP.put("양평군", "31");
        CITY_TO_AREA_CODE_MAP.put("연천", "31"); CITY_TO_AREA_CODE_MAP.put("연천군", "31");

        // 강원 (32)
        CITY_TO_AREA_CODE_MAP.put("춘천", "32"); CITY_TO_AREA_CODE_MAP.put("춘천시", "32");
        CITY_TO_AREA_CODE_MAP.put("원주", "32"); CITY_TO_AREA_CODE_MAP.put("원주시", "32");
        CITY_TO_AREA_CODE_MAP.put("강릉", "32"); CITY_TO_AREA_CODE_MAP.put("강릉시", "32");
        CITY_TO_AREA_CODE_MAP.put("동해", "32"); CITY_TO_AREA_CODE_MAP.put("동해시", "32");
        CITY_TO_AREA_CODE_MAP.put("속초", "32"); CITY_TO_AREA_CODE_MAP.put("속초시", "32");
        CITY_TO_AREA_CODE_MAP.put("삼척", "32"); CITY_TO_AREA_CODE_MAP.put("삼척시", "32");
        CITY_TO_AREA_CODE_MAP.put("태백", "32"); CITY_TO_AREA_CODE_MAP.put("태백시", "32");
        CITY_TO_AREA_CODE_MAP.put("홍천", "32"); CITY_TO_AREA_CODE_MAP.put("홍천군", "32");
        CITY_TO_AREA_CODE_MAP.put("횡성", "32"); CITY_TO_AREA_CODE_MAP.put("횡성군", "32");
        CITY_TO_AREA_CODE_MAP.put("영월", "32"); CITY_TO_AREA_CODE_MAP.put("영월군", "32");
        CITY_TO_AREA_CODE_MAP.put("평창", "32"); CITY_TO_AREA_CODE_MAP.put("평창군", "32");
        CITY_TO_AREA_CODE_MAP.put("정선", "32"); CITY_TO_AREA_CODE_MAP.put("정선군", "32");
        CITY_TO_AREA_CODE_MAP.put("철원", "32"); CITY_TO_AREA_CODE_MAP.put("철원군", "32");
        CITY_TO_AREA_CODE_MAP.put("화천", "32"); CITY_TO_AREA_CODE_MAP.put("화천군", "32");
        CITY_TO_AREA_CODE_MAP.put("양구", "32"); CITY_TO_AREA_CODE_MAP.put("양구군", "32");
        CITY_TO_AREA_CODE_MAP.put("인제", "32"); CITY_TO_AREA_CODE_MAP.put("인제군", "32");
        CITY_TO_AREA_CODE_MAP.put("강원고성", "32"); CITY_TO_AREA_CODE_MAP.put("고성군(강원)", "32");
        CITY_TO_AREA_CODE_MAP.put("양양", "32"); CITY_TO_AREA_CODE_MAP.put("양양군", "32");

        // 충북 (33)
        CITY_TO_AREA_CODE_MAP.put("청주", "33"); CITY_TO_AREA_CODE_MAP.put("청주시", "33");
        CITY_TO_AREA_CODE_MAP.put("충주", "33"); CITY_TO_AREA_CODE_MAP.put("충주시", "33");
        CITY_TO_AREA_CODE_MAP.put("제천", "33"); CITY_TO_AREA_CODE_MAP.put("제천시", "33");
        CITY_TO_AREA_CODE_MAP.put("보은", "33"); CITY_TO_AREA_CODE_MAP.put("보은군", "33");
        CITY_TO_AREA_CODE_MAP.put("옥천", "33"); CITY_TO_AREA_CODE_MAP.put("옥천군", "33");
        CITY_TO_AREA_CODE_MAP.put("영동", "33"); CITY_TO_AREA_CODE_MAP.put("영동군", "33");
        CITY_TO_AREA_CODE_MAP.put("증평", "33"); CITY_TO_AREA_CODE_MAP.put("증평군", "33");
        CITY_TO_AREA_CODE_MAP.put("진천", "33"); CITY_TO_AREA_CODE_MAP.put("진천군", "33");
        CITY_TO_AREA_CODE_MAP.put("괴산", "33"); CITY_TO_AREA_CODE_MAP.put("괴산군", "33");
        CITY_TO_AREA_CODE_MAP.put("음성", "33"); CITY_TO_AREA_CODE_MAP.put("음성군", "33");
        CITY_TO_AREA_CODE_MAP.put("단양", "33"); CITY_TO_AREA_CODE_MAP.put("단양군", "33");

        // 충남 (34)
        CITY_TO_AREA_CODE_MAP.put("천안", "34"); CITY_TO_AREA_CODE_MAP.put("천안시", "34");
        CITY_TO_AREA_CODE_MAP.put("공주", "34"); CITY_TO_AREA_CODE_MAP.put("공주시", "34");
        CITY_TO_AREA_CODE_MAP.put("보령", "34"); CITY_TO_AREA_CODE_MAP.put("보령시", "34");
        CITY_TO_AREA_CODE_MAP.put("아산", "34"); CITY_TO_AREA_CODE_MAP.put("아산시", "34");
        CITY_TO_AREA_CODE_MAP.put("서산", "34"); CITY_TO_AREA_CODE_MAP.put("서산시", "34");
        CITY_TO_AREA_CODE_MAP.put("논산", "34"); CITY_TO_AREA_CODE_MAP.put("논산시", "34");
        CITY_TO_AREA_CODE_MAP.put("계룡", "34"); CITY_TO_AREA_CODE_MAP.put("계룡시", "34");
        CITY_TO_AREA_CODE_MAP.put("당진", "34"); CITY_TO_AREA_CODE_MAP.put("당진시", "34");
        CITY_TO_AREA_CODE_MAP.put("금산", "34"); CITY_TO_AREA_CODE_MAP.put("금산군", "34");
        CITY_TO_AREA_CODE_MAP.put("부여", "34"); CITY_TO_AREA_CODE_MAP.put("부여군", "34");
        CITY_TO_AREA_CODE_MAP.put("서천", "34"); CITY_TO_AREA_CODE_MAP.put("서천군", "34");
        CITY_TO_AREA_CODE_MAP.put("청양", "34"); CITY_TO_AREA_CODE_MAP.put("청양군", "34");
        CITY_TO_AREA_CODE_MAP.put("홍성", "34"); CITY_TO_AREA_CODE_MAP.put("홍성군", "34");
        CITY_TO_AREA_CODE_MAP.put("예산", "34"); CITY_TO_AREA_CODE_MAP.put("예산군", "34");
        CITY_TO_AREA_CODE_MAP.put("태안", "34"); CITY_TO_AREA_CODE_MAP.put("태안군", "34");
        CITY_TO_AREA_CODE_MAP.put("태안남부", "34"); CITY_TO_AREA_CODE_MAP.put("태안군", "34");
        CITY_TO_AREA_CODE_MAP.put("태안북부", "34"); CITY_TO_AREA_CODE_MAP.put("태안군", "34");

        // 경북 (35)
        CITY_TO_AREA_CODE_MAP.put("포항", "35"); CITY_TO_AREA_CODE_MAP.put("포항시", "35");
        CITY_TO_AREA_CODE_MAP.put("경주", "35"); CITY_TO_AREA_CODE_MAP.put("경주시", "35");
        CITY_TO_AREA_CODE_MAP.put("김천", "35"); CITY_TO_AREA_CODE_MAP.put("김천시", "35");
        CITY_TO_AREA_CODE_MAP.put("안동", "35"); CITY_TO_AREA_CODE_MAP.put("안동시", "35");
        CITY_TO_AREA_CODE_MAP.put("구미", "35"); CITY_TO_AREA_CODE_MAP.put("구미시", "35");
        CITY_TO_AREA_CODE_MAP.put("영주", "35"); CITY_TO_AREA_CODE_MAP.put("영주시", "35");
        CITY_TO_AREA_CODE_MAP.put("영천", "35"); CITY_TO_AREA_CODE_MAP.put("영천시", "35");
        CITY_TO_AREA_CODE_MAP.put("상주", "35"); CITY_TO_AREA_CODE_MAP.put("상주시", "35");
        CITY_TO_AREA_CODE_MAP.put("문경", "35"); CITY_TO_AREA_CODE_MAP.put("문경시", "35");
        CITY_TO_AREA_CODE_MAP.put("경산", "35"); CITY_TO_AREA_CODE_MAP.put("경산시", "35");
        CITY_TO_AREA_CODE_MAP.put("군위", "35"); CITY_TO_AREA_CODE_MAP.put("군위군", "35");
        CITY_TO_AREA_CODE_MAP.put("의성", "35"); CITY_TO_AREA_CODE_MAP.put("의성군", "35");
        CITY_TO_AREA_CODE_MAP.put("청송", "35"); CITY_TO_AREA_CODE_MAP.put("청송군", "35");
        CITY_TO_AREA_CODE_MAP.put("영양", "35"); CITY_TO_AREA_CODE_MAP.put("영양군", "35");
        CITY_TO_AREA_CODE_MAP.put("영덕", "35"); CITY_TO_AREA_CODE_MAP.put("영덕군", "35");
        CITY_TO_AREA_CODE_MAP.put("청도", "35"); CITY_TO_AREA_CODE_MAP.put("청도군", "35");
        CITY_TO_AREA_CODE_MAP.put("고령", "35"); CITY_TO_AREA_CODE_MAP.put("고령군", "35");
        CITY_TO_AREA_CODE_MAP.put("성주", "35"); CITY_TO_AREA_CODE_MAP.put("성주군", "35");
        CITY_TO_AREA_CODE_MAP.put("칠곡", "35"); CITY_TO_AREA_CODE_MAP.put("칠곡군", "35");
        CITY_TO_AREA_CODE_MAP.put("예천", "35"); CITY_TO_AREA_CODE_MAP.put("예천군", "35");
        CITY_TO_AREA_CODE_MAP.put("봉화", "35"); CITY_TO_AREA_CODE_MAP.put("봉화군", "35");
        CITY_TO_AREA_CODE_MAP.put("울진", "35"); CITY_TO_AREA_CODE_MAP.put("울진군", "35");
        CITY_TO_AREA_CODE_MAP.put("울릉", "35"); CITY_TO_AREA_CODE_MAP.put("울릉군", "35");

        // 경남 (36)
        CITY_TO_AREA_CODE_MAP.put("창원", "36"); CITY_TO_AREA_CODE_MAP.put("창원시", "36");
        CITY_TO_AREA_CODE_MAP.put("진주", "36"); CITY_TO_AREA_CODE_MAP.put("진주시", "36");
        CITY_TO_AREA_CODE_MAP.put("통영", "36"); CITY_TO_AREA_CODE_MAP.put("통영시", "36");
        CITY_TO_AREA_CODE_MAP.put("사천", "36"); CITY_TO_AREA_CODE_MAP.put("사천시", "36");
        CITY_TO_AREA_CODE_MAP.put("김해", "36"); CITY_TO_AREA_CODE_MAP.put("김해시", "36");
        CITY_TO_AREA_CODE_MAP.put("밀양", "36"); CITY_TO_AREA_CODE_MAP.put("밀양시", "36");
        CITY_TO_AREA_CODE_MAP.put("거제", "36"); CITY_TO_AREA_CODE_MAP.put("거제시", "36");
        CITY_TO_AREA_CODE_MAP.put("양산", "36"); CITY_TO_AREA_CODE_MAP.put("양산시", "36");
        CITY_TO_AREA_CODE_MAP.put("의령", "36"); CITY_TO_AREA_CODE_MAP.put("의령군", "36");
        CITY_TO_AREA_CODE_MAP.put("함안", "36"); CITY_TO_AREA_CODE_MAP.put("함안군", "36");
        CITY_TO_AREA_CODE_MAP.put("창녕", "36"); CITY_TO_AREA_CODE_MAP.put("창녕군", "36");
        CITY_TO_AREA_CODE_MAP.put("경남고성", "36"); CITY_TO_AREA_CODE_MAP.put("고성군(경남)", "36");
        CITY_TO_AREA_CODE_MAP.put("남해", "36"); CITY_TO_AREA_CODE_MAP.put("남해군", "36");
        CITY_TO_AREA_CODE_MAP.put("하동", "36"); CITY_TO_AREA_CODE_MAP.put("하동군", "36");
        CITY_TO_AREA_CODE_MAP.put("산청", "36"); CITY_TO_AREA_CODE_MAP.put("산청군", "36");
        CITY_TO_AREA_CODE_MAP.put("함양", "36"); CITY_TO_AREA_CODE_MAP.put("함양군", "36");
        CITY_TO_AREA_CODE_MAP.put("거창", "36"); CITY_TO_AREA_CODE_MAP.put("거창군", "36");
        CITY_TO_AREA_CODE_MAP.put("합천", "36"); CITY_TO_AREA_CODE_MAP.put("합천군", "36");

        // 전북 (37)
        CITY_TO_AREA_CODE_MAP.put("전주", "37"); CITY_TO_AREA_CODE_MAP.put("전주시", "37");
        CITY_TO_AREA_CODE_MAP.put("군산", "37"); CITY_TO_AREA_CODE_MAP.put("군산시", "37");
        CITY_TO_AREA_CODE_MAP.put("익산", "37"); CITY_TO_AREA_CODE_MAP.put("익산시", "37");
        CITY_TO_AREA_CODE_MAP.put("정읍", "37"); CITY_TO_AREA_CODE_MAP.put("정읍시", "37");
        CITY_TO_AREA_CODE_MAP.put("남원", "37"); CITY_TO_AREA_CODE_MAP.put("남원시", "37");
        CITY_TO_AREA_CODE_MAP.put("김제", "37"); CITY_TO_AREA_CODE_MAP.put("김제시", "37");
        CITY_TO_AREA_CODE_MAP.put("완주", "37"); CITY_TO_AREA_CODE_MAP.put("완주군", "37");
        CITY_TO_AREA_CODE_MAP.put("진안", "37"); CITY_TO_AREA_CODE_MAP.put("진안군", "37");
        CITY_TO_AREA_CODE_MAP.put("무주", "37"); CITY_TO_AREA_CODE_MAP.put("무주군", "37");
        CITY_TO_AREA_CODE_MAP.put("장수", "37"); CITY_TO_AREA_CODE_MAP.put("장수군", "37");
        CITY_TO_AREA_CODE_MAP.put("임실", "37"); CITY_TO_AREA_CODE_MAP.put("임실군", "37");
        CITY_TO_AREA_CODE_MAP.put("순창", "37"); CITY_TO_AREA_CODE_MAP.put("순창군", "37");
        CITY_TO_AREA_CODE_MAP.put("고창", "37"); CITY_TO_AREA_CODE_MAP.put("고창군", "37");
        CITY_TO_AREA_CODE_MAP.put("부안", "37"); CITY_TO_AREA_CODE_MAP.put("부안군", "37");

        // 전남 (38)
        CITY_TO_AREA_CODE_MAP.put("목포", "38"); CITY_TO_AREA_CODE_MAP.put("목포시", "38");
        CITY_TO_AREA_CODE_MAP.put("여수", "38"); CITY_TO_AREA_CODE_MAP.put("여수시", "38");
        CITY_TO_AREA_CODE_MAP.put("순천", "38"); CITY_TO_AREA_CODE_MAP.put("순천시", "38");
        CITY_TO_AREA_CODE_MAP.put("나주", "38"); CITY_TO_AREA_CODE_MAP.put("나주시", "38");
        CITY_TO_AREA_CODE_MAP.put("광양", "38"); CITY_TO_AREA_CODE_MAP.put("광양시", "38");
        CITY_TO_AREA_CODE_MAP.put("담양", "38"); CITY_TO_AREA_CODE_MAP.put("담양군", "38");
        CITY_TO_AREA_CODE_MAP.put("곡성", "38"); CITY_TO_AREA_CODE_MAP.put("곡성군", "38");
        CITY_TO_AREA_CODE_MAP.put("구례", "38"); CITY_TO_AREA_CODE_MAP.put("구례군", "38");
        CITY_TO_AREA_CODE_MAP.put("고흥", "38"); CITY_TO_AREA_CODE_MAP.put("고흥군", "38");
        CITY_TO_AREA_CODE_MAP.put("보성", "38"); CITY_TO_AREA_CODE_MAP.put("보성군", "38");
        CITY_TO_AREA_CODE_MAP.put("화순", "38"); CITY_TO_AREA_CODE_MAP.put("화순군", "38");
        CITY_TO_AREA_CODE_MAP.put("장흥", "38"); CITY_TO_AREA_CODE_MAP.put("장흥군", "38");
        CITY_TO_AREA_CODE_MAP.put("강진", "38"); CITY_TO_AREA_CODE_MAP.put("강진군", "38");
        CITY_TO_AREA_CODE_MAP.put("해남", "38"); CITY_TO_AREA_CODE_MAP.put("해남군", "38");
        CITY_TO_AREA_CODE_MAP.put("영암", "38"); CITY_TO_AREA_CODE_MAP.put("영암군", "38");
        CITY_TO_AREA_CODE_MAP.put("무안", "38"); CITY_TO_AREA_CODE_MAP.put("무안군", "38");
        CITY_TO_AREA_CODE_MAP.put("함평", "38"); CITY_TO_AREA_CODE_MAP.put("함평군", "38");
        CITY_TO_AREA_CODE_MAP.put("영광", "38"); CITY_TO_AREA_CODE_MAP.put("영광군", "38");
        CITY_TO_AREA_CODE_MAP.put("장성", "38"); CITY_TO_AREA_CODE_MAP.put("장성군", "38");
        CITY_TO_AREA_CODE_MAP.put("완도", "38"); CITY_TO_AREA_CODE_MAP.put("완도군", "38");
        CITY_TO_AREA_CODE_MAP.put("진도", "38"); CITY_TO_AREA_CODE_MAP.put("진도군", "38");
        CITY_TO_AREA_CODE_MAP.put("신안", "38"); CITY_TO_AREA_CODE_MAP.put("신안군", "38");

        // 제주 (39)
        CITY_TO_AREA_CODE_MAP.put("서귀포", "39"); CITY_TO_AREA_CODE_MAP.put("서귀포시", "39");
    }

    public PublicDataService(WebClient.Builder webClientBuilder, ObjectMapper objectMapper) {
        this.webClient = webClientBuilder.build();
        this.objectMapper = objectMapper;
    }


    private Mono<List<Map<String, String>>> callApi(String baseUrl, String serviceKey, Map<String, String> params, String itemsPath) {
        UriComponentsBuilder uriBuilder = UriComponentsBuilder.fromUriString(baseUrl)
                .queryParam("ServiceKey", serviceKey);

        if (baseUrl.contains("KorService1")) {
            uriBuilder.queryParam("_type", "json")
                    .queryParam("numOfRows", params.getOrDefault("numOfRows", "100"))
                    .queryParam("pageNo", params.getOrDefault("pageNo", "1"))
                    .queryParam("MobileOS", "ETC")
                    .queryParam("MobileApp", "AppTest")
                    .queryParam("listYN", "Y")
                    .queryParam("arrange", params.getOrDefault("arrange", "A"));
        }

        params.forEach((key, value) -> {
            if (!key.equals("ServiceKey") && !key.equals("_type") && !key.equals("numOfRows") &&
                !key.equals("pageNo") && !key.equals("MobileOS") && !key.equals("MobileApp") &&
                !key.equals("listYN") && !key.equals("arrange")) {
                try {
                    if (value == null || value.isEmpty()) {
                        System.out.println("INFO: Skipping empty parameter: " + key);
                        return;
                    }
                    if (key.equals("areaCode") || key.equals("contentTypeId") || key.equals("sigunguCode") ||
                        key.equals("cat1") || key.equals("cat2") || key.equals("cat3") ||
                        (!value.isEmpty() && Character.isDigit(value.charAt(0)))) {
                        uriBuilder.queryParam(key, value);
                    } else {
                        uriBuilder.queryParam(key, URLEncoder.encode(value, StandardCharsets.UTF_8.toString()));
                    }
                } catch (Exception e) {
                    System.err.println("ERROR: Query parameter encoding error: " + key + "=" + value + " - " + e.getMessage());
                }
            }
        });

        URI finalUri = uriBuilder.build(true).toUri();
        System.out.println("DEBUG: API call URI: " + finalUri.toString());

        return webClient.get()
                .uri(finalUri)
                .retrieve()
                .bodyToMono(String.class)
                .flatMap(jsonResponse -> {
                    System.out.println("DEBUG: API raw response (Base URL: " + baseUrl + "): " + jsonResponse);
                    try {
                        JsonNode root = objectMapper.readTree(jsonResponse);
                        JsonNode header = root.path("response").path("header");
                        String resultCode = header.path("resultCode").asText("N/A");
                        String resultMsg = header.path("resultMsg").asText("No Message");

                        if (!resultCode.equals("0000") && !resultCode.equals("00")) {
                            System.err.println("ERROR: Public Data API response error (Result Code: " + resultCode + ", Message: " + resultMsg + ")");
                            if (jsonResponse.trim().startsWith("<")) {
                                return Mono.error(new RuntimeException("Public Data API response is in XML format. Possible API key or parameter error."));
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
                        } else if (itemsNode.isObject() && !itemsNode.isEmpty()) {
                            Map<String, String> itemMap = new HashMap<>();
                            itemsNode.fields().forEachRemaining(entry -> { // item -> itemsNode로 수정
                                itemMap.put(entry.getKey(), entry.getValue().asText(""));
                            });
                            resultList.add(itemMap);
                        } else {
                            int totalCount = root.path("response").path("body").path("totalCount").asInt(0);
                            System.out.println("INFO: No data in API response (totalCount: " + totalCount + ", itemsNode: " + itemsNode.getNodeType() + ")");
                            return Mono.just(Collections.<Map<String, String>>emptyList());
                        }
                        System.out.println("DEBUG: Parsed API response items: " + resultList.size() + " items");
                        return Mono.just(resultList);
                    } catch (Exception e) {
                        System.err.println("ERROR: API response parsing error: " + e.getMessage() + ", Response: " + jsonResponse);
                        if (jsonResponse.trim().startsWith("<")) {
                            return Mono.error(new RuntimeException("Public Data API response is in XML format. Possible API key or parameter error."));
                        }
                        return Mono.error(new RuntimeException("Failed to parse Public Data API response: " + e.getMessage()));
                    }
                })
                .onErrorResume(e -> {
                    System.err.println("ERROR: API call error (Base URL: " + baseUrl + "): " + e.getMessage());
                    return Mono.just(Collections.<Map<String, String>>emptyList());
                });
    }

    public Mono<List<Map<String, String>>> getSeaInfo(Map<String, String> params) {
        System.out.println("INFO: PublicDataService - getSeaInfo method called but sea info service is disabled.");
        return Mono.just(Collections.emptyList());
    }

    public Mono<List<Map<String, String>>> getRestaurantInfo(Map<String, String> params) {
        String baseUrl = "http://apis.data.go.kr/B551011/KorService1/areaBasedList1";
        Map<String, String> apiParams = new HashMap<>();
        apiParams.put("contentTypeId", "39");

        String locationName = params.getOrDefault("location", "서울");
        String areaCode = convertLocationToAreaCode(locationName);
        apiParams.put("areaCode", areaCode);

        String sigunguCode = params.get("sigunguCode");
        if (sigunguCode != null && !sigunguCode.isEmpty()) {
            apiParams.put("sigunguCode", sigunguCode);
        }

        apiParams.put("numOfRows", params.getOrDefault("numOfRows", "10"));
        apiParams.put("arrange", params.getOrDefault("arrange", "B"));
        
        String restaurantApiKey = apiKeysConfig.getRestaurantApiKey();
        System.out.println("DEBUG: getRestaurantInfo params: " + apiParams);
        return callApi(baseUrl, restaurantApiKey, apiParams, "item");
    }

    public Mono<List<Map<String, String>>> getAccommodationInfo(Map<String, String> params) {
        String baseUrl = "http://apis.data.go.kr/B551011/KorService1/searchStay1";
        Map<String, String> apiParams = new HashMap<>();

        String locationName = params.getOrDefault("location", "서울");
        String areaCode = convertLocationToAreaCode(locationName);
        apiParams.put("areaCode", areaCode);

        String sigunguCode = params.get("sigunguCode");
        if (sigunguCode != null && !sigunguCode.isEmpty()) {
            apiParams.put("sigunguCode", sigunguCode);
        }

        apiParams.put("numOfRows", params.getOrDefault("numOfRows", "10"));
        apiParams.put("arrange", params.getOrDefault("arrange", "B"));

        System.out.println("DEBUG: getAccommodationInfo params: " + apiParams);
        String accommodationApiKey = apiKeysConfig.getAccomApiKey();
        return callApi(baseUrl, accommodationApiKey, apiParams, "item");
    }

    private String convertLocationToAreaCode(String locationName) {
        if (locationName == null || locationName.trim().isEmpty()) {
            System.out.println("WARN: Input location name is empty. Using default '1' (Seoul).");
            return "1";
        }

        String trimmedLocationName = locationName.trim();

        // 0. "고성"과 같이 도 이름이 명시된 경우 처리
        if (trimmedLocationName.startsWith("강원") && trimmedLocationName.contains("고성")) {
            return "32";
        }
        if (trimmedLocationName.startsWith("경남") && trimmedLocationName.contains("고성")) {
            return "36";
        }
        if (trimmedLocationName.startsWith("경기") && trimmedLocationName.contains("광주")) {
            return "31";
        }

        // 1. 주요 시/군/구 이름으로 직접 매칭
        if (CITY_TO_AREA_CODE_MAP.containsKey(trimmedLocationName)) {
            return CITY_TO_AREA_CODE_MAP.get(trimmedLocationName);
        }

        // 2. 광역 지자체 이름으로 직접 매칭
        if (AREA_CODE_MAP.containsKey(trimmedLocationName)) {
            return AREA_CODE_MAP.get(trimmedLocationName);
        }

        // 3. 접미사 제거 후 재시도
        String baseLocationName = trimmedLocationName
                .replace("특별시", "")
                .replace("광역시", "")
                .replace("특별자치시", "")
                .replace("특별자치도", "")
                .replace("도", "")
                .replace("시", "")
                .replace("군", "")
                .replace("구", "")
                .trim();

        if (!baseLocationName.isEmpty() && !baseLocationName.equals(trimmedLocationName)) {
            if (CITY_TO_AREA_CODE_MAP.containsKey(baseLocationName)) {
                return CITY_TO_AREA_CODE_MAP.get(baseLocationName);
            }
            if (AREA_CODE_MAP.containsKey(baseLocationName)) {
                return AREA_CODE_MAP.get(baseLocationName);
            }
        }

        // 4. 부분 일치 검색
        for (Map.Entry<String, String> entry : CITY_TO_AREA_CODE_MAP.entrySet()) {
            String cityKey = entry.getKey();
            String strippedCityKey = cityKey.replace("시", "").replace("군", "").replace("구", "");
            if (cityKey.startsWith(baseLocationName) || strippedCityKey.equals(baseLocationName)) {
                if (!baseLocationName.equals("중") && !baseLocationName.equals("서") && 
                    !baseLocationName.equals("동") && !baseLocationName.equals("남") && 
                    !baseLocationName.equals("북")) {
                    System.out.println("INFO: Matched '" + trimmedLocationName + "' to '" + cityKey + "' with area code '" + entry.getValue() + "'.");
                    return entry.getValue();
                }
            }
        }

        // 5. 특수 케이스
        if (baseLocationName.equals("고성")) {
            System.out.println("WARN: '고성' input defaults to Gangwon-do (32). Use '경남 고성' for Gyeongsangnam-do.");
            return "32";
        }
        if (baseLocationName.equals("광주")) {
            System.out.println("WARN: '광주' input defaults to Gwangju City (5). Use '경기 광주' for Gyeonggi-do.");
            return "5";
        }

        System.out.println("WARN: No area code found for '" + trimmedLocationName + "' (base: '" + baseLocationName + "'). Using default '1' (Seoul).");
        return "1";
    }
}