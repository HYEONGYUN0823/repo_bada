package com.a7a7.module.accom;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.ObjectMapper;

@Service
public class AccomService {
	
	@Value("${accom_api_key}")
	private String serviceKey;

	public void saveAccomApiResponse() throws Exception {
		
		String apiUrl = "http://apis.data.go.kr/B551011/KorService1/searchStay1?listYN=Y&MobileOS=ETC&MobileApp=AppTest&arrange=A&numOfRows=20&pageNo=1&_type=json&ServiceKey=" + serviceKey;
		
		URL url = new URL(apiUrl);
		HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
		httpURLConnection.setRequestMethod("GET");
		
		BufferedReader bufferedReader;
		if (httpURLConnection.getResponseCode() >= 200 && httpURLConnection.getResponseCode() <= 300) {
			bufferedReader = new BufferedReader(new InputStreamReader(httpURLConnection.getInputStream()));
		} else {
			bufferedReader = new BufferedReader(new InputStreamReader(httpURLConnection.getErrorStream()));
		}
		
		StringBuilder stringBuilder = new StringBuilder();
		String line;
		while ((line = bufferedReader.readLine()) != null) {
			System.out.println("line: " + line);
			stringBuilder.append(line);
		}

		bufferedReader.close();
		httpURLConnection.disconnect();

		ObjectMapper objectMapper = new ObjectMapper();
		AccomApiDto dto = objectMapper.readValue(stringBuilder.toString(), AccomApiDto.class);
		
	}
}
