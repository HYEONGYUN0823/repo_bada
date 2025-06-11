package com.a7a7.common.config;

import org.springframework.stereotype.Component;

@Component
public class ApiKeysConfig {
    public String getKakaoMapApiKey() {
        return System.getenv("KAKAO_MAP_API");
    }

    public String getSeaApiKey() {
        return System.getenv("SEA_API_KEY");
    }

    public String getSeaApiDcokey() {
        return System.getenv("SEA_API_DCOKEY");
    }

    public String getAccomApiKey() {
        return System.getenv("ACCOM_API_KEY");
    }

    public String getAccomApiDcokey() {
        return System.getenv("ACCOM_API_DCOKEY");
    }

    public String getRestaurantApiKey() {
        return System.getenv("RESTAURANT_API_KEY");
    }

    public String getRestaurantApiDcokey() {
        return System.getenv("RESTAURANT_API_DCOKEY");
    }

    public String getTossPayApiKey() {
        return System.getenv("TOSSPAY_API");
    }

    public String getGeminiApiKey() {
        return System.getenv("GEMINI_API_KEY");
    }
}