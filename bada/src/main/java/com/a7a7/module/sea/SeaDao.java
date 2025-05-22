package com.a7a7.module.sea;

import java.util.List;

import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import com.a7a7.module.common.PageVo;
import com.a7a7.module.common.SearchVo;

@Repository
public interface SeaDao {

	public List<SeaDto> seaList();		// 바다여행지역 리스트
	public List<SeaDto> forecastList(); // 바다여행지역의 바다여행지수 리스트
	public List<SeaDto> forecastchecklist(SeaDto dto); // API부분 리스트 체크 확인
	public List<SeaDto> localForecastList(String seaId);
	
	public int seaInsert(SeaDto dto); // api에서 바다여행지역 리스트
	public int forecastInsert(SeaDto dto); // api에서 바다여행지수 리스트
	public int forecastUpdate(SeaDto dto);
	public int forecastDelete1(SeaDto dto);
	public int forecastDelete2(@Param("forecastId") String forecastId);
	
	public int countSeaList(@Param("pageVo") PageVo pageVo, @Param("searchVo") SearchVo searchVo); // 바다여행지역 전체 개수
	
	//사용자부분
	public List<SeaDto> userindexmap(String day);
	public List<SeaDto> seaUsrList(@Param("pageVo") PageVo pageVo, @Param("searchVo") SearchVo searchVo);
	public SeaDto seaView(String seaId);
	public SeaDto findSeaById(String seaId);
}
