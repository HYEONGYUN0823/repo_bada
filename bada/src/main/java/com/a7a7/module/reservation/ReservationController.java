package com.a7a7.module.reservation;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class ReservationController {

	@Autowired
	ReservationService service;
	
	@RequestMapping(value = "/order")
	public String order(Model model){
		model.addAttribute("items", service.reservationList());
		return "usr/information/order";
	}
	
}
