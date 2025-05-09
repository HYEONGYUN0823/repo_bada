package com.a7a7.module.sea;

import java.util.List;

import org.springframework.stereotype.Repository;

import com.a7a7.module.common.PageVo;

@Repository
public interface SeaDao {

	public List<SeaDto> seaList();		// 바다여행지역 리스트
	public List<SeaDto> forecastList(); // 바다여행지역의 바다여행지수 리스트
	
	public int seaInsert(SeaDto dto); // api에서 바다여행지역 리스트
	public int forecastInsert(SeaDto dto); // api에서 바다여행지수 리스트
	
	
	public int countSeaList(); // 바다여행지역 전체 개수
	
	//사용자부분
	public List<SeaDto> seaUsrList(PageVo pageVo);
	public SeaDto seaView(String sea_id);
}
