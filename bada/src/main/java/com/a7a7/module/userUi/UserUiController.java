package com.a7a7.module.userUi;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

import com.a7a7.module.sea.SeaService;

@Controller
public class UserUiController {
	
	@Value("${kakao_map_api}")
	private String kakaoApiKey;
	
	@Autowired
	SeaService seaService;	//홈페이지화면에서 DB에 있는 여행지 위도경도값 받기 위해서 불러옴.
	
	
	@RequestMapping(value = "/index")
	public String index(Model model, Authentication auth) {

		String day = "";
		if (day == "" || day.trim().isEmpty()) {
		       day = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
		} 		
		List<Map<String, Object>> processed = seaService.getGroupedData(seaService.userindexmap(day));
		model.addAttribute("items", processed); //여행지 위도,경도 값 자바스크립트에 보내기.
		model.addAttribute("kakaoApiKey", kakaoApiKey);
		return "usr/index/index";
	}
	
//	---------------------------------
//	              여행지
//	---------------------------------
	
	@RequestMapping(value = "/travelList")
	public String travelList() {
		
		return "usr/travel/travelList";
	}
	
	@RequestMapping(value = "/travelView")
	public String travelView() {
		
		return "usr/travel/travelView";
	}
	
//	---------------------------------
//                식당
//  ---------------------------------
	
	@RequestMapping(value = "/restaurantList")
	public String restaurantList() {
		
		return "usr/restaurant/restaurantList";
	}	
	
//	---------------------------------
//                숙박업소
//  ---------------------------------
	
	@RequestMapping(value = "/accomList")
	public String accomList() {
		
		return "usr/accom/accomList";
	}	
	
//	---------------------------------
//                1:1문의
//  ---------------------------------
	
	@RequestMapping(value = "/contact")
	public String contact() {
		
		return "usr/contact/contact";
	}	
	
	@RequestMapping(value = "/test")
	public String test() {
		
		return "usr/test";
	}
	
	@RequestMapping(value = "/checkout")
	public String checkout() {
		
		return "usr/checkout/checkout";
	}
	
//	---------------------------------
//               마이페이지
//  ---------------------------------
	
	@RequestMapping(value = "/changePassword")
	public String changePassword() {
		
		return "usr/information/changePassword";
	}
	
	@RequestMapping(value = "/newPassword")
	public String newPassword() {
		
		return "usr/information/newPassword";
	}
	
	@RequestMapping(value = "/wishlist")
	public String wishlist() {
		
		return "usr/information/wishlist";
	}
	
}
