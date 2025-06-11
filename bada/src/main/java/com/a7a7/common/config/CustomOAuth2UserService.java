package com.a7a7.common.config;

import java.util.Random;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserService;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

import com.a7a7.module.member.MemberDao;
import com.a7a7.module.member.MemberDto;

@Service
public class CustomOAuth2UserService implements OAuth2UserService<OAuth2UserRequest, OAuth2User> {

	@Autowired
    MemberDao dao;
	
	private final String CHARACTERS = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
	
    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        OAuth2User oauth2User = new DefaultOAuth2UserService().loadUser(userRequest);

        String email = oauth2User.getAttribute("email");

        MemberDto memberDto = dao.findMemberByEmail(email);

        if (memberDto == null){ // 회원정보 없으면 회원가입
        	memberDto = new MemberDto();
        	String name = oauth2User.getAttribute("name");
        	memberDto.setEmail(email);
        	memberDto.setName(name);
        	
        	// 랜덤 비밀번호
        	Random random = new Random();
            StringBuilder sb = new StringBuilder(10);
            for (int i = 0; i < 10; i++) {
                int randomIndex = random.nextInt(CHARACTERS.length());
                sb.append(CHARACTERS.charAt(randomIndex));
            }
        	memberDto.setPassword(sb.toString());
        	
        	dao.saveMember(memberDto);
        }

        return new MemberDetails(memberDto, oauth2User.getAttributes());
    }
}