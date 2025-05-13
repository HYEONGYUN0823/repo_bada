package com.a7a7.module.contact;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.a7a7.module.accom.AccomDto;
import com.a7a7.module.accom.AccomService;
import com.a7a7.module.restaurant.RestaurantDto;
import com.a7a7.module.restaurant.RestaurantService;

@Controller
public class ContactController {
	
	@Autowired
	RestaurantService restaurantService;
	
	@Autowired
	AccomService accomService;
	
	@RequestMapping(value = "/restaurant/contact")
	public String restaurantContact(@RequestParam("restaurantId") String restaurantId, Model model) {
	    RestaurantDto restaurant = restaurantService.findRestaurantById(restaurantId);
	    model.addAttribute("item", restaurant);
	    
	    return "usr/contact/contact";
	}
	
	@RequestMapping(value = "/accom/contact")
	public String accomContact(@RequestParam("accomId") String accomId, Model model) {
		AccomDto accom = accomService.findAccomById(accomId);
		model.addAttribute("item", accom);
		
		return "usr/contact/contact";
	}
	
}
