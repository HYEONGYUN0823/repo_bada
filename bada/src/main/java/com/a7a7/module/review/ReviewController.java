package com.a7a7.module.review;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

@Controller
public class ReviewController {
	
	@Autowired
	ReviewService service;
	
	// 리뷰 Ajax
	@GetMapping("/bada/reviewFragment")
	public String reviewFragment(ReviewDto dto, Model model) {
		System.out.println(dto);
		model.addAttribute("reviewList", service.findReviewListByParent(dto));
		return "usr/fragment/reviewFragment :: reviewFragment";
	}
	
	// 리뷰 추가 Ajax
	@PostMapping("/bada/saveReview")
	public ResponseEntity<String> saveReview(ReviewDto dto) {
		service.saveReview(dto);
		return ResponseEntity.ok("리뷰 저장");
	}
	
}
