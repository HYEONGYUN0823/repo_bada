package com.a7a7.module.index;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import com.a7a7.module.sea.SeaService;

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
