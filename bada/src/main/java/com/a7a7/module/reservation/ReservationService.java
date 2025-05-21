package com.a7a7.module.reservation;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ReservationService {

	@Autowired
	ReservationDao dao;
	
	public List<ReservationDto> reservationList(){
		return dao.reservationList();
	}
}
