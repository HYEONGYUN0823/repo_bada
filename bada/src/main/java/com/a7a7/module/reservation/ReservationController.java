package com.a7a7.module.reservation;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

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
	public String saveReservation(@RequestParam("parentType") String parentType,
								  @RequestParam("parentId") String parentId,
								  @RequestParam("startTime") String startTime,
								  @RequestParam("endTime") String endTime,
								  Authentication auth){
		ReservationDto dto = new ReservationDto();
		MemberDetails memberDetails = (MemberDetails) auth.getPrincipal();
		dto.setMemberId(memberDetails.getMemberId());
		dto.setParentType(parentType);
		dto.setParentId(parentId);
		dto.setStartTime(startTime);
		dto.setEndTime(endTime);
		service.saveReservation(dto);
		return "redirect:/paymentSuccess";
	}
	
}
