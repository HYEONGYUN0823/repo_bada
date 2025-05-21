package com.a7a7.module.accom;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.a7a7.module.common.PageVo;
import com.a7a7.module.common.SearchVo;

@Controller
public class AccomController {
	
	@Autowired
	AccomService service;
	
	@Value("${kakao_map_api}")
	private String kakaoApiKey;
	
	// 관리자 숙박업소 관리 화면
	@RequestMapping("/xdm/accom/list")
	public String findXdmAccomList(Model model, PageVo pageVo, SearchVo searchVo) throws Exception {

		// AccomApi 호출 값 DB 저장
		service.saveAccomApiResponse();
		// DB에서 숙박업소 전체 출력
		model.addAttribute("list", service.findAccomList(pageVo, searchVo));
		
		return "xdm/accom/accomList";
	}
	
	// 사용자 숙박업소 목록 화면
	@RequestMapping("/bada/accom/list")
	public String findUsrAccomList(Model model, PageVo pageVo, SearchVo searchVo) {
		// 검색
		model.addAttribute("searchVo", searchVo);
		// 페이징
		pageVo.setParamsPaging(service.countAccomList(pageVo, searchVo));
		model.addAttribute("pageVo", pageVo);
		// DB에서 숙박업소 전체 출력
		model.addAttribute("list", service.findAccomList(pageVo, searchVo));
		
		return "usr/accom/accomList";
	}
	
	// 사용자 숙박업소 상세화면
	@GetMapping("/bada/accom/{id}")
	public String findUsrAccomDetail(@PathVariable("id") String accomId, Model model) {
		
		// 숙박업소 검색
		model.addAttribute("item", service.findAccomById(accomId));
		// kakao api
		model.addAttribute("kakaoApiKey", kakaoApiKey);
		
		return "usr/accom/accomDetail";
	}
	
	
}
