package com.thepegeekapps.easyassessment.model;

public class ACriteria {
	
	protected int id;
	protected int aStudentId;
	protected int criteriaId;
	protected String name;
	protected int startScale;
	protected int endScale;
	protected int value;
	
	public ACriteria() {}
	
	
	
	public ACriteria(int id, int aStudentId, int criteriaId, String name, int startScale, int endScale, int value) {
		this. id = id;
		this.aStudentId = aStudentId;
		this.criteriaId = criteriaId;
		this.name = name;
		this.startScale = startScale;
		this.endScale = endScale;
		this.value = value;
	}
	
	public ACriteria(Criteria criteria, int aStudentId) {
		this(0, aStudentId, criteria.getId(), criteria.getName(), criteria.getStartScale(), criteria.getEndScale(), criteria.getStartScale());
	}



	public int getId() {
		return id;
	}
	
	public void setId(int id) {
		this.id = id;
	}
	
	public int getAStudentId() {
		return aStudentId;
	}
	
	public void setAStudentId(int aStudentId) {
		this.aStudentId = aStudentId;
	}
	
	public int getCriteriaId() {
		return criteriaId;
	}
	
	public void setCriteriaId(int criteriaId) {
		this.criteriaId = criteriaId;
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
	
	public int getValue() {
		return value;
	}
	
	public void setValue(int value) {
		this.value = value;
	}

}
