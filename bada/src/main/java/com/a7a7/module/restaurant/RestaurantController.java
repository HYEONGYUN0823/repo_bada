package com.a7a7.module.restaurant;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;


@Controller
public class RestaurantController {
	@Autowired
	RestaurantService service;
	
	@Value("${kakao_map_api}")
	private String kakaoApiKey;
	
	// 관리자 숙박업소 관리 화면
	@RequestMapping("/xdm/restaurant/list")
	public String findXdmRestaurantList(Model model) throws Exception {

		// AccomApi 호출 값 DB 저장
		service.saveRestaurantApiResponse();
		// DB에서 숙박업소 전체 출력
		model.addAttribute("list", service.findRestaurantList());
		
		return "xdm/restaurant/restaurantList";
	}
	
	// 사용자 숙박업소 목록화면
	@GetMapping("/bada/restaurant/list")
	public String findUsrRestaurantList(Model model) {
		
		// DB에서 숙박업소 전체 출력
		model.addAttribute("list", service.findRestaurantList());
		
		return "usr/restaurant/restaurantList";
	}
	
	// 사용자 숙박업소 상세화면
	@GetMapping("/bada/restaurant/list/{id}")
	public String findUsrRestaurantDetail(@PathVariable("id") String restaurantId, Model model) {
		
		// DB에서 숙박업소 전체 출력
		model.addAttribute("item", service.findRestaurantById(restaurantId));
		
		return "usr/restaurant/restaurantDetail";
	}
	
	
}
