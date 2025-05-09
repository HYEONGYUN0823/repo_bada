package com.a7a7.module.sea;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.a7a7.module.common.PageVo;

@Controller
public class SeaController {
	
	@Autowired
	SeaService service;
	
	@Value("${kakao_map_api}")
	private String kakaoApiKey;

	 
	//******************//
	///관리자 화면 부분/////
	//******************//
	
	

	//////////바다여행장소///////////////
	@RequestMapping(value="xdm/seaTravel")
	public String seaTravel(PageVo pageVo,Model model)throws Exception{
		service.seaApiResponse();
		model.addAttribute("items",service.seaList());
		return "/xdm/travel/seaTravel";
	}
	
	////////// 바다여행지수///////////////

	@RequestMapping(value="xdm/travel")
	public String Travel(PageVo pageVo,Model model) throws Exception {
		service.seaApiResponse();
		model.addAttribute("items", service.forecastList());
		return "/xdm/travel/travel";
	}
	
	
	
	//******************//
	//사용자 부분//
	//******************//
	
	@GetMapping("/bada/travel/list")
	public String findUsrTravelList(@RequestParam(name = "page", defaultValue = "1") int page ,Model model) {	
		// 페이징
		PageVo pageVo = new PageVo();
		pageVo.setThisPage(page);
		pageVo.setParamsPaging(service.countSeaList());
		model.addAttribute("pageVo", pageVo);
		// DB에서 숙박업소 전체 출력
		model.addAttribute("list", service.seaUsrList(pageVo));
		return "usr/travel/travelList";
	}
	
	@GetMapping("/bada/travel/{id}")
	public String findUsrTravelView(@PathVariable("id") String sea_id,Model model){
		model.addAttribute("list",service.seaView(sea_id));
		model.addAttribute("kakaoApiKey", kakaoApiKey);
		return "usr/travel/travelView";
	}
}
