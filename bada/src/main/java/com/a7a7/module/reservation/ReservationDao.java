package com.a7a7.module.reservation;

import java.util.List;

import org.springframework.stereotype.Repository;

@Repository
public interface ReservationDao {

	public List<ReservationDto> reservationList();
}
