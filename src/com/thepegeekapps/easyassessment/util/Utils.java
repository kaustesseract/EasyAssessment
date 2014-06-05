package com.thepegeekapps.easyassessment.util;

import java.util.LinkedList;
import java.util.List;

import com.thepegeekapps.easyassessment.model.ACriteria;
import com.thepegeekapps.easyassessment.model.AStudent;
import com.thepegeekapps.easyassessment.model.Criteria;
import com.thepegeekapps.easyassessment.model.Student;

public class Utils {
	
	public static String[] getScaleItems(int startScale, int endScale) {
		int count = endScale - startScale + 1;
		String[] items = new String[count];
		for (int i=0; i<count; i++)
			items[i] = String.valueOf(startScale + i);
		return items;
	}
	
	public static List<AStudent> studentsToAStudents(List<Student> students, int assessmentId) {
		if (students == null || students.isEmpty() || assessmentId == 0)
			return null;
		List<AStudent> aStudents = new LinkedList<AStudent>();
		for (Student student : students)
			aStudents.add(new AStudent(student, assessmentId));
		return aStudents;
	}
	
	public static List<ACriteria> criteriasToACriterias(List<AStudent> aStudents, List<Criteria> criterias) {
		if (aStudents == null || aStudents.isEmpty() || criterias == null || criterias.isEmpty())
			return null;
		List<ACriteria> aCriterias = new LinkedList<ACriteria>();
		for (AStudent aStudent : aStudents) 
			for (Criteria criteria : criterias)
				aCriterias.add(new ACriteria(criteria, aStudent.getId()));
		return aCriterias;
	}
	
	public static CharSequence[] aStudentsAsStringArray(List<AStudent> aStudents) {
		if (aStudents == null || aStudents.isEmpty())
			return new CharSequence[] {};
		CharSequence[] items = new CharSequence[aStudents.size()];
		for (int i=0; i<aStudents.size(); i++)
			items[i] = aStudents.get(i).getName();
		return items;
	}
	
	public static final String getReadableFilesize(long bytes) {
		String s = " B";
		String size = bytes + s;
		
		while (bytes > 1024) {
			if (s.equals(" B"))	s = " KB";
			else if (s.equals(" KB")) s = " MB";
			else if (s.equals(" MB")) s = " GB";
			else if (s.equals(" GB")) s = " TB";
			
			size = (bytes / 1024) + "." + (bytes % 1024) + s;
			bytes = bytes / 1024;
		}
		
		return size;
	}

}
