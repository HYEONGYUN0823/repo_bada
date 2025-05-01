package com.a7a7.module.userUi;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class UserUiController {
	
	@RequestMapping(value = "/index")
	public String index() {
		
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
}
