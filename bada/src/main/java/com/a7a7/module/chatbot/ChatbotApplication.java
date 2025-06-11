//package com.a7a7.module.chatbot;
//
//import org.springframework.boot.SpringApplication;
//import org.springframework.boot.autoconfigure.SpringBootApplication;
//import org.springframework.context.annotation.Bean;
//import org.springframework.web.servlet.config.annotation.CorsRegistry;
//import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
//
//@SpringBootApplication
//public class ChatbotApplication {
//
//    public static void main(String[] args) {
//        SpringApplication.run(ChatbotApplication.class, args);
//    }
//
//    @Bean
//    public WebMvcConfigurer corsConfigurer() {
//        return new WebMvcConfigurer() {
//            @Override
//            public void addCorsMappings(CorsRegistry registry) {
//                registry.addMapping("/**")
//                        // 여기에 웹 페이지가 실행되는 정확한 도메인(URL)을 입력하세요.
//                        // 예: Live Server를 사용한다면 "http://127.0.0.1:5500" 또는 "http://localhost:5500"
//                        // 로컬에서 개발 테스트 중이고, Spring Boot가 8080 포트라면 자신도 포함
//                        .allowedOrigins("http://127.0.0.1:5500", "http://localhost:5500", "http://localhost:8080", "http://43.201.33.231:8888") // <-- 이 부분 수정
//                        .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS")
//                        .allowedHeaders("*")
////                        .allowCredentials(true)
//                        .maxAge(3600);
//            }
//        };
//    }
//}