package com.a7a7.module.checkout;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.a7a7.module.restaurant.RestaurantDto;
import com.a7a7.module.restaurant.RestaurantService;

@Controller
public class CheckoutController {
	@Autowired
	RestaurantService restaurantService;
	
	@RequestMapping(value = "/restaurant/checkout")
	public String restaurantCheckout(@RequestParam("restaurantId") String restaurantId, Model model) {
	    RestaurantDto restaurant = restaurantService.findRestaurantById(restaurantId);
	    model.addAttribute("item", restaurant);
	    
	    return "usr/checkout/checkout";
	}
	
}
