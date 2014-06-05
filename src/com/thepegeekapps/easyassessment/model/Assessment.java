package com.thepegeekapps.easyassessment.model;

import java.util.List;

public class Assessment {
	
	protected int id;
	protected int groupId;
	protected String groupName;
	protected int rubricId;
	protected String rubricName;
	protected String name;
	protected String takenDate;
	protected boolean checked;
	
	protected List<AStudent> students;
	
	public Assessment() {}
	
	public Assessment(int id, int groupId, String groupName, int rubricId, String rubricName, String name, String takenDate) {
		this(id, groupId, groupName, rubricId, rubricName, name, takenDate, null);
	}
	
	public Assessment(int id, int groupId, String groupName, int rubricId, String rubricName, String name, String takenDate, List<AStudent> students) {
		this.id = id;
		this.groupId = groupId;
		this.groupName = groupName;
		this.rubricId = rubricId;
		this.rubricName = rubricName;
		this.name = name;
		this.takenDate = takenDate;
		this.students = students;
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
	
	public String getGroupName() {
		return groupName;
	}
	
	public void setGroupName(String groupName) {
		this.groupName = groupName;
	}
	
	public int getRubricId() {
		return rubricId;
	}
	
	public void setRubricId(int rubricId) {
		this.rubricId = rubricId;
	}
	
	public String getRubricName() {
		return rubricName;
	}
	
	public void setRubricName(String rubricName) {
		this.rubricName = rubricName;
	}	
	
	public String getName() {
		return name;
	}
	
	public void setName(String name) {
		this.name = name;
	}
	
	public String getTakenDate() {
		return takenDate;
	}
	
	public void setTakenDate(String takenDate) {
		this.takenDate = takenDate;
	}
	
	public List<AStudent> getStudents() {
		return students;
	}
	
	public void setStudents(List<AStudent> students) {
		this.students = students;
	}
	
	public boolean isStudentsSelected() {
		return (students != null && !students.isEmpty()); 
	}
	
	public boolean isRubricSelected() {
		return (rubricId != 0);
	}
	
	public boolean isTaken() {
		return (isStudentsSelected() && isRubricSelected());
	}
	
	public boolean isChecked() {
		return checked;
	}
	
	public void setChecked(boolean checked) {
		this.checked = checked;
	}

}
