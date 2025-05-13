package com.a7a7.module.restaurant;

import java.util.List;

import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import com.a7a7.module.common.PageVo;
import com.a7a7.module.common.SearchVo;

@Repository
public interface RestaurantDao {
	
	public void saveRestaurantApiResponse(RestaurantDto dto); // RestaurantApi 호출 값 DB 저장
	public RestaurantDto findRestaurantByTitle(String title); // 식당명으로 DB 검색
	public RestaurantDto findRestaurantById(String restaurantId); // Id로 DB 검색
	public List<RestaurantDto> findRestaurantList(@Param("pageVo") PageVo pageVo, @Param("searchVo") SearchVo searchVo); // 식당 전체 출력
	public int countRestaurantList(@Param("pageVo") PageVo pageVo, @Param("searchVo") SearchVo searchVo); // 숙박업소 전체 개수
	
	public RestaurantDto findByRestaurantId(String restaurantId); // 1:1문의
}
