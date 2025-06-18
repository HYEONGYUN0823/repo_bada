package com.a7a7.common.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class ApiKeysConfig {
	
    @Value("${kakao.map.api}")
    private String kakaoMapApiKey;
    
    @Value("${sea.api.key}")
    private String seaApiKey;

    @Value("${accom.api.key}")
    private String accomApiKey;

    @Value("${restaurant.api.key}")
    private String restaurantApiKey;

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
    
    public String getAccomApiKey() {
        return accomApiKey;
    }

    public String getRestaurantApiKey() {
        return restaurantApiKey;
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
