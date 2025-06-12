package com.a7a7.module.sea;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.a7a7.common.config.ApiKeysConfig;
import com.a7a7.common.config.MemberDetails;
import com.a7a7.module.common.PageVo;
import com.a7a7.module.common.SearchVo;
import com.a7a7.module.review.ReviewDto;
import com.a7a7.module.review.ReviewService;


@Controller
public class SeaController {
	
	@Autowired
	SeaService service;
	
	@Autowired
	ReviewService reviewService;
	
	@Autowired
	ApiKeysConfig apiKeysConfig;
	
//	@Value("${kakao_map_api}")
//	private String kakaoApiKey;
	


	 
	//******************//
	///관리자 화면 부분/////
	//******************//
	
	

	//////////바다여행장소///////////////
	@RequestMapping(value="xdm/seaTravel")
	public String seaTravel(PageVo pageVo,Model model)throws Exception{
		service.seaApiResponse();
		model.addAttribute("items",service.seaList());
		return "/xdm/travel/seaTravel";
	}
	
	////////// 바다여행지수///////////////

	@RequestMapping(value="xdm/travel")
	public String Travel(PageVo pageVo,Model model) throws Exception {
		service.seaApiResponse();
		model.addAttribute("items", service.forecastList());
		return "/xdm/travel/travel";
	}
	
	
	
	//******************//
	//사용자 부분//
	//******************//
	
	@RequestMapping("/bada/travel/list")
	public String findUsrTravelList(ReviewDto reviewDto, PageVo pageVo,SearchVo searchVo,Model model) {	
		//검색기능.
		model.addAttribute("searchVo", searchVo);
		// 페이징
		pageVo.setParamsPaging(service.countSeaList(pageVo, searchVo));
		model.addAttribute("pageVo", pageVo);
		// DB에서 숙박업소 전체 출력
		model.addAttribute("list", service.seaUsrList(pageVo,searchVo));
		
		return "usr/travel/travelList";
	}
	
	@GetMapping("/bada/travel/{id}")
	public String findUsrTravelView(@PathVariable("id") String seaId, SeaDto dto,Model model, Authentication auth) throws Exception{
//		service.seaApiResponse();
		
		 if (auth == null) {
		        System.out.println("로그인하지 않은 사용자입니다.");
		 }else if(auth !=null){
			 	MemberDetails details = (MemberDetails) auth.getPrincipal();
				dto.setMemberId(details.getMemberId());
				dto.setSeaId(seaId);
				Integer result = service.FavoriteD(dto);
				if(result != null) {
					model.addAttribute("fav", result);		 
				} 
		 }
		
		
		String kakaoApiKey = apiKeysConfig.getKakaoMapApiKey();
		model.addAttribute("item", service.seaView(seaId));
		model.addAttribute("list",service.seaView(seaId));
		model.addAttribute("kakaoApiKey", kakaoApiKey);
		model.addAttribute("items",service.localForecastList(seaId));
		return "usr/travel/travelDetail";
	}
	
	// 즐겨찾기
	@PostMapping("/favoriteAdd")
	@ResponseBody
	public void favoriteAdd(@RequestBody SeaDto dto, Authentication auth) {
		
		
		
		 if (auth == null){
		 }else {
			 MemberDetails details = (MemberDetails) auth.getPrincipal();
			dto.setMemberId(details.getMemberId());
		}
		
		int result = service.seaFavoriteView(dto);

		if (result == 0) {
		    service.seaFavoriteAdd(dto);
		} else {
		    service.seaFavoriteUpdate(dto);
		}
		
	}
	
	@PostMapping("/favoriteDelete")
	@ResponseBody
	public void favoriteDelete(@RequestBody SeaDto dto, Authentication auth) {
		
		 if (auth == null){
		 }else {
			 MemberDetails details = (MemberDetails) auth.getPrincipal();
			dto.setMemberId(details.getMemberId());
		}
		service.favoriteDelete(dto);

	}
	
	
	
}
