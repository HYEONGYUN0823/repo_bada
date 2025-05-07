package com.a7a7.module.sea;

import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;


@Controller
public class SeaController {
	
	@Autowired
	SeaService service;

	 
	//******************//
	///관리자 화면 부분/////
	//******************//
	
	////////// 바다여행지수///////////////

	@RequestMapping(value="xdm/travel")
	public String Travel(Model model) throws Exception {
		service.setlist();
		model.addAttribute("items", service.forecastList());
		return "/xdm/travel/travel";
	}
	
	
	//////////바다여행장소///////////////
	@RequestMapping(value="xdm/seaTravel")
	public String seaTravel(Model model)throws Exception{
		service.setlist();
		model.addAttribute("items",service.seaList());
		return "/xdm/travel/seaTravel";
	}
	
	//******************//
	//사용자 부분//
	//******************//
	
	@GetMapping("/bada/travel/list")
	public String findUsrAccomList(Model model) {	
		// DB에서 숙박업소 전체 출력
		model.addAttribute("list", service.seaList());
		return "usr/travel/travelList";
	}
}
