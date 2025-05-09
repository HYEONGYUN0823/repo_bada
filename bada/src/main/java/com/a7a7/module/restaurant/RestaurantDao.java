package com.a7a7.module.restaurant;

import java.util.List;

import org.springframework.stereotype.Repository;

import com.a7a7.module.common.PageVo;

@Repository
public interface RestaurantDao {
	
	public void saveRestaurantApiResponse(RestaurantDto dto); // RestaurantApi 호출 값 DB 저장
	public RestaurantDto findRestaurantByTitle(String title); // 식당명으로 DB 검색
	public RestaurantDto findRestaurantById(String restaurantId); // Id로 DB 검색
	public List<RestaurantDto> findRestaurantList(PageVo pageVo); // 식당 전체 출력
	public int countRestaurantList(); // 식당 전체 개수
	
}
