package com.a7a7.module.sea;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import com.a7a7.module.codegroup.CodeGroupController;
import com.a7a7.module.common.PageVo;
import com.a7a7.module.common.SearchVo;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

@Service
public class SeaService {

    private final CodeGroupController codeGroupController;

	@Autowired
	SeaDao dao;
	
	@Value("${sea_api_key}")
	private String serviceKey;


    SeaService(CodeGroupController codeGroupController) {
        this.codeGroupController = codeGroupController;
    }
	
	
	public void seaApiResponse() throws Exception {		
		// api 호출
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
	                    itemDTO.setSea_id(check.getSea_id());
	                    break;
	                }
	            }
	            // 그 다음 중복 여부 확인
	            boolean isExist = false;
	            for (SeaDto checkDto : confirmForecastList) {
	                if (itemDTO.getSea_id().equals(checkDto.getSea_id()) &&
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
			if(list.getSea_id().equals(dto.getSea_id()) && list.getPredcYmd().equals(dto.getPredcYmd()) && list.getPredcNoonSeCd().equals("일")
				&& (dto.getPredcNoonSeCd().equals("오전")|| dto.getPredcNoonSeCd().equals("오후"))){
				return list.getForecast_id();
			}
		}
		return null;
	}
	
	
	
	//사용자부분
	public List<SeaDto> userindexmap(){
		return dao.userindexmap();
	}
	
	public List<SeaDto> seaUsrList(PageVo pageVo,SearchVo searchVo){
		return dao.seaUsrList(pageVo,  searchVo);
	}
	
	public SeaDto seaView(String sea_id) {
		return dao.seaView(sea_id);
	}
	// 숙박업소 전체 개수
	public int countSeaList(PageVo pageVo, SearchVo searchVo) {
		return dao.countSeaList(pageVo, searchVo);
	}
	
	public List<SeaDto> localForecastList(String sea_id){
		return dao.localForecastList(sea_id);
	}
	
	
}
