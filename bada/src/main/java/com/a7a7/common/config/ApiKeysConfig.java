package com.a7a7.common.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class ApiKeysConfig {
	
    @Value("${kakao.map.api}")
    private String kakaoMapApiKey;
    
    @Value("${sea.api.key}")
    private String seaApiKey;

    @Value("${sea.api.dcokey}")
    private String seaApiDcokey;

    @Value("${accom.api.key}")
    private String accomApiKey;

    @Value("${accom.api.dcokey}")
    private String accomApiDcokey;

    @Value("${restaurant.api.key}")
    private String restaurantApiKey;

    @Value("${restaurant.api.dcokey}")
    private String restaurantApiDcokey;

    @Value("${toss.pay.api}")
    private String tossPayApiKey;

    @Value("${gemini.api.key}")
    private String geminiApiKey;

    @Value("${spring.mail.username}")
    private String gmailApiKey;

    public String getKakaoMapApiKey() {
        return kakaoMapApiKey;
    }

    public String getSeaApiKey() {
        return seaApiKey;
    }

    public String getSeaApiDcokey() {
        return seaApiDcokey;
    }

    public String getAccomApiKey() {
        return accomApiKey;
    }

    public String getAccomApiDcokey() {
        return accomApiDcokey;
    }

    public String getRestaurantApiKey() {
        return restaurantApiKey;
    }

    public String getRestaurantApiDcokey() {
        return restaurantApiDcokey;
    }

    public String getTossPayApiKey() {
        return tossPayApiKey;
    }

    public String getGeminiApiKey() {
        return geminiApiKey;
    }
    
    public String getGmailApiKey() {
        return gmailApiKey;
    }
}
