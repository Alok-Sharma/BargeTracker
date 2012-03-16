package com.social;


public class Model {
	private String bargeItemName;
	private String bargeItemStatus;
	private String bargeItemTime;
	public boolean selected;
	public Model(String name, String status, String time) {
		this.bargeItemName = name;
		this.bargeItemStatus = status;
		this.bargeItemTime = time;
	}
	
	public String getBargeName() {
		return bargeItemName;
	}
	
	public void setBargeName(String bargeName) {
		this.bargeItemName = bargeName;
	}

	public String getBargeStatus() {
		return bargeItemStatus;
	}
	
	public void setBargeStatus(String bargeStatus) {
		this.bargeItemStatus = bargeStatus;
	}

	public String getBargeTime() {
		return bargeItemTime;
	}
	
	public void setBargeTime(String bargeTime) {
		this.bargeItemTime = bargeTime;
	}
	
	public boolean isSelected() {
		return selected;
	}

	public void setSelected(boolean selected) {
		this.selected = selected;
	}

}
