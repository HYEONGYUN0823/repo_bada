package com.a7a7.module.accom;

import java.util.List;

import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import com.a7a7.module.common.PageVo;
import com.a7a7.module.common.SearchVo;

@Repository
public interface AccomDao {
	
	public void saveAccomApiResponse(AccomDto dto); // AccomApi 호출 값 DB 저장
	public AccomDto findAccomByTitle(String title); // 숙박업소명으로 DB 검색
	public AccomDto findAccomById(String accomId); // Id로 DB 검색
	public List<AccomDto> findAccomList(@Param("pageVo") PageVo pageVo, @Param("searchVo") SearchVo searchVo); // 숙박업소 전체 출력
	public int countAccomList(@Param("pageVo") PageVo pageVo, @Param("searchVo") SearchVo searchVo); // 숙박업소 전체 개수

}
