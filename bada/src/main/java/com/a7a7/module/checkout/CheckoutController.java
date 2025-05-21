package com.a7a7.module.checkout;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.a7a7.module.accom.AccomDto;
import com.a7a7.module.accom.AccomService;
import com.a7a7.module.restaurant.RestaurantDto;
import com.a7a7.module.restaurant.RestaurantService;
import com.a7a7.module.sea.SeaDto;
import com.a7a7.module.sea.SeaService;

@Controller
public class CheckoutController {
	@Autowired
	SeaService seaService;
	@Autowired
	RestaurantService restaurantService;
	@Autowired
	AccomService accomService;
	
	@RequestMapping(value = "/sea/checkout")
	public String seaCheckout(@RequestParam("sea_id") String sea_id, Model model) {
		SeaDto sea = seaService.seaView(sea_id);
		model.addAttribute("item", sea);
		model.addAttribute("type", "sea");
		model.addAttribute("title", sea.getSareaDtlNm());
		
		return "usr/checkout/checkout";
	}
	
	@RequestMapping(value = "/restaurant/checkout")
	public String restaurantCheckout(@RequestParam("restaurantId") String restaurantId, Model model) {
	    RestaurantDto restaurant = restaurantService.findRestaurantById(restaurantId);
	    model.addAttribute("item", restaurant);
	    model.addAttribute("type", "2");
	    model.addAttribute("title", restaurant.getTitle());
	    
	    return "usr/checkout/checkout";
	}
	
	@RequestMapping(value = "/accom/checkout")
	public String accomContact(@RequestParam("accomId") String accomId, Model model) {
		AccomDto accom = accomService.findAccomById(accomId);
		model.addAttribute("item", accom);
		model.addAttribute("type", "3");
		model.addAttribute("title", accom.getTitle());
		
		return "usr/checkout/checkout";
	}
	
}
