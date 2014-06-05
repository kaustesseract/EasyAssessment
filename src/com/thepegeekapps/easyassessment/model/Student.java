package com.thepegeekapps.easyassessment.model;

public class Student {

	protected int id;
	protected int groupId; 
	protected String name;
	protected boolean checked;
	
	public Student() {}
	
	public Student(int id, int groupId, String name) {
		this.id = id;
		this.groupId = groupId;
		this.name = name;
	}
	
	public int getId() {
		return id;
	}
	
	public void setId(int id) {
		this.id = id;
	}
	
	public int getGroupId() {
		return groupId;
	}
	
	public void setGroupId(int groupId) {
		this.groupId = groupId;
	}
	
	public String getName() {
		return name;
	}
	
	public void setName(String name) {
		this.name = name;
	}
	
	public boolean isChecked() {
		return checked;
	}
	
	public void setChecked(boolean checked) {
		this.checked = checked;
	}

}
