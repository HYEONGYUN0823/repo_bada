package com.a7a7.module.member;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

@Controller
public class MemberController {
	
	@Autowired
	MemberService service;
	
	// 로그인 화면
	@GetMapping("/bada/signIn")
	public String showSignIn() {
		return "usr/member/signIn";
	}
	// 로그인
	@PostMapping("/bada/processSignIn")
	public String processSignIn(MemberDto dto) {
		
		if(service.processSignIn(dto)) {
			return "redirect:/index";
		} else {
			return "redirect:/bada/signIn";
		}
	}

	
	// 회원가입 화면
	@GetMapping("/bada/signUp")
	public String showSignUp() {
		return "usr/member/signUp";
	}
	// 회원가입
	@PostMapping("/bada/saveMember")
	public String saveMember(MemberDto dto) {
		service.saveMember(dto);
		return "redirect:/index";
	}
	
	
}
