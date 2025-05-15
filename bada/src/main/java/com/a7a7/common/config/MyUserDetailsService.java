package com.a7a7.common.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.a7a7.module.member.MemberDao;
import com.a7a7.module.member.MemberDto;

@Service
public class MyUserDetailsService implements UserDetailsService {

	@Autowired
	MemberDao memberDao;
	
	@Override
	public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
		MemberDto memberDto = memberDao.findPasswordByEmail(email);
	    if (memberDto == null) {
	        throw new UsernameNotFoundException("사용자를 찾을 수 없습니다: " + email);
	    }
		return new MemberDetails(memberDto);
	}
}
