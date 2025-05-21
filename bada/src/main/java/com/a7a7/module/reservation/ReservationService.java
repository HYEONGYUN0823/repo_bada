package com.a7a7.module.reservation;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.a7a7.module.common.PageVo;

@Service
public class ReservationService {

	@Autowired
	ReservationDao dao;
	
	public int countReservationList(PageVo pageVo) {
		int limit = 5;
		pageVo.setRowNumToShow(limit);
		return dao.countReservationList(pageVo);
	}
	
	public List<ReservationDto> reservationList(PageVo pageVo){
		int limit = 5;
		pageVo.setRowNumToShow(limit);
		return dao.reservationList(pageVo);
	}
	
	public int saveReservation(ReservationDto dto) {
		return dao.saveReservation(dto);
	}
	
}
