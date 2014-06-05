package com.thepegeekapps.easyassessment.model;

import java.util.List;

public class AStudent {
	
	protected int id;
	protected int studentId;
	protected int assessmentId;
	protected int groupId;
	protected String name;
	protected String note;
	protected String imagePath;
	protected String videoPath;
	
	protected List<ACriteria> aCriterias;
	
	public AStudent() {}
	
	public AStudent(Student student, int assessmentId) {
		this(0, student.getId(), assessmentId, student.getGroupId(), student.getName(), null, null, null, null);
	}
	
	public AStudent(int id, int studentId, int assessmentId, int groupId, String name, 
			String note, String imagePath, String videoPath)
	{
		this(id, studentId, assessmentId, groupId, name, note, imagePath, videoPath, null);
	}
	
	public AStudent(int id, int studentId, int assessmentId, int groupId, String name, 
			String note, String imagePath, String videoPath, List<ACriteria> aCriterias)
	{
		this.id = id;
		this.studentId = studentId;
		this.assessmentId = assessmentId;
		this.groupId = groupId;
		this.name = name;
		this.note = note;
		this.imagePath = imagePath;
		this.videoPath = videoPath;
		this.aCriterias = aCriterias;
	}
	
	public int getId() {
		return id;
	}
	
	public void setId(int id) {
		this.id = id;
	}
	
	public int getStudentId() {
		return studentId;
	}
	
	public void setStudentId(int studentId) {
		this.studentId = studentId;
	}
	
	public int getAssessmentId() {
		return assessmentId;
	}
	
	public void setAssessmentId(int assessmentId) {
		this.assessmentId = assessmentId;
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
	
	public String getNote() {
		return note;
	}
	
	public void setNote(String note) {
		this.note = note;
	}
	
	public String getImagePath() {
		return imagePath;
	}
	
	public void setImagePath(String imagePath) {
		this.imagePath = imagePath;
	}
	
	public String getVideoPath() {
		return videoPath;
	}
	
	public void setVideoPath(String videoPath) {
		this.videoPath = videoPath;
	}
	
	public List<ACriteria> getACriterias() {
		return aCriterias;
	}
	
	public void setACriterias(List<ACriteria> aCriterias) {
		this.aCriterias = aCriterias;
	}
	
	public int getValueByCriteriaId(int criteriaId) {
		if (aCriterias != null && !aCriterias.isEmpty()) {
			for (ACriteria aCriteria : aCriterias) {
				if (aCriteria.getCriteriaId() == criteriaId)
					return aCriteria.getValue();
			}
		}
		return 0;
	}
	
	public boolean hasExtraInfo() {
		return (note != null && note.length() > 0) ||
				(imagePath != null && imagePath.length() > 0) ||
				(videoPath != null && videoPath.length() > 0);
	}
	
	public boolean hasNote() {
		return (note != null && note.length() > 0);
	}
	
	public boolean hasImage() {
		return (imagePath != null && imagePath.length() > 0);
	}
	
	public boolean hasVideo() {
		return (videoPath != null && videoPath.length() > 0);
	}

}
