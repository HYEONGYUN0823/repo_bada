package com.a7a7.module.userUi;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.a7a7.module.accom.AccomDao;
import com.a7a7.module.restaurant.RestaurantDao;
import com.a7a7.module.sea.SeaDao;
import com.a7a7.module.sea.SeaDto;

@Service
public class UserUiService {

	
	@Autowired
	SeaDao seaDao;
	
	@Autowired
	RestaurantDao restaurantDao;
	
	@Autowired
	AccomDao accomDao;
	
	
	public List<Map<String, Object>> getGroupedList(List<SeaDto> rawData) {  //SeaDto 객체들을 모아놓은 rawData 리스트를 받아서, 지역별로 데이터를 그룹화하여 정리한 결과를 List<Map<String, Object>> 형태로 반환 -datalist를 반환받기 위해서
		   
		Map<String, Map<String, Object>> grouped = new LinkedHashMap<>();
	    for (SeaDto dto : rawData) {
	        String key = dto.getSareaDtlNm(); // 지역명으로 묶기 (필요하면 sea_id 같이 써도 됨)
	        
	        grouped.computeIfAbsent(key, k -> {  //computeIfAbsent: "만약 key에 해당하는 값이 없으면, 이 함수를 실행해서 값을 넣고 리턴해줘."
	            Map<String, Object> newEntry = new HashMap<>();  //Object는 **모든 클래스의 최상위 부모(최상위 타입)** value값이 다 달라서 Object로 묶는거.
	            newEntry.put("sareaDtlNm", dto.getSareaDtlNm());
	            newEntry.put("lat", dto.getLat());
	            newEntry.put("lot", dto.getLot());
	            newEntry.put("sea_id", dto.getSeaId());
	            newEntry.put("restaurantList", new ArrayList<Map<String, Object>>());
	            newEntry.put("accomList", new ArrayList<Map<String, String>>());
	            return newEntry;
	        });
	        
	        
	        List<Map<String, String>> dataList = (List<Map<String, String>>) grouped.get(key).get("dataList");  //grouped.get(key) Map에서 key에 해당하는 값을 꺼내는 거, grouped.get(key).get("dataList")의 리턴 타입이 Object이기에 (List<Map<String, Object>>) 타입
	        Map<String, String> data = new HashMap<>();
	        data.put("predcNoonSeCd", dto.getPredcNoonSeCd());
	        data.put("totalIndex", dto.getTotalIndex());
	        dataList.add(data);
	    }

	    return new ArrayList<>(grouped.values());
	}
	
	
	public double calculateDistance(double lat, double lot, double mapx, double mapy) {
        final int R = 6371; // 지구 반지름 (km)
        double dLat = Math.toRadians(mapx - lat);
        double dLon = Math.toRadians(mapy - lot);

        double a = Math.sin(dLat / 2) * Math.sin(dLat / 2) +
                   Math.cos(Math.toRadians(lat)) * Math.cos(Math.toRadians(mapx)) *
                   Math.sin(dLon / 2) * Math.sin(dLon / 2);

        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        return R * c;
    }
}
