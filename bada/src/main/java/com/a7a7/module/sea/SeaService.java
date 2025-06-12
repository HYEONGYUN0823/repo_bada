package com.a7a7.module.sea;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.a7a7.common.config.ApiKeysConfig;
import com.a7a7.module.codegroup.CodeGroupController;
import com.a7a7.module.common.PageVo;
import com.a7a7.module.common.SearchVo;
import com.a7a7.module.review.ReviewDao;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

@Service
public class SeaService {

    private final CodeGroupController codeGroupController;

	@Autowired
	SeaDao dao;
	
	@Autowired
	ReviewDao reviewdao;
	
	@Autowired
	ApiKeysConfig apiKeysConfig;
	
//	@Value("${sea_api_key}")
//	private String serviceKey;


    SeaService(CodeGroupController codeGroupController) {
        this.codeGroupController = codeGroupController;
    }
	
	
	public void seaApiResponse() throws Exception {		
		// api 호출
		String serviceKey = apiKeysConfig.getSeaApiKey();
		String apiUrl = "https://apis.data.go.kr/1192136/fcstSeaTrip/GetFcstSeaTripApiService?&type=json&reqDate=&pageNo=1&numOfRows=300&include=lastScr,sareaDtlNm,lat,lot,predcYmd,predcNoonSeCd,avgArtmp,avgWspd,avgWtem,avgWvhgt,avgCrsp,weather,totalIndex&serviceKey=" + serviceKey;
		URL url = new URL(apiUrl);
		HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection(); //openConnection() : 해당 URL로 연결을 여는 거야.
		httpURLConnection.setRequestMethod("GET"); //HttpURLConnection : HTTP 요청을 보낼 수 있는 클래스야., setRequestMethod("GET") : GET 방식으로 요청을 보낸다는 의미.
		
		BufferedReader bufferedReader; //BufferedReader는 줄 단위로 읽을 수 있게 도와줘.
		if (httpURLConnection.getResponseCode() == 200) {
			bufferedReader = new BufferedReader(new InputStreamReader(httpURLConnection.getInputStream())); //InputStreamReader는 바이트를 문자로 바꿔주는 클래스.
		} else {
			bufferedReader = new BufferedReader(new InputStreamReader(httpURLConnection.getErrorStream()));
		}
		
		StringBuilder stringBuilder = new StringBuilder();
		String line;
		while ((line = bufferedReader.readLine()) != null) { //한 줄씩 읽어서 StringBuilder에 붙여.
			System.out.println("line: " + line);
			stringBuilder.append(line);
		}

		bufferedReader.close();
		httpURLConnection.disconnect();

		ObjectMapper objectMapper = new ObjectMapper();			//ObjectMapper는 Jackson 라이브러리에서 제공하는 JSON 처리용 클래스야.
		JsonNode node = objectMapper.readTree(stringBuilder.toString()); //JsonNode node = objectMapper.readTree(stringBuilder.toString());
		
		JsonNode itemsNode = node.path("response").path("body").path("items").path("item");  //이렇게 중첩된 구조에서 items는 객체이고, 그 안에 item 배열이 존재하기 때문에 items.get("item")으로 배열을 꺼내서 넘겨줘야 Thymeleaf에서 이를 순회하며 값을 출력할 수 있게 됩니다.
		
		 List<SeaDto> itemList = new ArrayList<>(); //items 데이터가 JSON 배열 형식이라면, 이를 제대로 List 형태로 변환하여 모델에 전달하는 것이 좋습니다. JsonNode에서 직접 데이터를 추출할 때, JsonNode를 List나 DTO 객체로 변환하는 것이 중요
		 List<SeaDto> confirmSeaList = dao.seaList(); 
		 List<SeaDto> confirmForecastList = dao.forecastList();  
		 
		if(itemsNode.isArray()) {
	        for (JsonNode itemNode : itemsNode) {
	        	SeaDto itemDTO = new SeaDto();
	            itemDTO.setSareaDtlNm(itemNode.path("sareaDtlNm").asText());
	            itemDTO.setLat(itemNode.path("lat").asDouble());
	            itemDTO.setLot(itemNode.path("lot").asDouble());
	            itemDTO.setPredcYmd(itemNode.path("predcYmd").asText());
	            itemDTO.setPredcNoonSeCd(itemNode.path("predcNoonSeCd").asText());
	            itemDTO.setAvgArtmp(itemNode.path("avgArtmp").asText());
	            itemDTO.setAvgWspd(itemNode.path("avgWspd").asText());
	            itemDTO.setAvgWtem(itemNode.path("avgWtem").asText());
	            itemDTO.setAvgWvhgt(itemNode.path("avgWvhgt").asText());
	            itemDTO.setAvgCrsp(itemNode.path("avgCrsp").asText());
	            itemDTO.setWeather(itemNode.path("weather").asText());
	            itemDTO.setTotalIndex(itemNode.path("totalIndex").asText());          
	            itemList.add(itemDTO);
	            
	            // 매번 최신 상태의 DB 확인
//	            List<SeaDto> confirmSeaList = dao.seaList(); 
	            
	            /////여행지 관리///////////
	            boolean seaExist = false;

	            for(SeaDto checkDto : confirmSeaList) {
	                if (checkDto.getSareaDtlNm().equals(itemDTO.getSareaDtlNm())) {
	                    seaExist = true;
	                    break; // 하나만 찾아도 중단
	                }
	            }
	            if(!seaExist){
	                dao.seaInsert(itemDTO); 
	                confirmSeaList = dao.seaList();  // DB에서 최신 여행지 리스트를 다시 불러옵니다.
	            }
	            ////////////////////////////
//	            List<SeaDto> confirmForecastList = dao.forecastList();  
	                   
	         // 먼저 정확한 sea_id를 설정 (forecastList 비교 전에)
	            for (SeaDto check : confirmSeaList) {
	                if (check.getSareaDtlNm().equals(itemDTO.getSareaDtlNm())) {
	                    itemDTO.setSeaId(check.getSeaId());
	                    break;
	                }
	            }
	            // 그 다음 중복 여부 확인
	            boolean isExist = false;
	            for (SeaDto checkDto : confirmForecastList) {
	                if (itemDTO.getSeaId().equals(checkDto.getSeaId()) &&
	                    itemDTO.getPredcYmd().equals(checkDto.getPredcYmd()) &&
	                    itemDTO.getPredcNoonSeCd().equals(checkDto.getPredcNoonSeCd())){
	                    isExist = true;
	                    dao.forecastUpdate(itemDTO);
	                    dao.forecastDelete1(itemDTO);
	                    dao.forecastDelete2(findForecastDeleteList(itemDTO));
	                    break;
	                }
	            }
	            // 중복이 아니면 insert
	            if (!isExist) {
	                dao.forecastInsert(itemDTO);
	                dao.forecastDelete2(findForecastDeleteList(itemDTO));
	                confirmForecastList = dao.forecastList();  // DB에서 최신 예보 리스트를 다시 불러옵니다.
	            }
	                        
	        }
	    }
	}
	
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
	
	//delete
	public String findForecastDeleteList(SeaDto dto){
		List<SeaDto> Searchlist = new ArrayList<>(dao.forecastchecklist(dto));
		for(SeaDto list: Searchlist){
			if(list.getSeaId().equals(dto.getSeaId()) && list.getPredcYmd().equals(dto.getPredcYmd()) && list.getPredcNoonSeCd().equals("일")
				&& (dto.getPredcNoonSeCd().equals("오전")|| dto.getPredcNoonSeCd().equals("오후"))){
				return list.getForecastId();
			}
		}
		return null;
	}
	
	
	
	
	//사용자부분
	public List<SeaDto> userindexmap(String day){
		return dao.userindexmap(day);
	}
	
	public List<SeaDto> seaUsrList(PageVo pageVo,SearchVo searchVo){
		return dao.seaUsrList(pageVo,  searchVo);
	}
	
	
	
	//개별 리스트.
	public SeaDto seaView(String seaId) {
		return dao.seaView(seaId);
	}
	public SeaDto findSeaById(String seaId) {
		return dao.findSeaById(seaId);
	}
	// 숙박업소 전체 개수
	public int countSeaList(PageVo pageVo, SearchVo searchVo) {
		return dao.countSeaList(pageVo, searchVo);
	}
	
	public List<SeaDto> localForecastList(String seaId){
		return dao.localForecastList(seaId);
	}
	

	
	
	
	//index 지도 보여주기.
	public List<Map<String, Object>> getGroupedData(List<SeaDto> rawData) {  //SeaDto 객체들을 모아놓은 rawData 리스트를 받아서, 지역별로 데이터를 그룹화하여 정리한 결과를 List<Map<String, Object>> 형태로 반환 -datalist를 반환받기 위해서
	   
		Map<String, Map<String, Object>> grouped = new LinkedHashMap<>(); // LinkedHashMap을 쓰는 이유는 grouped는 지역 이름 순으로 계속 출력되거나 유지돼야 하기 때문에 **입력 순서를 유지하는 LinkedHashMap**을 선택

	    for (SeaDto dto : rawData) {
	        String key = dto.getSareaDtlNm(); // 지역명으로 묶기 (필요하면 sea_id 같이 써도 됨)
	        
	        grouped.computeIfAbsent(key, k -> {  //computeIfAbsent: "만약 key에 해당하는 값이 없으면, 이 함수를 실행해서 값을 넣고 리턴해줘."
	            Map<String, Object> newEntry = new HashMap<>();  //Object는 **모든 클래스의 최상위 부모(최상위 타입)** value값이 다 달라서 Object로 묶는거.
	            newEntry.put("sareaDtlNm", dto.getSareaDtlNm());
	            newEntry.put("lat", dto.getLat());
	            newEntry.put("predcYmd", dto.getPredcYmd());
	            newEntry.put("lot", dto.getLot());
	            newEntry.put("sea_id", dto.getSeaId());
	            newEntry.put("dataList", new ArrayList<Map<String, Object>>());
	            return newEntry;
	        });
	        List<Map<String, Object>> dataList = (List<Map<String, Object>>) grouped.get(key).get("dataList");  //grouped.get(key) Map에서 key에 해당하는 값을 꺼내는 거, grouped.get(key).get("dataList")의 리턴 타입이 Object이기에 (List<Map<String, Object>>) 타입
	        Map<String, Object> data = new HashMap<>();
	        data.put("predcNoonSeCd", dto.getPredcNoonSeCd());
	        data.put("totalIndex", dto.getTotalIndex());
	        dataList.add(data);
	    }

	    return new ArrayList<>(grouped.values());
	}
	
	
	
	
	// 즐겨찾기.
	
	public Integer FavoriteD(SeaDto dto) { // 사용여부 del_ny 1,0 값 찾기
		return dao.FavoriteD(dto);
	}
	
	public int seaFavoriteView(SeaDto dto) { // 처음 insert, update인지 count 찾기.
		return dao.seaFavoriteView(dto);
	}
	public int seaFavoriteAdd(SeaDto dto) {
		return dao.seaFavoriteAdd(dto);
	}
	public int seaFavoriteUpdate(SeaDto dto) {
		return dao.seaFavoriteUpdate(dto);
	}
	public int favoriteDelete(SeaDto dto) {
		return dao.favoriteDelete(dto);
	}
	
	public List<SeaDto> favoriteList(SeaDto dto){
		return dao.favoriteList(dto);
	}
	
	
}
