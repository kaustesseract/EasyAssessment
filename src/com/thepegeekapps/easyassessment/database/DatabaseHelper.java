package com.thepegeekapps.easyassessment.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DatabaseHelper extends SQLiteOpenHelper {

	public static final String DATABASE_NAME = "easy_assessment";
	public static final int DATABASE_VERSION = 1;
	
	public static final String TABLE_GROUPS = "groups";
	public static final String TABLE_STUDENTS = "students";
	public static final String TABLE_RUBRICS = "rubrics";
	public static final String TABLE_CRITERIAS = "criterias";
	public static final String TABLE_ASSESSMENTS = "assessments";
	public static final String TABLE_A_STUDENTS = "a_students";
	public static final String TABLE_A_CRITERIAS = "a_criterias";
	
	public static final String FIELD_ID = "id";
	public static final String FIELD_NAME = "name";
	public static final String FIELD_GROUP_ID = "group_id";
	public static final String FIELD_START_SCALE = "start_scale";
	public static final String FIELD_END_SCALE = "end_scale";
	public static final String FIELD_RUBRIC_ID = "rubric_id";
	public static final String FIELD_ASSESSMENT_ID = "assessment_id";
	public static final String FIELD_TAKEN_DATE = "taken_date";
	public static final String FIELD_STUDENT_ID = "student_id";
	public static final String FIELD_NOTE = "note";
	public static final String FIELD_IMAGE_PATH = "image_path";
	public static final String FIELD_VIDEO_PATH = "video_path";
	public static final String FIELD_A_STUDENT_ID = "a_student_id";
	public static final String FIELD_CRITERIA_ID = "criteria_id";
	public static final String FIELD_VALUE = "value";
	public static final String FIELD_GROUP_NAME = "group_name";
	public static final String FIELD_RUBRIC_NAME = "rubric_name";
	
	public static final String CREATE_TABLE_GROUPS = 
			"create table if not exists " + TABLE_GROUPS + " (" +
			FIELD_ID + " integer primary key autoincrement, " +
			FIELD_NAME + " text);";
	
	public static final String DROP_TABLE_GROUPS = 
			"drop table if exists " + TABLE_GROUPS;
	
	public static final String CREATE_TABLE_STUDENTS = 
			"create table if not exists " + TABLE_STUDENTS + " (" +
			FIELD_ID + " integer primary key autoincrement, " +
			FIELD_GROUP_ID + " integer, " +
			FIELD_NAME + " text);";
	
	public static final String DROP_TABLE_STUDENTS = 
			"drop table if exists " + TABLE_STUDENTS;
	
	public static final String CREATE_TABLE_RUBRICS = 
			"create table if not exists "  +TABLE_RUBRICS + " (" +
			FIELD_ID + " integer primary key autoincrement, " +
			FIELD_NAME + " text, " +
			FIELD_START_SCALE + " integer, " +
			FIELD_END_SCALE + " integer);";
	
	public static final String DROP_TABLE_RUBRICS =
			"drop table if exists " + TABLE_RUBRICS;
	
	public static final String CREATE_TABLE_CRITERIAS =
			"create table if not exists " + TABLE_CRITERIAS + " (" +
			FIELD_ID + " integer primary key autoincrement, " +
			FIELD_RUBRIC_ID + " integer, " +
			FIELD_NAME + " text, " +
			FIELD_START_SCALE + " integer, " +
			FIELD_END_SCALE + " integer);";
	
	public static final String DROP_TABLE_CRITERIAS =
			"drop table if exists " + TABLE_CRITERIAS;
	
	public static final String CREATE_TABLE_ASSESSMENTS =
			"create table if not exists " + TABLE_ASSESSMENTS + " (" +
			FIELD_ID + " integer primary key autoincrement, " +
			FIELD_GROUP_ID + " integer, " +
			FIELD_GROUP_NAME + " text, " +
			FIELD_RUBRIC_ID + " integer, " +
			FIELD_RUBRIC_NAME + " text, " +
			FIELD_NAME + " text, " +
			FIELD_TAKEN_DATE + " text);";
	
	public static final String DROP_TABLE_ASSESSMENTS =
			"drop table if exists " + TABLE_ASSESSMENTS;
	
	public static final String CREATE_TABLE_A_STUDENTS =
			"create table if not exists " + TABLE_A_STUDENTS + " (" +
			FIELD_ID + " integer primary key autoincrement, " +
			FIELD_STUDENT_ID + " integer, " +
			FIELD_ASSESSMENT_ID + " integer, " +
			FIELD_GROUP_ID + " integer, " +
			FIELD_NAME + " text, " +
			FIELD_NOTE + " text, " +
			FIELD_IMAGE_PATH + " text, " +
			FIELD_VIDEO_PATH + " text);";
	
	public static final String DROP_TABLE_A_STUDENTS =
			"drop table if exists " + TABLE_A_STUDENTS;
	
	public static final String CREATE_TABLE_A_CRITERIAS =
			"create table if not exists " + TABLE_A_CRITERIAS + " (" +
			FIELD_ID + " integer primary key autoincrement, " +
			FIELD_A_STUDENT_ID + " integer, " +
			FIELD_CRITERIA_ID + " integer, " +
			FIELD_NAME + " text, " +
			FIELD_START_SCALE + " integer, " +
			FIELD_END_SCALE + " integer, " +
			FIELD_VALUE + " integer);";
	
	public static final String DROP_TABLE_A_CRITERIAS =
			"drop table if exists " + TABLE_A_CRITERIAS;
	
	protected Context context;
	
	public DatabaseHelper(Context context) {
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
		this.context = context;
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		createTables(db);
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		dropTables(db);
		onCreate(db);
	}
	
	protected void createTables(SQLiteDatabase db) {
		if (db != null) {
			db.execSQL(CREATE_TABLE_GROUPS);
			db.execSQL(CREATE_TABLE_STUDENTS);
			db.execSQL(CREATE_TABLE_RUBRICS);
			db.execSQL(CREATE_TABLE_CRITERIAS);
			db.execSQL(CREATE_TABLE_ASSESSMENTS);
			db.execSQL(CREATE_TABLE_A_STUDENTS);
			db.execSQL(CREATE_TABLE_A_CRITERIAS);
		}
	}
	
	protected void dropTables(SQLiteDatabase db) {
		if (db != null) {
			db.execSQL(DROP_TABLE_GROUPS);
			db.execSQL(DROP_TABLE_STUDENTS);
			db.execSQL(DROP_TABLE_RUBRICS);
			db.execSQL(DROP_TABLE_CRITERIAS);
			db.execSQL(DROP_TABLE_ASSESSMENTS);
			db.execSQL(DROP_TABLE_A_STUDENTS);
			db.execSQL(DROP_TABLE_A_CRITERIAS);
		}
	}

}
