package com.a7a7.module.codegroup;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class CodeGroupController {
	
	@RequestMapping(value = "/index")
	public String index() {
		
		return "usr/index/index";
	}
	
	@RequestMapping(value = "/list")
	public String list() {
		
		return "usr/list/list";
	}	
	
}
