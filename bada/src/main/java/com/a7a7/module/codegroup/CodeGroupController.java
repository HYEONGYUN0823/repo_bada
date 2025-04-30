package com.a7a7.module.codegroup;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class CodeGroupController {
	
	@RequestMapping(value = "/index")
	public String index() {
		
		return "usr/index/index";
	}
	
	@RequestMapping(value = "/travelList")
	public String travelList() {
		
		return "usr/list/travelList";
	}	
	
	@RequestMapping(value = "/restaurantList")
	public String restaurantList() {
		
		return "usr/list/restaurantList";
	}	
	
	@RequestMapping(value = "/hotelList")
	public String hotelList() {
		
		return "usr/list/hotelList";
	}	
	
}
