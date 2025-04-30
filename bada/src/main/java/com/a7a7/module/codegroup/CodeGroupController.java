package com.a7a7.module.codegroup;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class CodeGroupController {
	
	@RequestMapping(value = "/index")
	public String index() {
		
		return "usr/index/index";
	}
	
//	---------------------------------
//	              여행지
//	---------------------------------
	
	@RequestMapping(value = "/travelList")
	public String travelList() {
		
		return "usr/list/travelList";
	}
	
	@RequestMapping(value = "/travelView")
	public String travelView() {
		
		return "usr/view/travelView";
	}
	
//	---------------------------------
//                식당
//  ---------------------------------
	
	@RequestMapping(value = "/restaurantList")
	public String restaurantList() {
		
		return "usr/list/restaurantList";
	}	
	
//	---------------------------------
//                숙박업소
//  ---------------------------------
	
	@RequestMapping(value = "/hotelList")
	public String hotelList() {
		
		return "usr/list/hotelList";
	}	
	
//	---------------------------------
//                1:1문의
//  ---------------------------------
	
	@RequestMapping(value = "/contact")
	public String contact() {
		
		return "usr/contact/contact";
	}	
	
}
