package com.poh.database;

public class SQLItem {
	private int recid;
	private int parentId;
	private String imageFile;
	private String tite;
	
	public int getRecid() {
		return recid;
	}
	
	public void setRecid(int recid) {
		this.recid = recid;
	}
	
	public int getParentId() {
		return parentId;
	}
	
	public void setParentId(int parentId) {
		this.parentId = parentId;
	}
	
	public String getImageFile() {
		return imageFile;
	}
	
	public void setImageFile(String imageFile) {
		this.imageFile = imageFile;
	}
	
	public String getTite() {
		return tite;
	}
	
	public void setTite(String tite) {
		this.tite = tite;
	}

}
