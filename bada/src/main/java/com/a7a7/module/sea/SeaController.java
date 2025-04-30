package com.a7a7.module.sea;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

@Controller
public class SeaController {

	@Value("${sea_api_key}")
	private String serviceKey;
	
	@RequestMapping(value="xdm/travel")
	public String travel(Model model) throws Exception {
		String apiUrl = "https://apis.data.go.kr/1192136/fcstSeaTrip/GetFcstSeaTripApiService?&type=json&reqDate=&pageNo=1&numOfRows=300&include=lastScr,sareaDtlNm,lat,lot,predcYmd,predcNoonSeCd,avgArtmp,avgWspd,avgWtem,avgWvhgt,avgCrsp,weather,totalIndex&serviceKey=" + serviceKey;
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
		
		JsonNode itemsNode = node.path("response").path("body").path("items").path("item");  //이렇게 중첩된 구조에서 items는 객체이고, 그 안에 item 배열이 존재하기 때문에 items.get("item")으로 배열을 꺼내서 넘겨줘야 Thymeleaf에서 이를 순회하며 값을 출력할 수 있게 됩니다.
		
		List<SeaDto> itemList = new ArrayList<>(); //items 데이터가 JSON 배열 형식이라면, 이를 제대로 List 형태로 변환하여 모델에 전달하는 것이 좋습니다. JsonNode에서 직접 데이터를 추출할 때, JsonNode를 List나 DTO 객체로 변환하는 것이 중요
	    if (itemsNode.isArray()) {
	        for (JsonNode itemNode : itemsNode) {
	        	SeaDto itemDTO = new SeaDto();
	            itemDTO.setSareaDtlNm(itemNode.path("sareaDtlNm").asText());
	            itemDTO.setLat(itemNode.path("lat").asText());
	            itemDTO.setLot(itemNode.path("lot").asText());
	            itemDTO.setPredcYmd(itemNode.path("predcYmd").asText());
	            itemDTO.setPredcNoonSeCd(itemNode.path("predcNoonSeCd").asText());
	            itemDTO.setAvgArtmp(itemNode.path("avgArtmp").asText());
	            itemDTO.setAvgWspd(itemNode.path("avgWspd").asText());
	            itemDTO.setAvgWtem(itemNode.path("avgWtem").asText());
	            itemDTO.setAvgWvhgt(itemNode.path("avgWvhgt").asText());
	            itemDTO.setAvgCrsp(itemNode.path("avgCrsp").asText());
	            itemDTO.setWeather(itemNode.path("weather").asText());
	            itemDTO.setTotalIndex(itemNode.path("totalIndex").asText());          
	            itemList.add(itemDTO);
	        }
	    }
		model.addAttribute("items", itemList);
		return "/xdm/travel/travel";
	}
	
	
}
