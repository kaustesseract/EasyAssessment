package com.thepegeekapps.easyassessment.database;

import java.util.LinkedList;
import java.util.List;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.thepegeekapps.easyassessment.model.ACriteria;
import com.thepegeekapps.easyassessment.model.AStudent;
import com.thepegeekapps.easyassessment.model.Assessment;
import com.thepegeekapps.easyassessment.model.Criteria;
import com.thepegeekapps.easyassessment.model.Group;
import com.thepegeekapps.easyassessment.model.Rubric;
import com.thepegeekapps.easyassessment.model.Student;

public class DatabaseManager {

	protected static DatabaseManager instance;
	protected DatabaseHelper dbHelper;
	protected Context context;
	protected SQLiteDatabase db;
	
	protected DatabaseManager(Context context) {
		this.context = context;
		dbHelper = new DatabaseHelper(context);
		db = dbHelper.getWritableDatabase();
	}
	
	public static DatabaseManager getInstance(Context context) {
		if (instance == null)
			instance = new DatabaseManager(context);
		return instance;
	}
	
	/************************************************************************************************************************
	 ***************************************************** G R O U P S ****************************************************** 
	 ************************************************************************************************************************/
	
	public List<Group> getGroups() {
		List<Group> groups = null;
		try {
			Cursor c = db.query(DatabaseHelper.TABLE_GROUPS,
					null, null, null, null, null, null);
			if (c != null && c.moveToFirst()) {
				groups = new LinkedList<Group>();
				do {
					int id = c.getInt(c.getColumnIndex(DatabaseHelper.FIELD_ID));
					Group group = new Group(
						id,
						c.getString(c.getColumnIndex(DatabaseHelper.FIELD_NAME)),
						getStudentsCountByGroupId(id),
						getAssessmentsCountByGroupId(id)
					);
//					group.setStudents(getStudentsByGroupId(id));
//					group.setAssessments(getAssessmentsByGroupId(id));
					groups.add(group);
				} while (c.moveToNext());
			}
			if (c != null && !c.isClosed())
				c.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return groups;
	}
	
	public Group getGroupById(int id) {
		Group group = null;
		try {
			Cursor c = db.query(DatabaseHelper.TABLE_GROUPS,
					null, DatabaseHelper.FIELD_ID+"="+id, null, null, null, null);
			if (c != null && c.moveToFirst()) {
				group = new Group(
					id,
					c.getString(c.getColumnIndex(DatabaseHelper.FIELD_NAME)),
					getStudentsCountByGroupId(id),
					getAssessmentsCountByGroupId(id)
				);
//				group.setStudents(getStudentsByGroupId(id));
//				group.setAssessments(getAssessmentsByGroupId(id));
			}
			if (c != null && !c.isClosed())
				c.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return group;
	}
	
	public void addGroup(Group group) {
		if (group == null)
			return;
		ContentValues values = new ContentValues();
		values.put(DatabaseHelper.FIELD_NAME, group.getName());
		db.insert(DatabaseHelper.TABLE_GROUPS, null, values);
	}
	
	public void updateGroup(Group group) {
		if (group == null)
			return;
		ContentValues values = new ContentValues();
		values.put(DatabaseHelper.FIELD_NAME, group.getName());
		db.update(DatabaseHelper.TABLE_GROUPS, values, DatabaseHelper.FIELD_ID+"="+group.getId(), null);
	}
	
	public void deleteGroup(Group group) {
		if (group == null)
			return;
		db.delete(DatabaseHelper.TABLE_GROUPS, DatabaseHelper.FIELD_ID+"="+group.getId(), null);
	}
	
	/************************************************************************************************************************
	 *************************************************** S T U D E N T S **************************************************** 
	 ************************************************************************************************************************/
	
	public List<Student> getStudentsByGroupId(int groupId) {
		List<Student> students = null;
		try {
			Cursor c = db.query(DatabaseHelper.TABLE_STUDENTS, null, DatabaseHelper.FIELD_GROUP_ID + "=" + groupId, null, null, null, null);
			if (c != null && c.moveToFirst()) {
				students = new LinkedList<Student>();
				do {
					students.add(new Student(
						c.getInt(c.getColumnIndex(DatabaseHelper.FIELD_ID)),
						groupId,
						c.getString(c.getColumnIndex(DatabaseHelper.FIELD_NAME))
					));
				} while (c.moveToNext());
			}
			if (c != null && !c.isClosed())
				c.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return students;
	}
	
	public Student getStudentById(int id) {
		Student student = null;
		try {
			Cursor c = db.query(DatabaseHelper.TABLE_STUDENTS, null, DatabaseHelper.FIELD_ID + "=" + id, null, null, null, null);
			if (c != null && c.moveToFirst()) {
				student = new Student(
					id,
					c.getInt(c.getColumnIndex(DatabaseHelper.FIELD_GROUP_ID)),
					c.getString(c.getColumnIndex(DatabaseHelper.FIELD_NAME))
				);
			}
			if (c != null && !c.isClosed())
				c.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return student;
	}
	
	public int getStudentsCountByGroupId(int groupId) {
		int count = 0;
		try {
			Cursor c = db.rawQuery("select count(*) from " + DatabaseHelper.TABLE_STUDENTS + " where " + DatabaseHelper.FIELD_GROUP_ID+"="+groupId, null);
			if (c != null && c.moveToFirst())
				count = c.getInt(0);
			if (c != null && !c.isClosed())
				c.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return count;
	}
	
	public void addStudent(Student student) {
		if (student == null)
			return;
		ContentValues values = new ContentValues();
		values.put(DatabaseHelper.FIELD_GROUP_ID, student.getGroupId());
		values.put(DatabaseHelper.FIELD_NAME, student.getName());
		if (db != null)
			db.insert(DatabaseHelper.TABLE_STUDENTS, null, values);
	}
	
	public void updateStudent(Student student) {
		if (student == null)
			return;
		ContentValues values = new ContentValues();
		values.put(DatabaseHelper.FIELD_GROUP_ID, student.getGroupId());
		values.put(DatabaseHelper.FIELD_NAME, student.getName());
		if (db != null)
			db.update(DatabaseHelper.TABLE_STUDENTS, values, DatabaseHelper.FIELD_ID + "=" + student.getId(), null);
	}
	
	public void deleteStudent(Student student) {
		if (student == null)
			return;
		if (db != null)
			db.delete(DatabaseHelper.TABLE_STUDENTS, DatabaseHelper.FIELD_ID + "=" + student.getId(), null);
	}
	
	/************************************************************************************************************************
	 **************************************************** R U B R I C S ***************************************************** 
	 ************************************************************************************************************************/
	
	public List<Rubric> getRubrics() {
		List<Rubric> rubrics = null;
		try {
			Cursor c = db.query(DatabaseHelper.TABLE_RUBRICS, null, null, null, null, null, null);
			if (c != null && c.moveToFirst()) {
				rubrics = new LinkedList<Rubric>();
				do {
					int id = c.getInt(c.getColumnIndex(DatabaseHelper.FIELD_ID)); 
					Rubric rubric = new Rubric(
						id,
						c.getString(c.getColumnIndex(DatabaseHelper.FIELD_NAME)),
						c.getInt(c.getColumnIndex(DatabaseHelper.FIELD_START_SCALE)),
						c.getInt(c.getColumnIndex(DatabaseHelper.FIELD_END_SCALE))
					);
					rubric.setCriterias(getCriteriasByRubricId(id));
					rubrics.add(rubric);
				} while (c.moveToNext());
			}
			if (c != null && !c.isClosed())
				c.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return rubrics;
	}
	
	public Rubric getRubricById(int id) {
		Rubric rubric = null;
		try {
			Cursor c = db.query(DatabaseHelper.TABLE_RUBRICS, null, DatabaseHelper.FIELD_ID + "=" + id, null, null, null, null);
			if (c != null && c.moveToFirst()) {
				rubric = new Rubric(
					id,
					c.getString(c.getColumnIndex(DatabaseHelper.FIELD_NAME)),
					c.getInt(c.getColumnIndex(DatabaseHelper.FIELD_START_SCALE)),
					c.getInt(c.getColumnIndex(DatabaseHelper.FIELD_END_SCALE))
				);
				rubric.setCriterias(getCriteriasByRubricId(id));
			}
			if (c != null && !c.isClosed())
				c.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return rubric;
	}
	
	public void addRubric(Rubric rubric) {
		if (rubric == null)
			return;
		ContentValues values = new ContentValues();
		values.put(DatabaseHelper.FIELD_NAME, rubric.getName());
		values.put(DatabaseHelper.FIELD_START_SCALE, rubric.getStartScale());
		values.put(DatabaseHelper.FIELD_END_SCALE, rubric.getEndScale());
		if (db != null)
			db.insert(DatabaseHelper.TABLE_RUBRICS, null, values);
	}
	
	public void updateRubric(Rubric rubric) {
		if (rubric == null)
			return;
		ContentValues values = new ContentValues();
		values.put(DatabaseHelper.FIELD_NAME, rubric.getName());
		values.put(DatabaseHelper.FIELD_START_SCALE, rubric.getStartScale());
		values.put(DatabaseHelper.FIELD_END_SCALE, rubric.getEndScale());
		if (db != null)
			db.update(DatabaseHelper.TABLE_RUBRICS, values, DatabaseHelper.FIELD_ID + "=" + rubric.getId(), null); 
	}
	
	public void deleteRubric(Rubric rubric) {
		if (rubric == null)
			return;
		if (db != null)
			db.delete(DatabaseHelper.TABLE_RUBRICS, DatabaseHelper.FIELD_ID + "=" + rubric.getId(), null);
	}
	
	/************************************************************************************************************************
	 ************************************************** C R I T E R I A S *************************************************** 
	 ************************************************************************************************************************/
	
	public List<Criteria> getCriteriasByRubricId(int rubricId) {
		List<Criteria> criterias = null;
		try {
			Cursor c = db.query(DatabaseHelper.TABLE_CRITERIAS, null, DatabaseHelper.FIELD_RUBRIC_ID + "=" + rubricId, null, null, null, null);
			if (c != null && c.moveToFirst()) {
				criterias = new LinkedList<Criteria>();
				do {
					criterias.add(new Criteria(
						c.getInt(c.getColumnIndex(DatabaseHelper.FIELD_ID)),
						rubricId,
						c.getString(c.getColumnIndex(DatabaseHelper.FIELD_NAME)),
						c.getInt(c.getColumnIndex(DatabaseHelper.FIELD_START_SCALE)),
						c.getInt(c.getColumnIndex(DatabaseHelper.FIELD_END_SCALE))
					));
				} while (c.moveToNext());
			}
			if (c != null && !c.isClosed())
				c.close();
				
		} catch (Exception e) {
			e.printStackTrace();
		}
		return criterias;
	}
	
	public void addCriteria(Criteria criteria) {
		if (criteria == null)
			return;
		ContentValues values = new ContentValues();
		values.put(DatabaseHelper.FIELD_RUBRIC_ID, criteria.getRubricId());
		values.put(DatabaseHelper.FIELD_NAME, criteria.getName());
		values.put(DatabaseHelper.FIELD_START_SCALE, criteria.getStartScale());
		values.put(DatabaseHelper.FIELD_END_SCALE, criteria.getEndScale());
		if (db != null)
			db.insert(DatabaseHelper.TABLE_CRITERIAS, null, values);
	}
	
	public void updateCriteria(Criteria criteria) {
		if (criteria == null)
			return;
		ContentValues values = new ContentValues();
		values.put(DatabaseHelper.FIELD_RUBRIC_ID, criteria.getRubricId());
		values.put(DatabaseHelper.FIELD_NAME, criteria.getName());
		values.put(DatabaseHelper.FIELD_START_SCALE, criteria.getStartScale());
		values.put(DatabaseHelper.FIELD_END_SCALE, criteria.getEndScale());
		if (db != null)
			db.update(DatabaseHelper.TABLE_CRITERIAS, values, DatabaseHelper.FIELD_ID + "=" + criteria.getId(), null);
	}
	
	public void deleteCriteria(Criteria criteria) {
		if (criteria == null)
			return;
		if (db != null)
			db.delete(DatabaseHelper.TABLE_CRITERIAS, DatabaseHelper.FIELD_ID + "=" + criteria.getId(), null);
	}
	
	/************************************************************************************************************************
	 ************************************************ A S S E S S M E N T S ************************************************* 
	 ************************************************************************************************************************/
	
	public List<Assessment> getAssessmentsByGroupId(int groupId) {
		List<Assessment> assessments = null;
		try {
			Cursor c = db.query(DatabaseHelper.TABLE_ASSESSMENTS, null, DatabaseHelper.FIELD_GROUP_ID + "=" + groupId, null, null, null, null);
			if (c != null && c.moveToFirst()) {
				assessments = new LinkedList<Assessment>();
				do {
					int id = c.getInt(c.getColumnIndex(DatabaseHelper.FIELD_ID));
					Assessment assessment = new Assessment(
						id,
						groupId,
						c.getString(c.getColumnIndex(DatabaseHelper.FIELD_GROUP_NAME)),
						c.getInt(c.getColumnIndex(DatabaseHelper.FIELD_RUBRIC_ID)),
						c.getString(c.getColumnIndex(DatabaseHelper.FIELD_RUBRIC_NAME)),
						c.getString(c.getColumnIndex(DatabaseHelper.FIELD_NAME)),
						c.getString(c.getColumnIndex(DatabaseHelper.FIELD_TAKEN_DATE))
					);
					assessment.setStudents(getAStudentsByAssessmentId(id));
					assessments.add(assessment);
				} while (c.moveToNext());
			}
			if (c != null && !c.isClosed())
				c.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return assessments;
	}
	
	public List<Assessment> getAssessmentsByStudentId(int studentId) {
		List<Assessment> assessments = null;
		try {
			Cursor c = db.query(true, DatabaseHelper.TABLE_A_STUDENTS, new String[] {DatabaseHelper.FIELD_ASSESSMENT_ID}, 
					DatabaseHelper.FIELD_STUDENT_ID+"="+studentId, null, null, null, null, null);
			if (c != null && c.moveToFirst()) {
				String idStr = "";
				do {
					idStr += c.getInt(c.getColumnIndex(DatabaseHelper.FIELD_ASSESSMENT_ID)) + ",";
				} while (c.moveToNext());
				idStr = idStr.substring(0, idStr.length()-1);
				assessments = getAssessmentsByIds(idStr);
			}
			if (c != null && !c.isClosed())
				c.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return assessments;
	}
	
	public List<Assessment> getAssessmentsByIds(String idStr) {
		List<Assessment> assessments = null;
		try {
			Cursor c = db.query(DatabaseHelper.TABLE_ASSESSMENTS, null, DatabaseHelper.FIELD_ID+" in ("+idStr+")", null, null, null, null);
			if (c != null && c.moveToFirst()) {
				assessments = new LinkedList<Assessment>();
				do {
					assessments.add(new Assessment(
						c.getInt(c.getColumnIndex(DatabaseHelper.FIELD_ID)),
						c.getInt(c.getColumnIndex(DatabaseHelper.FIELD_GROUP_ID)),
						c.getString(c.getColumnIndex(DatabaseHelper.FIELD_GROUP_NAME)),
						c.getInt(c.getColumnIndex(DatabaseHelper.FIELD_RUBRIC_ID)),
						c.getString(c.getColumnIndex(DatabaseHelper.FIELD_RUBRIC_NAME)),
						c.getString(c.getColumnIndex(DatabaseHelper.FIELD_NAME)),
						c.getString(c.getColumnIndex(DatabaseHelper.FIELD_TAKEN_DATE))
					));
				} while (c.moveToNext());
			}
			if (c != null && !c.isClosed())
				c.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return assessments; 
	}
	
	public int getAssessmentsCountByGroupId(int groupId) {
		int count = 0;
		try {
			Cursor c = db.rawQuery("select count(*) from " + DatabaseHelper.TABLE_ASSESSMENTS + " where "+DatabaseHelper.FIELD_GROUP_ID+"="+groupId, null);
			if (c != null && c.moveToFirst())
				count = c.getInt(0);
			if (c != null && !c.isClosed())
				c.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return count;
	}
	
	public Assessment getAssessmentById(int id) {
		Assessment assessment = null;
		try {
			Cursor c = db.query(DatabaseHelper.TABLE_ASSESSMENTS, null, DatabaseHelper.FIELD_ID + "=" + id, null, null, null, null);
			if (c != null && c.moveToFirst()) {
				assessment = new Assessment(
					c.getInt(c.getColumnIndex(DatabaseHelper.FIELD_ID)),
					c.getInt(c.getColumnIndex(DatabaseHelper.FIELD_GROUP_ID)),
					c.getString(c.getColumnIndex(DatabaseHelper.FIELD_GROUP_NAME)),
					c.getInt(c.getColumnIndex(DatabaseHelper.FIELD_RUBRIC_ID)),
					c.getString(c.getColumnIndex(DatabaseHelper.FIELD_RUBRIC_NAME)),
					c.getString(c.getColumnIndex(DatabaseHelper.FIELD_NAME)),
					c.getString(c.getColumnIndex(DatabaseHelper.FIELD_TAKEN_DATE))
				);
			}
			if (c != null && !c.isClosed())
				c.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return assessment;
	}
	
	public void addAssessment(Assessment assessment) {
		if (assessment == null)
			return;
		ContentValues values = new ContentValues();
		values.put(DatabaseHelper.FIELD_GROUP_ID, assessment.getGroupId());
		values.put(DatabaseHelper.FIELD_GROUP_NAME, assessment.getGroupName());
		values.put(DatabaseHelper.FIELD_RUBRIC_ID, assessment.getRubricId());
		values.put(DatabaseHelper.FIELD_RUBRIC_NAME, assessment.getRubricName());
		values.put(DatabaseHelper.FIELD_NAME, assessment.getName());
		values.put(DatabaseHelper.FIELD_TAKEN_DATE, assessment.getTakenDate());
		if (db != null)
			db.insert(DatabaseHelper.TABLE_ASSESSMENTS, null, values);
	}
	
	public void updateAssessment(Assessment assessment) {
		if (assessment == null)
			return;
		ContentValues values = new ContentValues();
		values.put(DatabaseHelper.FIELD_GROUP_ID, assessment.getGroupId());
		values.put(DatabaseHelper.FIELD_GROUP_NAME, assessment.getGroupName());
		values.put(DatabaseHelper.FIELD_RUBRIC_ID, assessment.getRubricId());
		values.put(DatabaseHelper.FIELD_RUBRIC_NAME, assessment.getRubricName());
		values.put(DatabaseHelper.FIELD_NAME, assessment.getName());
		values.put(DatabaseHelper.FIELD_TAKEN_DATE, assessment.getTakenDate());
		if (db != null)
			db.update(DatabaseHelper.TABLE_ASSESSMENTS, values, DatabaseHelper.FIELD_ID+"="+assessment.getId(), null);
	}
	
	public void deleteAssessment(Assessment assessment) {
		if (assessment == null)
			return;
		if (db != null)
			db.delete(DatabaseHelper.TABLE_ASSESSMENTS, DatabaseHelper.FIELD_ID+"="+assessment.getId(), null);
	}
	
	/************************************************************************************************************************
	 ************************************************** A S T U D E N T S *************************************************** 
	 ************************************************************************************************************************/
	
	public List<AStudent> getAStudentsByAssessmentId(int assessmentId) {
		List<AStudent> aStudents = null;
		try {
			Cursor c = db.query(DatabaseHelper.TABLE_A_STUDENTS, null, DatabaseHelper.FIELD_ASSESSMENT_ID+"="+assessmentId, null, null, null, null);
			if (c != null && c.moveToFirst()) {
				aStudents = new LinkedList<AStudent>();
				do {
					int id = c.getInt(c.getColumnIndex(DatabaseHelper.FIELD_ID));
					AStudent aStudent = new AStudent(
						id,
						c.getInt(c.getColumnIndex(DatabaseHelper.FIELD_STUDENT_ID)),
						assessmentId,
						c.getInt(c.getColumnIndex(DatabaseHelper.FIELD_GROUP_ID)),
						c.getString(c.getColumnIndex(DatabaseHelper.FIELD_NAME)),
						c.getString(c.getColumnIndex(DatabaseHelper.FIELD_NOTE)),
						c.getString(c.getColumnIndex(DatabaseHelper.FIELD_IMAGE_PATH)),
						c.getString(c.getColumnIndex(DatabaseHelper.FIELD_VIDEO_PATH))
					);
					aStudent.setACriterias(getACriteriasByAStudentId(id));
					aStudents.add(aStudent);
				} while (c.moveToNext());
			}
			if (c != null && !c.isClosed())
				c.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return aStudents;
	}
	
	public AStudent getAStudentById(int id) {
		AStudent aStudent = null;
		try {
			Cursor c = db.query(DatabaseHelper.TABLE_A_STUDENTS, null, DatabaseHelper.FIELD_ID+"="+id, null, null, null, null);
			if (c != null && c.moveToFirst()) {
				aStudent = new AStudent(
					id,
					c.getInt(c.getColumnIndex(DatabaseHelper.FIELD_STUDENT_ID)),
					c.getInt(c.getColumnIndex(DatabaseHelper.FIELD_ASSESSMENT_ID)),
					c.getInt(c.getColumnIndex(DatabaseHelper.FIELD_GROUP_ID)),
					c.getString(c.getColumnIndex(DatabaseHelper.FIELD_NAME)),
					c.getString(c.getColumnIndex(DatabaseHelper.FIELD_NOTE)),
					c.getString(c.getColumnIndex(DatabaseHelper.FIELD_IMAGE_PATH)),
					c.getString(c.getColumnIndex(DatabaseHelper.FIELD_VIDEO_PATH))
				);
				aStudent.setACriterias(getACriteriasByAStudentId(id));
			}
			if (c != null && !c.isClosed())
				c.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return aStudent;
	}
	
	public AStudent getAStudent(int assessmentId, int studentId) {
		AStudent aStudent = null;
		try {
			Cursor c = db.query(DatabaseHelper.TABLE_A_STUDENTS, null, 
					DatabaseHelper.FIELD_ASSESSMENT_ID+"="+assessmentId+" and "+DatabaseHelper.FIELD_STUDENT_ID+"="+studentId, null, null, null, null);
			if (c != null && c.moveToFirst()) {
				int id = c.getInt(c.getColumnIndex(DatabaseHelper.FIELD_ID));
				aStudent = new AStudent(
					id,
					studentId,
					assessmentId,
					c.getInt(c.getColumnIndex(DatabaseHelper.FIELD_GROUP_ID)),
					c.getString(c.getColumnIndex(DatabaseHelper.FIELD_NAME)),
					c.getString(c.getColumnIndex(DatabaseHelper.FIELD_NOTE)),
					c.getString(c.getColumnIndex(DatabaseHelper.FIELD_IMAGE_PATH)),
					c.getString(c.getColumnIndex(DatabaseHelper.FIELD_VIDEO_PATH))
				);
				aStudent.setACriterias(getACriteriasByAStudentId(id));
			}
			if (c != null && !c.isClosed())
				c.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return aStudent;
	}
	
	public void addAStudents(List<AStudent> aStudents) {
		if (aStudents == null || aStudents.isEmpty())
			return;
		for (AStudent aStudent : aStudents)
			addAStudent(aStudent);
	}
	
	public void addAStudent(AStudent aStudent) {
		if (aStudent == null)
			return;
		ContentValues values = new ContentValues();
		values.put(DatabaseHelper.FIELD_STUDENT_ID, aStudent.getStudentId());
		values.put(DatabaseHelper.FIELD_ASSESSMENT_ID, aStudent.getAssessmentId());
		values.put(DatabaseHelper.FIELD_GROUP_ID, aStudent.getGroupId());
		values.put(DatabaseHelper.FIELD_NAME, aStudent.getName());
		values.put(DatabaseHelper.FIELD_NOTE, aStudent.getNote());
		values.put(DatabaseHelper.FIELD_IMAGE_PATH, aStudent.getImagePath());
		values.put(DatabaseHelper.FIELD_VIDEO_PATH, aStudent.getVideoPath());
		
		if (db != null)
			db.insert(DatabaseHelper.TABLE_A_STUDENTS, null, values);
	}
	
	public void updateAStudents(List<AStudent> aStudents, boolean updateACriterias) {
		if (aStudents == null || aStudents.isEmpty())
			return;
		for (AStudent aStudent : aStudents)
			updateAStudent(aStudent, updateACriterias);
	}
	
	public void updateAStudent(AStudent aStudent, boolean updateACriterias) {
		if (aStudent == null)
			return;
		ContentValues values = new ContentValues();
		values.put(DatabaseHelper.FIELD_STUDENT_ID, aStudent.getStudentId());
		values.put(DatabaseHelper.FIELD_ASSESSMENT_ID, aStudent.getAssessmentId());
		values.put(DatabaseHelper.FIELD_GROUP_ID, aStudent.getGroupId());
		values.put(DatabaseHelper.FIELD_NAME, aStudent.getName());
		values.put(DatabaseHelper.FIELD_NOTE, aStudent.getNote());
		values.put(DatabaseHelper.FIELD_IMAGE_PATH, aStudent.getImagePath());
		values.put(DatabaseHelper.FIELD_VIDEO_PATH, aStudent.getVideoPath());
		
		if (db != null)
			db.update(DatabaseHelper.TABLE_A_STUDENTS, values, DatabaseHelper.FIELD_ID+"="+aStudent.getId(), null);
		
		if (updateACriterias)
			updateACriterias(aStudent.getACriterias());
	}
	
	public void deleteAStudents(List<AStudent> aStudents) {
		if (aStudents == null || aStudents.isEmpty())
			return;
		for (AStudent aStudent : aStudents)
			deleteAStudent(aStudent);
	}
	
	public void deleteAStudent(AStudent aStudent) {
		if (aStudent == null)
			return;
		if (db != null)
			db.delete(DatabaseHelper.TABLE_A_STUDENTS, DatabaseHelper.FIELD_ID+"="+aStudent.getId(), null);
	}
	
	/************************************************************************************************************************
	 ************************************************* A C R I T E R I A S ************************************************** 
	 ************************************************************************************************************************/
	
	public List<ACriteria> getACriteriasByAStudentId(int aStudentId) {
		List<ACriteria> aCriterias = null;
		try {
			Cursor c = db.query(DatabaseHelper.TABLE_A_CRITERIAS, null, DatabaseHelper.FIELD_A_STUDENT_ID+"="+aStudentId, null, null, null, null);
			if (c != null && c.moveToFirst()) {
				aCriterias = new LinkedList<ACriteria>();
				do {
					aCriterias.add(new ACriteria(
						c.getInt(c.getColumnIndex(DatabaseHelper.FIELD_ID)),
						c.getInt(c.getColumnIndex(DatabaseHelper.FIELD_A_STUDENT_ID)),
						c.getInt(c.getColumnIndex(DatabaseHelper.FIELD_CRITERIA_ID)),
						c.getString(c.getColumnIndex(DatabaseHelper.FIELD_NAME)),
						c.getInt(c.getColumnIndex(DatabaseHelper.FIELD_START_SCALE)),
						c.getInt(c.getColumnIndex(DatabaseHelper.FIELD_END_SCALE)),
						c.getInt(c.getColumnIndex(DatabaseHelper.FIELD_VALUE))
					));
				} while (c.moveToNext());
			}
			if (c != null && !c.isClosed())
				c.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return aCriterias;
	}
	
	public void addACriterias(List<ACriteria> aCriterias) {
		if (aCriterias == null || aCriterias.isEmpty())
			return;
		for (ACriteria aCriteria : aCriterias)
			addACriteria(aCriteria);
	}
	
	public void addACriteria(ACriteria aCriteria) {
		if (aCriteria == null)
			return;
		ContentValues values = new ContentValues();
		values.put(DatabaseHelper.FIELD_A_STUDENT_ID, aCriteria.getAStudentId());
		values.put(DatabaseHelper.FIELD_CRITERIA_ID, aCriteria.getCriteriaId());
		values.put(DatabaseHelper.FIELD_NAME, aCriteria.getName());
		values.put(DatabaseHelper.FIELD_START_SCALE, aCriteria.getStartScale());
		values.put(DatabaseHelper.FIELD_END_SCALE, aCriteria.getEndScale());
		values.put(DatabaseHelper.FIELD_VALUE, aCriteria.getValue());
		if (db != null)
			db.insert(DatabaseHelper.TABLE_A_CRITERIAS, null, values);
	}
	
	public void updateACriterias(List<ACriteria> aCriterias) {
		if (aCriterias == null || aCriterias.isEmpty())
			return;
		for (ACriteria aCriteria : aCriterias)
			updateACriteria(aCriteria);
	}
	
	public void updateACriteria(ACriteria aCriteria) {
		if (aCriteria == null)
			return;
		ContentValues values = new ContentValues();
		values.put(DatabaseHelper.FIELD_A_STUDENT_ID, aCriteria.getAStudentId());
		values.put(DatabaseHelper.FIELD_CRITERIA_ID, aCriteria.getCriteriaId());
		values.put(DatabaseHelper.FIELD_NAME, aCriteria.getName());
		values.put(DatabaseHelper.FIELD_START_SCALE, aCriteria.getStartScale());
		values.put(DatabaseHelper.FIELD_END_SCALE, aCriteria.getEndScale());
		values.put(DatabaseHelper.FIELD_VALUE, aCriteria.getValue());
		if (db != null)
			db.update(DatabaseHelper.TABLE_A_CRITERIAS, values, DatabaseHelper.FIELD_ID+"="+aCriteria.getId(), null);
	}
	
	public void deleteACriterias(List<ACriteria> aCriterias) {
		if (aCriterias == null || aCriterias.isEmpty())
			return;
		for (ACriteria aCriteria : aCriterias)
			deleteACriteria(aCriteria);
	}
	
	public void deleteACriteria(ACriteria aCriteria) {
		if (aCriteria == null)
			return;
		if (db != null)
			db.delete(DatabaseHelper.TABLE_A_CRITERIAS, DatabaseHelper.FIELD_ID+"="+aCriteria.getId(), null);
	}
	
}
