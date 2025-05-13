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
import com.a7a7.module.common.SearchVo;

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
	
	@RequestMapping("/bada/travel/list")
	public String findUsrTravelList(PageVo pageVo,SearchVo searchVo,Model model) {	
		//검색기능.
		model.addAttribute("searchVo", searchVo);
		// 페이징
		pageVo.setParamsPaging(service.countSeaList(pageVo, searchVo));
		model.addAttribute("pageVo", pageVo);;
		// DB에서 숙박업소 전체 출력
		model.addAttribute("list", service.seaUsrList(pageVo,searchVo));
		return "usr/travel/travelList";
	}
	
	@GetMapping("/bada/travel/{id}")
	public String findUsrTravelView(@PathVariable("id") String sea_id,Model model){
		model.addAttribute("list",service.seaView(sea_id));
		model.addAttribute("kakaoApiKey", kakaoApiKey);
		model.addAttribute("item",service.localForecastList(sea_id));
		return "usr/travel/travelView";
	}
}
