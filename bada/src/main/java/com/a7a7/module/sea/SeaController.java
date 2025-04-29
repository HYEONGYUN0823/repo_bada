package com.a7a7.module.sea;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

@Controller
public class SeaController {

	
	
	@RequestMapping(value="xdm/travel")
	public String travel(Model model) throws Exception {
		String apiUrl = "";
		URL url = new URL(apiUrl);
		HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection(); //openConnection() : 해당 URL로 연결을 여는 거야.
		httpURLConnection.setRequestMethod("GET"); //HttpURLConnection : HTTP 요청을 보낼 수 있는 클래스야., setRequestMethod("GET") : GET 방식으로 요청을 보낸다는 의미.
		
		BufferedReader bufferedReader; //BufferedReader는 줄 단위로 읽을 수 있게 도와줘.
		if (httpURLConnection.getResponseCode() == 200) {
			bufferedReader = new BufferedReader(new InputStreamReader(httpURLConnection.getInputStream())); //InputStreamReader는 바이트를 문자로 바꿔주는 클래스.
		} else {
			bufferedReader = new BufferedReader(new InputStreamReader(httpURLConnection.getErrorStream()));
		}
		
		StringBuilder stringBuilder = new StringBuilder();
		String line;
		while ((line = bufferedReader.readLine()) != null) { //한 줄씩 읽어서 StringBuilder에 붙여.
			System.out.println("line: " + line);
			stringBuilder.append(line);
		}

		bufferedReader.close();
		httpURLConnection.disconnect();

		ObjectMapper objectMapper = new ObjectMapper();			//ObjectMapper는 Jackson 라이브러리에서 제공하는 JSON 처리용 클래스야.
		JsonNode node = objectMapper.readTree(stringBuilder.toString()); //JsonNode node = objectMapper.readTree(stringBuilder.toString());
		
		JsonNode itemsNode = node.path("response").path("body").path("items").path("item"); 
		//이렇게 중첩된 구조에서 items는 객체이고, 그 안에 item 배열이 존재하기 때문에 items.get("item")으로 배열을 꺼내서 넘겨줘야 Thymeleaf에서 이를 순회하며 값을 출력할 수 있게 됩니다.
		model.addAttribute("items", itemsNode);
		return "/xdm/travel/travel";
	}
	
	
}
