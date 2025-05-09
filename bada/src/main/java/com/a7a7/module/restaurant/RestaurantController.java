package com.a7a7.module.restaurant;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.a7a7.module.common.PageVo;
import com.a7a7.module.sea.SeaService;


@Controller
public class RestaurantController {
	
	private final SeaService seaService;
	
	@Autowired
	RestaurantService service;
	
	@Value("${kakao_map_api}")
	private String kakaoApiKey;
	
	RestaurantController(SeaService seaService) {
        this.seaService = seaService;
    }
	
	// 관리자 식당 관리 화면
	@RequestMapping("/xdm/restaurant/list")
	public String findXdmRestaurantList(Model model, PageVo pageVo) throws Exception {

		// AccomApi 호출 값 DB 저장
		service.saveRestaurantApiResponse();
		// DB에서 식당 전체 출력
		model.addAttribute("list", service.findRestaurantList(pageVo));
		
		return "xdm/restaurant/restaurantList";
	}
	
	// 사용자 식당 목록화면
	@GetMapping("/bada/restaurant/list")
	public String findUsrRestaurantList(@RequestParam(name = "page", defaultValue = "1") int page , Model model) {
		
		// 페이징
		PageVo pageVo = new PageVo();
		pageVo.setThisPage(page);
		pageVo.setParamsPaging(service.countRestaurantList());
		model.addAttribute("pageVo", pageVo);
		// DB에서 식당 전체 출력
		model.addAttribute("list", service.findRestaurantList(pageVo));
		
		return "usr/restaurant/restaurantList";
	}
	
	// 사용자 식당 상세화면
	@GetMapping("/bada/restaurant/{id}")
	public String findUsrRestaurantDetail(@PathVariable("id") String restaurantId, Model model) {
		
		// DB에서 식당 전체 출력
		model.addAttribute("item", service.findRestaurantById(restaurantId));
		// kakao api
		model.addAttribute("kakaoApiKey", kakaoApiKey);
		
		return "usr/restaurant/restaurantDetail";
	}
	
	
}
