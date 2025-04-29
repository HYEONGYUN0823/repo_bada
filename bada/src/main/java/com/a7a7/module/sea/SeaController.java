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
	public String travel() {
		return "/xdm/travel/travel";
	}
	
	
	
	
	@RequestMapping(value = "/travelJsonNodeList")
	public String travelJsonNodeList(Model model) throws Exception {
		
		String apiUrl = "https://apis.data.go.kr/1192136/fcstSeaTrip/GetFcstSeaTripApiService?serviceKey=9GyQfz7WfYE5IWKlIQB2FaXDXc2h8lfc%2Fy871UUm%2BNBS1fO5SbIUk%2FeVUdFyctG2PfyrWJAhHCyeu6H5pYr1KQ%3D%3D&type=json&reqDate=&pageNo=1&numOfRows=300&include=lastScr,sareaDtlNm,lat,lot,predcYmd,predcNoonSeCd,avgArtmp,avgWspd,avgWtem,avgWvhgt,avgCrsp,weather,totalIndex";
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
		
		System.out.println("node.get(\"header\").get(\"resultCode\").asText(): " + node.get("header").get("resultCode").asText());
		System.out.println("node.get(\"header\").get(\"resultMsg\").asText(): " + node.get("header").get("resultMsg").asText());
		System.out.println("node.get(\"header\").get(\"resultMsg\").asText(): " + node.get("body").get("items").get(1).get("lastScr").asText());
		System.out.println("node.get(\"header\").get(\"resultMsg\").asText(): " + node.get("body").get("items").get(1).get("sareaDtlNm").asText());
		System.out.println("node.get(\"header\").get(\"resultMsg\").asText(): " + node.get("body").get("items").get(1).get("lat").asText());
		System.out.println("node.get(\"header\").get(\"resultMsg\").asText(): " + node.get("body").get("items").get(1).get("lot").asText());
		System.out.println("node.get(\"header\").get(\"resultMsg\").asText(): " + node.get("body").get("items").get(1).get("predcYmd").asText());
		System.out.println("node.get(\"header\").get(\"resultMsg\").asText(): " + node.get("body").get("items").get(1).get("predcNoonSeCd").asText());
		System.out.println("node.get(\"header\").get(\"resultMsg\").asText(): " + node.get("body").get("items").get(1).get("avgArtmp").asText());
		System.out.println("node.get(\"header\").get(\"resultMsg\").asText(): " + node.get("body").get("items").get(1).get("avgWspd").asText());
		System.out.println("node.get(\"header\").get(\"resultMsg\").asText(): " + node.get("body").get("items").get(1).get("avgWtem").asText());
		System.out.println("node.get(\"header\").get(\"resultMsg\").asText(): " + node.get("body").get("items").get(1).get("avgWvhgt").asText());
		System.out.println("node.get(\"header\").get(\"resultMsg\").asText(): " + node.get("body").get("items").get(1).get("avgCrsp").asText());
		System.out.println("node.get(\"header\").get(\"resultMsg\").asText(): " + node.get("body").get("items").get(1).get("weather").asText());
		System.out.println("node.get(\"header\").get(\"resultMsg\").asText(): " + node.get("body").get("items").get(1).get("totalIndex").asText());
		
		model.addAttribute("node", node);
		
		
		return "/xdm/travel/travel";
	}
	
	
}
