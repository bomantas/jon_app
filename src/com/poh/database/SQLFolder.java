package com.poh.database;

public class SQLFolder {
	private int recid;
	private int parent;
	private String title;
	private String logo;
	
	public int getRecid() {
		return recid;
	}
	public void setRecid(int recid) {
		this.recid = recid;
	}
	public int getParent() {
		return parent;
	}
	public void setParent(int parent) {
		this.parent = parent;
	}
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	public String getLogo() {
		return logo;
	}
	public void setLogo(String logo) {
		this.logo = logo;
	}
	
}
