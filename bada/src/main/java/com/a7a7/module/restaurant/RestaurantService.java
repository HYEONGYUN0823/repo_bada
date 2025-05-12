package com.a7a7.module.restaurant;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.a7a7.module.common.PageVo;
import com.a7a7.module.common.SearchVo;
import com.a7a7.module.restaurant.RestaurantApiDto.Response.Body.Items.Item;
import com.fasterxml.jackson.databind.ObjectMapper;

@Service
public class RestaurantService {
	
	@Autowired
	RestaurantDao dao;
	
	@Value("${restaurant_api_key}")
	private String serviceKey;

	public void saveRestaurantApiResponse() throws Exception {	
		// AccomApi 호출
		String apiUrl = "http://apis.data.go.kr/B551011/KorService1/areaBasedList1?numOfRows=100&pageNo=1&MobileOS=ETC&MobileApp=AppTest&listYN=Y&arrange=A&contentTypeId=39&areaCode=1&_type=json&ServiceKey=" + serviceKey;
		
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
		RestaurantApiDto apiDto = objectMapper.readValue(stringBuilder.toString(), RestaurantApiDto.class);
		List<Item> itemList = apiDto.getResponse().getBody().getItems().getItem();
		
		// Accom 호출 값 DB 저장
		for(Item item : itemList) {
			if(dao.findRestaurantByTitle(item.getTitle()) == null) {
				RestaurantDto dto = new RestaurantDto();
				dto.setTitle(item.getTitle());
				if(item.getFirstimage() == null || item.getFirstimage() == "" || item.getTel() == null || item.getTel() == "") { // 이미지, 전화번호 필수
					continue;
				}
				dto.setImage(item.getFirstimage());
				dto.setTel(item.getTel());
				dto.setAddress(item.getAddr1());
				dto.setMapx(item.getMapx());
				dto.setMapy(item.getMapy());
				dto.setCreatedtime(item.getCreatedtime());
				dto.setModifiedtime(item.getModifiedtime());
				dto.setManagerId("1"); // temp
				
				dao.saveRestaurantApiResponse(dto);
			}
		}
		
	}
	
	// accomId로 숙박업소 검색
	public RestaurantDto findRestaurantById(String RestaurantId) {
		return dao.findRestaurantById(RestaurantId);
	}
	
	// 숙박업소 전체 출력
	public List<RestaurantDto> findRestaurantList(PageVo pageVo, SearchVo searchVo) {
		return dao.findRestaurantList(pageVo, searchVo);
	}
	
	// 숙박업소 전체 개수
	public int countRestaurantList(PageVo pageVo, SearchVo searchVo) {
		return dao.countRestaurantList(pageVo, searchVo);
	}
	
}
