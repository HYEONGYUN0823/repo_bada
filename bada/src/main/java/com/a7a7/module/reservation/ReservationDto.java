package com.a7a7.module.reservation;

public class ReservationDto {

	//reservation(예약)
		private Integer member_id;
		private Integer parent_type;
		private Integer parent_id;
		private Integer del_ny;
		
		private String reservation_id;
		private String start_time;
		private String end_time;
		private Integer quantity;
		private String createdtime;
		
		
		
	//getter && setter	
		public Integer getMember_id() {
			return member_id;
		}
		public void setMember_id(Integer member_id) {
			this.member_id = member_id;
		}
		public Integer getParent_type() {
			return parent_type;
		}
		public void setParent_type(Integer parent_type) {
			this.parent_type = parent_type;
		}
		public Integer getParent_id() {
			return parent_id;
		}
		public void setParent_id(Integer parent_id) {
			this.parent_id = parent_id;
		}
		public Integer getDel_ny() {
			return del_ny;
		}
		public void setDel_ny(Integer del_ny) {
			this.del_ny = del_ny;
		}
		public String getReservation_id() {
			return reservation_id;
		}
		public void setReservation_id(String reservation_id) {
			this.reservation_id = reservation_id;
		}
		public String getStart_time() {
			return start_time;
		}
		public void setStart_time(String start_time) {
			this.start_time = start_time;
		}
		public String getEnd_time() {
			return end_time;
		}
		public void setEnd_time(String end_time) {
			this.end_time = end_time;
		}
		public Integer getQuantity() {
			return quantity;
		}
		public void setQuantity(Integer quantity) {
			this.quantity = quantity;
		}
		public String getCreatedtime() {
			return createdtime;
		}
		public void setCreatedtime(String createdtime) {
			this.createdtime = createdtime;
		}
		
		
		
}
