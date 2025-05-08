package com.a7a7.module.accom;
import com.a7a7.module.sea.SeaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import com.a7a7.module.common.PageVo;

@Controller
public class AccomController {

    private final SeaService seaService;
	
	@Autowired
	AccomService service;
	
	@Value("${kakao_map_api}")
	private String kakaoApiKey;

    AccomController(SeaService seaService) {
        this.seaService = seaService;
    }
	
	// 관리자 숙박업소 관리 화면
	@RequestMapping("/xdm/accom/list")
	public String findXdmAccomList(Model model, PageVo pageVo) throws Exception {

		// AccomApi 호출 값 DB 저장
		service.saveAccomApiResponse();
		// DB에서 숙박업소 전체 출력
		model.addAttribute("list", service.findAccomList(pageVo));
		
		return "xdm/accom/accomList";
	}
	
	// 사용자 숙박업소 목록 화면
	@GetMapping("/bada/accom/list")
	public String findUsrAccomList(Model model, PageVo pageVo) {
		
		pageVo.setParamsPaging(service.countAccomList());
		// DB에서 숙박업소 전체 출력
		model.addAttribute("list", service.findAccomList(pageVo));
		
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
