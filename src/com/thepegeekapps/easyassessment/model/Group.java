package com.thepegeekapps.easyassessment.model;


public class Group {

	protected int id;
	protected String name;
	protected int studentsCount;
	protected int assessmentsCount;
	
	public Group() {}
	
	public Group(int id, String name) {
		this.id = id;
		this.name = name;
	}
	
	public Group(int id, String name, int studentsCount, int assessmentsCount) {
		this.id = id;
		this.name = name;
		this.studentsCount = studentsCount;
		this.assessmentsCount = assessmentsCount;
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
	
	public int getStudentsCount() {
		return studentsCount;
	}
	
	public void setStudentsCount(int studentsCount) {
		this.studentsCount = studentsCount;
	}
	
	public int getAssessmentsCount() {
		return assessmentsCount;
	}
	
	public void setAssessmentsCount(int assessmentsCount) {
		this.assessmentsCount = assessmentsCount;
	}

}
