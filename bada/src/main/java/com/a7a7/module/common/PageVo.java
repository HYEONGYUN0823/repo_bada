package com.a7a7.module.common;

public class PageVo {

	private int thisPage = 1;									// 현재 페이지
	private int rowNumToShow = 6;								// 화면에 보여줄 데이터 줄 갯수
	private int pageNumToShow = 5;								// 화면에 보여줄 페이징 번호 갯수

	private int totalRows;										// 전체 데이터 갯수
	private int totalPages;										// 전체 페이지 번호
	private int startPage;										// 시작 페이지 번호
	private int endPage;										// 마지막 페이지 번호

	private int startRnumForMysql = 0;							// 쿼리 시작 row
	
	// 페이징 설정
	public void setParamsPaging(int totalRows) {
		// 전체 행 수 설정
		setTotalRows(totalRows);

		// 총 페이지 수 계산
		int totalPages = (int) Math.ceil((double) getTotalRows() / getRowNumToShow());
		setTotalPages(totalPages == 0 ? 1 : totalPages);
		
		
		// 현재 페이지가 총 페이지보다 크면 마지막 페이지로 이동
		if(getThisPage() > getTotalPages()) {
			setThisPage(getTotalPages());
		}
		
		// 시작 페이지와 끝 페이지 설정
		setStartPage(((getThisPage() - 1) / getPageNumToShow()) * getPageNumToShow() + 1);
		setEndPage(Math.min(getStartPage() + getPageNumToShow() - 1, getTotalPages()));
		
		// DB 검색용 시작 행 번호 설정
		setStartRnumForMysql((getThisPage()-1) * getRowNumToShow());
		
		// 로그 출력
		System.out.println("getThisPage():" + getThisPage());
		System.out.println("getTotalRows():" + getTotalRows());
		System.out.println("getRowNumToShow():" + getRowNumToShow());
		System.out.println("getTotalPages():" + getTotalPages());
		System.out.println("getStartPage():" + getStartPage());
		System.out.println("getEndPage():" + getEndPage());		
		System.out.println("getStartRnumForMysql(): " + getStartRnumForMysql());
	}
	
	public int getThisPage() {
		return thisPage;
	}

	public void setThisPage(int thisPage) {
		this.thisPage = thisPage;
	}

	public int getRowNumToShow() {
		return rowNumToShow;
	}

	public void setRowNumToShow(int rowNumToShow) {
		this.rowNumToShow = rowNumToShow;
	}

	public int getPageNumToShow() {
		return pageNumToShow;
	}

	public void setPageNumToShow(int pageNumToShow) {
		this.pageNumToShow = pageNumToShow;
	}

	public int getTotalRows() {
		return totalRows;
	}

	public void setTotalRows(int totalRows) {
		this.totalRows = totalRows;
	}

	public int getTotalPages() {
		return totalPages;
	}

	public void setTotalPages(int totalPages) {
		this.totalPages = totalPages;
	}

	public int getStartPage() {
		return startPage;
	}

	public void setStartPage(int startPage) {
		this.startPage = startPage;
	}

	public int getEndPage() {
		return endPage;
	}

	public void setEndPage(int endPage) {
		this.endPage = endPage;
	}

	public int getStartRnumForMysql() {
		return startRnumForMysql;
	}

	public void setStartRnumForMysql(int startRnumForMysql) {
		this.startRnumForMysql = startRnumForMysql;
	}
	
}
