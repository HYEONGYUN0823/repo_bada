package com.a7a7.module.reservation;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

import com.a7a7.common.config.MemberDetails;
import com.a7a7.module.common.PageVo;

@Controller
public class ReservationController {

	@Autowired
	ReservationService service;
	
	@RequestMapping("/bada/mypage/reservation")
	public String order(PageVo pageVo,Model model){
		int c = service.countReservationList(pageVo);
		pageVo.setParamsPaging(service.countReservationList(pageVo));
		model.addAttribute("pageVo", pageVo);
		model.addAttribute("count", c);
		model.addAttribute("items", service.reservationList(pageVo));
		return "usr/information/reservation";
	}
	
	@RequestMapping("/bada/saveReservation")
	public String saveReservation(ReservationDto dto, Authentication auth){
		MemberDetails memberDetails = (MemberDetails) auth.getPrincipal();
		System.out.println("@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@");
		System.out.println(memberDetails);
		dto.setMemberId(memberDetails.getMemberId());
		service.saveReservation(dto);
		return "redirect:/bada/mypage/reservation";
	}
}
