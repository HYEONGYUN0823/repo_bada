package com.a7a7.module.sea;

import java.util.List;

import org.springframework.stereotype.Repository;

@Repository
public interface SeaDao {

	public List<SeaDto> seaList();
	public List<SeaDto> forecastList();
	
	public int seaInsert(SeaDto dto);
	public int forecastInsert(SeaDto dto);
}
