package com.a7a7.module.pay;

import java.util.Base64;

import org.json.JSONObject;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class TossPayService {

    // 시크릿 키 (test 환경용)
    private final String SECRET_KEY = "test_sk_jExPeJWYVQbyN5LAEGxqr49R5gvN";

    public String confirmPayment(String paymentKey, String orderId, int amount) {
        String apiUrl = "https://api.tosspayments.com/v1/payments/confirm";

        // Authorization 헤더: Basic + base64 인코딩된 secretKey:
        String encodedAuth = Base64.getEncoder().encodeToString((SECRET_KEY + ":").getBytes());

        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Basic " + encodedAuth);
        headers.setContentType(MediaType.APPLICATION_JSON);

        // 요청 바디에 paymentKey, orderId, amount 넣기
        JSONObject requestBody = new JSONObject();
        requestBody.put("paymentKey", paymentKey);
        requestBody.put("orderId", orderId);
        requestBody.put("amount", amount);

        HttpEntity<String> request = new HttpEntity<>(requestBody.toString(), headers);

        // RestTemplate으로 POST 요청
        RestTemplate restTemplate = new RestTemplate();
        ResponseEntity<String> response = restTemplate.postForEntity(apiUrl, request, String.class);

        return response.getBody();
    }
}