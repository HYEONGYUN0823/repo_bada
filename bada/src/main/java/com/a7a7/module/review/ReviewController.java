package com.a7a7.module.review;
import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@Controller
public class ReviewController {
	
	@Autowired
	ReviewService service;
	
	// 리뷰 Ajax
	@GetMapping("/bada/reviewFragment")
	public String reviewFragment(ReviewDto dto, Model model) {
		model.addAttribute("reviewCount", service.countReviewByParent(dto));
		model.addAttribute("reviewList", service.findReviewListByParent(dto));
		return "usr/fragment/reviewFragment :: reviewFragment";
	}
	
	// 리뷰 추가 Ajax
	@PostMapping("/bada/saveReview")
	public ResponseEntity<Map<String, String>> saveReview(@RequestBody ReviewDto dto) {
		service.saveReview(dto);
	    Map<String, String> response = new HashMap<>();
	    response.put("message", "리뷰 저장");
	    return ResponseEntity.ok(response);
	}
	
}
