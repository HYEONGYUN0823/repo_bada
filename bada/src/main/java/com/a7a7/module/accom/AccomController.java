package com.a7a7.module.accom;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

import com.fasterxml.jackson.databind.ObjectMapper;

@Controller
public class AccomController {
	
	@Value("${accom_api_key}")
	private String serviceKey;
	
	@RequestMapping("/xdm/accom/list")
	public String getAccomList(Model model) throws Exception {
		
		String apiUrl = "http://apis.data.go.kr/B551011/KorService1/searchStay1?areaCode=32&sigunguCode=1&listYN=Y&MobileOS=ETC&MobileApp=AppTest&arrange=A&numOfRows=12&pageNo=1&_type=json&ServiceKey=" + serviceKey;
		
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
		AccomDto dto = objectMapper.readValue(stringBuilder.toString(), AccomDto.class);
		
		model.addAttribute("list", dto.getResponse().getBody().getItems().getItem());
		
		return "xdm/accom/accomList";
	}
}
