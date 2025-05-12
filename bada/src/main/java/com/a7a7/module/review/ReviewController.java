package com.a7a7.module.review;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class ReviewController {
	
	@Autowired
	ReviewService service;
	
	// 리뷰 Ajax
	@GetMapping("/bada/reviewFragment")
	public String reviewFragment(ReviewDto dto, Model model) {
		System.out.println(dto);
		model.addAttribute("reviewList", service.findAllReviewByParent(dto));
		return "/usr/fragment/reviewFragment :: reviewFragment";
	}
	
}
