package com.a7a7.module.reservation;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import com.a7a7.module.codegroup.CodeGroupController;
import com.a7a7.module.common.PageVo;

@Controller
public class ReservationController {

    private final CodeGroupController codeGroupController;

	@Autowired
	ReservationService service;

    ReservationController(CodeGroupController codeGroupController) {
        this.codeGroupController = codeGroupController;
    }
	
	@RequestMapping(value = "/reservation")
	public String order(PageVo pageVo,Model model){
		int c = service.countReservationList(pageVo);
		pageVo.setParamsPaging(service.countReservationList(pageVo));
		model.addAttribute("pageVo", pageVo);
		model.addAttribute("count", c);
		model.addAttribute("items", service.reservationList(pageVo));
		return "usr/information/reservation";
	}
	
}
