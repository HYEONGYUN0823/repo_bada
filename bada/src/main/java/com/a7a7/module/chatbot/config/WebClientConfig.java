package com.a7a7.module.chatbot.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
public class WebClientConfig {

    @Bean
    public WebClient webClient() {
        // WebClient 인스턴스를 직접 빌드하여 반환합니다.
        // 여기서 baseUrl을 설정하지 않고, 각 API 호출에서 전체 URL을 제공합니다.
        return WebClient.builder()
                .build();
    }
}