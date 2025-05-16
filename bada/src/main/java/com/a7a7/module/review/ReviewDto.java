package com.a7a7.module.review;

public class ReviewDto {
	
	private Integer rating;
	private String contents;
	private String createdtime;
	private String name;
	
	private Integer parentType;
	private String parentId;
	private String memberId;
	
	
	
	public Integer getRating() {
		return rating;
	}
	public void setRating(Integer rating) {
		this.rating = rating;
	}
	public String getContents() {
		return contents;
	}
	public void setContents(String contents) {
		this.contents = contents;
	}
	public String getCreatedtime() {
		return createdtime;
	}
	public void setCreatedtime(String createdtime) {
		this.createdtime = createdtime;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public Integer getParentType() {
		return parentType;
	}
	public void setParentType(Integer parentType) {
		this.parentType = parentType;
	}
	public String getParentId() {
		return parentId;
	}
	public void setParentId(String parentId) {
		this.parentId = parentId;
	}
	public String getMemberId() {
		return memberId;
	}
	public void setMemberId(String memberId) {
		this.memberId = memberId;
	}
	@Override
	public String toString() {
		return "ReviewDto [rating=" + rating + ", contents=" + contents + ", createdtime=" + createdtime + ", name="
				+ name + ", parentType=" + parentType + ", parentId=" + parentId + ", memberId=" + memberId + "]";
	}
	
	
}
