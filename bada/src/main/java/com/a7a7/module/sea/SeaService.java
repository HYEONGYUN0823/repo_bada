package com.a7a7.module.sea;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class SeaService {

	@Autowired
	SeaDao dao;
	
	//db에서 list값 보여주기
	public List<SeaDto> seaList(){
		return dao.seaList();
	}
	public List<SeaDto> forecastList(){
		return dao.forecastList();
	}
	
	
	//insert
	public int seaInsert(SeaDto dto){
		return dao.seaInsert(dto);
	}
	public int forecastInsert(SeaDto dto){
		return dao.forecastInsert(dto);
	}
	
}
