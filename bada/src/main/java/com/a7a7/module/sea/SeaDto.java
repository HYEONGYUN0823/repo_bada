package com.a7a7.module.sea;

public class SeaDto {
	//sea table
    private String sareaDtlNm;
    private Double  lat;
    private Double  lot;
    
    
    //forecast table
    private String predcYmd;
    private String predcNoonSeCd;
    private String avgArtmp;
    private String avgWspd;
    private String avgWtem;
    private String avgWvhgt;
    private String avgCrsp;
    private String weather;
    private String totalIndex;
    private String sea_id;		//나중에 물어보기
    
    
	
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
	public String getSea_id() {
		return sea_id;
	}
	public void setSea_id(String sea_id) {
		this.sea_id = sea_id;
	}
	
    
    
    
}
