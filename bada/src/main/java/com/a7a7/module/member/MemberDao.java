package com.a7a7.module.member;

import org.springframework.stereotype.Repository;

@Repository
public interface MemberDao {
	
	public int saveMember(MemberDto dto);
	public MemberDto findMemberByEmail(String email);
	
}
