package com.a7a7.module.accom;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class AccomController {
	
	@Autowired
	AccomService service;
	
	@Value("${accom_api_key}")
	private String serviceKey;
	
	@RequestMapping("/xdm/accom/list")
	public String getAccomList(Model model) throws Exception {

		service.saveAccomApiResponse();
		
		model.addAttribute("list", service.getAccomList());
		
		return "xdm/accom/accomList";
	}
}
