package com.a7a7.common.config;

import java.util.Collection;
import java.util.Collections;
import java.util.Map;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.oauth2.core.user.OAuth2User;

import com.a7a7.module.member.MemberDto;

public class MemberDetails implements OAuth2User, UserDetails {

    private final MemberDto memberDto;
    private Map<String, Object> attributes;

    public MemberDetails(MemberDto memberDto) {
        this.memberDto = memberDto;
    }
    
    public MemberDetails(MemberDto memberDto, Map<String, Object> attributes) {
        this.memberDto = memberDto;
        this.attributes = attributes;
    }
    
    @Override
    public Map<String, Object> getAttributes() {
        return attributes;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return Collections.singletonList(new SimpleGrantedAuthority("ROLE_USER"));
    }

    @Override
    public String getPassword() {
        return memberDto.getPassword();
    }

    @Override
    public String getUsername() {
        return memberDto.getEmail();
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }

    public MemberDto getMemberDto() {
        return memberDto;
    }
    
    public String getLoginName() {
		return memberDto.getName();
	}
    
    public String getMemberId() {
		return memberDto.getMemberId();
	}

	@Override
	public String getName() {
		return memberDto.getName();
	}
}