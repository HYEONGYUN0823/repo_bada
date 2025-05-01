package com.a7a7.module.accom;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class AccomController {
	
	@Autowired
	AccomService service;
	
	// 관리자 숙박업소 관리 화면
	@RequestMapping("/xdm/accom/list")
	public String getXdmAccomList(Model model) throws Exception {

		// AccomApi 호출 값 DB 저장
		service.saveAccomApiResponse();
		// DB에서 숙박업소 전체 출력
		model.addAttribute("list", service.getAccomList());
		
		return "xdm/accom/accomList";
	}
	
	// 사용자 숙박업소 목록
	@GetMapping("/bada/accom/list")
	public String getUsrAccomList(Model model) {
		
		// DB에서 숙박업소 전체 출력
		model.addAttribute("list", service.getAccomList());
		
		return "usr/accom/accomList";
	}
	
	
}
