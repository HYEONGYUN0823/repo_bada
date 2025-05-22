package com.a7a7.module.sea;

public class SeaDto {
	//sea table
    private String sareaDtlNm;
    private Double lat;
    private Double lot;
    private String managerId;
    
    //forecast table
    private String forecastId;
    private String predcYmd;
    private String predcNoonSeCd;
    private String avgArtmp;
    private String avgWspd;
    private String avgWtem;
    private String avgWvhgt;
    private String avgCrsp;
    private String weather;
    private String totalIndex;
    private Integer delNy;
    private String createdtime;
    private String seaId;		//나중에 물어보기
    
    
    //favorite
    private String memberId;
    
    
    
	
    // --get, set---
    
	public String getSareaDtlNm() {
		return sareaDtlNm;
	}
	
	public void setSareaDtlNm(String sareaDtlNm) {
		this.sareaDtlNm = sareaDtlNm;
	}
	public Double getLat() {
		return lat;
	}
	public void setLat(Double lat) {
		this.lat = lat;
	}
	public Double getLot() {
		return lot;
	}
	public void setLot(Double lot) {
		this.lot = lot;
	}
	public String getPredcYmd() {
		return predcYmd;
	}
	public void setPredcYmd(String predcYmd) {
		this.predcYmd = predcYmd;
	}
	public String getPredcNoonSeCd() {
		return predcNoonSeCd;
	}
	public void setPredcNoonSeCd(String predcNoonSeCd) {
		this.predcNoonSeCd = predcNoonSeCd;
	}
	public String getAvgArtmp() {
		return avgArtmp;
	}
	public void setAvgArtmp(String avgArtmp) {
		this.avgArtmp = avgArtmp;
	}
	public String getAvgWspd() {
		return avgWspd;
	}
	public void setAvgWspd(String avgWspd) {
		this.avgWspd = avgWspd;
	}
	public String getAvgWtem() {
		return avgWtem;
	}
	public void setAvgWtem(String avgWtem) {
		this.avgWtem = avgWtem;
	}
	public String getAvgWvhgt() {
		return avgWvhgt;
	}
	public void setAvgWvhgt(String avgWvhgt) {
		this.avgWvhgt = avgWvhgt;
	}
	public String getAvgCrsp() {
		return avgCrsp;
	}
	public void setAvgCrsp(String avgCrsp) {
		this.avgCrsp = avgCrsp;
	}
	public String getWeather() {
		return weather;
	}
	public void setWeather(String weather) {
		this.weather = weather;
	}
	public String getTotalIndex() {
		return totalIndex;
	}
	public void setTotalIndex(String totalIndex) {
		this.totalIndex = totalIndex;
	}
	
	public String getCreatedtime() {
		return createdtime;
	}
	public void setCreatedtime(String createdtime) {
		this.createdtime = createdtime;
	}
	public String getMemberId() {
		return memberId;
	}
	public void setMemberId(String memberId) {
		this.memberId = memberId;
	}

	public String getManagerId() {
		return managerId;
	}

	public void setManagerId(String managerId) {
		this.managerId = managerId;
	}

	public String getForecastId() {
		return forecastId;
	}

	public void setForecastId(String forecastId) {
		this.forecastId = forecastId;
	}

	public Integer getDelNy() {
		return delNy;
	}

	public void setDelNy(Integer delNy) {
		this.delNy = delNy;
	}

	public String getSeaId() {
		return seaId;
	}

	public void setSeaId(String seaId) {
		this.seaId = seaId;
	}
	
    
    
    
}
