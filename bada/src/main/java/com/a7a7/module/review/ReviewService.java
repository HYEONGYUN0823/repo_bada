package com.a7a7.module.review;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ReviewService {
	
	@Autowired
	ReviewDao dao;
	
	public List<ReviewDto> findReviewListByParent(ReviewDto dto) {
		return dao.findReviewListByParent(dto);
	}
	
}
