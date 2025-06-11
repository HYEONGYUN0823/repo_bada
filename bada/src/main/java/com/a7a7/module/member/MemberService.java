package com.a7a7.module.member;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class MemberService {
	
	@Autowired
	MemberDao dao;
	
	@Autowired
	PasswordEncoder passwordEncoder;
	
	// 회원가입
	public int saveMember(MemberDto dto) {
		dto.setPassword(passwordEncoder.encode(dto.getPassword()));
		return dao.saveMember(dto);
	}
	
	// 로그인
	public boolean processSignIn(MemberDto dto) {
		
		MemberDto result = dao.findMemberByEmail(dto.getEmail());
		
		if(result == null) {
			return false;
		}
		
		if(passwordEncoder.matches(dto.getPassword(), result.getPassword())) {
			return true;
		} else {
			return false;
		}
	}
	
}
