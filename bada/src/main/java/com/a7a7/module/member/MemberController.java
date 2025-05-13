package com.a7a7.module.member;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.a7a7.module.common.PageVo;
import com.a7a7.module.common.SearchVo;

@Controller
public class MemberController {
	
	@Autowired
	MemberService service;
	
	// 로그인 화면
	@RequestMapping("/bada/signIn")
	public String showSignIn() {
		return "usr/member/signIn";
	}

	
	// 회원가입 화면
	@RequestMapping("/bada/signUp")
	public String showSignUp() {
		return "usr/member/signUp";
	}
	
	
}
