package com.a7a7.module.accom;

import java.util.List;

import org.springframework.stereotype.Repository;

@Repository
public interface AccomDao {
	
	public void saveAccomApiResponse(AccomDto dto); // AccomApi 호출 값 DB 저장
	public AccomDto getAccomByTitle(String title); // 숙박업소명으로 DB 검색
	public List<AccomDto> getAccomList(); // 숙소 전체 출력

}
