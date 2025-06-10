package com.a7a7.module.index;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class IndexXdmController {
	
	
	//******************
	//관리자
	//******************
	@RequestMapping(value="/xdm/")
	public String indexXdm() {
		return "xdm/index/index";
	}
	
	
	
}
