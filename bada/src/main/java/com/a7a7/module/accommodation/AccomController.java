package com.a7a7.module.accommodation;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class AccomController {

	@RequestMapping("/xdm/accom/list")
	public String getAccomList() {
		
		return "xdm/accom/accomList";
	}
}
