package com.a7a7.module.review;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ReviewService {
	
	@Autowired
	ReviewDao dao;
	
	public ReviewDto findAllReviewByParent(ReviewDto dto) {
		return dao.findAllReviewByParent(dto);
	}
	
}
