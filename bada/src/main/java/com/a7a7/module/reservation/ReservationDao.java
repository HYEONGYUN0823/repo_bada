package com.a7a7.module.reservation;

import java.util.List;

import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import com.a7a7.module.common.PageVo;

@Repository
public interface ReservationDao {

	public List<ReservationDto> reservationList(@Param("pageVo") PageVo pageVo);
	
	public int countReservationList(@Param("pageVo") PageVo pageVo);
	public int saveReservation(ReservationDto dto);
}
