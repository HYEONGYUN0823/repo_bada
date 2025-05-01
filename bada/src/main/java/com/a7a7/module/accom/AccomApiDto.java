package com.a7a7.module.accom;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

public class AccomApiDto {
	private Response response;

	public static class Response {
		private Header header;
		private Body body;		
		
		public static class Header {
			private String resultCode;
			private String resultMsg;
//			Getter, Setter			
			public String getResultCode() {
				return resultCode;
			}
			public void setResultCode(String resultCode) {
				this.resultCode = resultCode;
			}
			public String getResultMsg() {
				return resultMsg;
			}
			public void setResultMsg(String resultMsg) {
				this.resultMsg = resultMsg;
			}
//			Constructor
			public Header() {
				super();
			}
		}
		public static class Body {
			private Items items;
			private String numOfRows;
			private String pageNo;
			private String totalCount;
			
			public static class Items {
				private List<Item> item;
				
				@JsonIgnoreProperties(ignoreUnknown = true)
				public static class Item {
					private String title;
					private String createdtime;
					private String modifiedtime;
					private String tel;
					private String addr1;
					private String mapx;
					private String mapy;
					private String firstimage;
//					Constructor
					public Item() {}	
//					Getter, Setter
					public String getTitle() {
						return title;
					}
					public void setTitle(String title) {
						this.title = title;
					}
					public String getCreatedtime() {
						return createdtime;
					}
					public void setCreatedtime(String createdtime) {
						this.createdtime = createdtime;
					}
					public String getModifiedtime() {
						return modifiedtime;
					}
					public void setModifiedtime(String modifiedtime) {
						this.modifiedtime = modifiedtime;
					}
					public String getTel() {
						return tel;
					}
					public void setTel(String tel) {
						this.tel = tel;
					}
					public String getAddr1() {
						return addr1;
					}
					public void setAddr1(String addr1) {
						this.addr1 = addr1;
					}
					public String getMapx() {
						return mapx;
					}
					public void setMapx(String mapx) {
						this.mapx = mapx;
					}
					public String getMapy() {
						return mapy;
					}
					public void setMapy(String mapy) {
						this.mapy = mapy;
					}
					public String getFirstimage() {
						return firstimage;
					}
					public void setFirstimage(String firstimage) {
						this.firstimage = firstimage;
					}
				}
//				Getter, Setter
				public List<Item> getItem() {
					return item;
				}
				public void setItem(List<Item> item) {
					this.item = item;
				}
				
//				Constructor
				public Items() {}
			}
//			Getter, Setter
			public Items getItems() {
				return items;
			}
			public void setItems(Items items) {
				this.items = items;
			}
			public String getNumOfRows() {
				return numOfRows;
			}
			public void setNumOfRows(String numOfRows) {
				this.numOfRows = numOfRows;
			}
			public String getPageNo() {
				return pageNo;
			}
			public void setPageNo(String pageNo) {
				this.pageNo = pageNo;
			}
			public String getTotalCount() {
				return totalCount;
			}
			public void setTotalCount(String totalCount) {
				this.totalCount = totalCount;
			}
//			Constructor
			public Body() {}
		}
//		Getter, Setter	
		public Header getHeader() {
			return header;
		}
		public void setHeader(Header header) {
			this.header = header;
		}
		public Body getBody() {
			return body;
		}
		public void setBody(Body body) {
			this.body = body;
		}
//		Constructor
		public Response() {
			super();
		}
	}
//	Constructor
	public AccomApiDto() {}
//	Getter, Setter
	public Response getResponse() {
		return response;
	}
	public void setResponse(Response response) {
		this.response = response;
	}
}
