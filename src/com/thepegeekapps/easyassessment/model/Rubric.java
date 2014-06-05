package com.thepegeekapps.easyassessment.model;

import java.util.List;

public class Rubric {
	
	public static final int MIN_SCALE = 0;
	public static final int MAX_SCALE = 100;
	
	public static final int DEFAULT_START_SCALE = 0;
	public static final int DEFAULT_END_SCALE = 10;
	
	
	protected int id;
	protected String name;
	protected int startScale;
	protected int endScale;
	protected List<Criteria> criterias;
	
	public Rubric() {}
	
	public Rubric(int id, String name) {
		this(id, name, DEFAULT_START_SCALE, DEFAULT_END_SCALE);
	}
	
	public Rubric(int id, String name, int startScale, int endScale) {
		this(id, name, startScale, endScale, null);
	}
	
	public Rubric(int id, String name, int startScale, int endScale, List<Criteria> criterias) {
		this.id = id;
		this.name = name;
		this.startScale = startScale;
		this.endScale = endScale;
		this.criterias = criterias;
	}
	
	public int getId() {
		return id;
	}
	
	public void setId(int id) {
		this.id = id;
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
	
	public List<Criteria> getCriterias() {
		return criterias;
	}
	
	public void setCriterias(List<Criteria> criterias) {
		this.criterias = criterias;
	}

}
