package com.thepegeekapps.easyassessment.model;

public class Criteria {
	
	protected int id;
	protected int rubricId; 
	protected String name;
	protected int startScale;
	protected int endScale;
	
	public Criteria() {}
	
	public Criteria(int id, int rubricId, String name, int startScale, int endScale) {
		this.id = id;
		this.rubricId = rubricId;
		this.name = name;
		this.startScale = startScale;
		this.endScale = endScale;
	}
	
	public int getId() {
		return id;
	}
	
	public void setId(int id) {
		this.id = id;
	}
	
	public int getRubricId() {
		return rubricId;
	}
	
	public void setRubricId(int rubricId) {
		this.rubricId = rubricId;
	}
	
	public String getName() {
		return name;
	}
	
	public void setName(String name) {
		this.name = name;
	}
	
	public int getStartScale() {
		return startScale;
	}
	
	public void setStartScale(int startScale) {
		this.startScale = startScale;
	}
	
	public int getEndScale() {
		return endScale;
	}
	
	public void setEndScale(int endScale) {
		this.endScale = endScale;
	}

}
