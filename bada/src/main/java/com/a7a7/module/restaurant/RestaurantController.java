package com.a7a7.module.restaurant;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import com.a7a7.common.config.ApiKeysConfig;
import com.a7a7.module.common.PageVo;
import com.a7a7.module.common.SearchVo;


@Controller
public class RestaurantController {
	
	@Autowired
	RestaurantService service;
	
	@Autowired
    ApiKeysConfig apiKeysConfig;
	
//	@Value("${kakao_map_api}")
//	private String kakaoApiKey;
	
	// 관리자 식당 관리 화면
	@RequestMapping("/xdm/restaurant/list")
	public String findXdmRestaurantList(Model model, PageVo pageVo, SearchVo searchVo) throws Exception {

		// AccomApi 호출 값 DB 저장
		service.saveRestaurantApiResponse();
		// DB에서 식당 전체 출력
		model.addAttribute("list", service.findRestaurantList(pageVo, searchVo));
		
		return "xdm/restaurant/restaurantList";
	}
	
	// 사용자 식당 목록화면
	@RequestMapping("/bada/restaurant/list")
	public String findUsrRestaurantList(Model model, PageVo pageVo, SearchVo searchVo) {
		
		// 검색
		model.addAttribute("searchVo", searchVo);
		// 페이징
		pageVo.setParamsPaging(service.countRestaurantList(pageVo, searchVo));
		model.addAttribute("pageVo", pageVo);
		// DB에서 식당 전체 출력
		model.addAttribute("list", service.findRestaurantList(pageVo, searchVo));
		
		return "usr/restaurant/restaurantList";
	}
	
	// 사용자 식당 상세화면
	@GetMapping("/bada/restaurant/{id}")
	public String findUsrRestaurantDetail(@PathVariable("id") String restaurantId, Model model) {
		
		// DB에서 식당 전체 출력
		model.addAttribute("item", service.findRestaurantById(restaurantId));
		// kakao api
		String kakaoApiKey = apiKeysConfig.getKakaoMapApiKey();
		model.addAttribute("kakaoApiKey", kakaoApiKey);
		
		return "usr/restaurant/restaurantDetail";
	}
	
	
}
