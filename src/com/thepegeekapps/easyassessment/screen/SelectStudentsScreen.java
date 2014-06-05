package com.thepegeekapps.easyassessment.screen;

import java.util.List;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import com.thepegeekapps.easyassessment.R;
import com.thepegeekapps.easyassessment.adapter.SelectStudentAdapter;
import com.thepegeekapps.easyassessment.database.DatabaseHelper;
import com.thepegeekapps.easyassessment.listener.OnSwipeTouchListener;
import com.thepegeekapps.easyassessment.model.AStudent;
import com.thepegeekapps.easyassessment.model.Assessment;
import com.thepegeekapps.easyassessment.model.Student;
import com.thepegeekapps.easyassessment.util.Utils;

public class SelectStudentsScreen extends BaseScreen implements OnClickListener, OnItemClickListener {
	
	protected ListView studentsList;
	protected TextView emptyView;
	protected Button backBtn;
	protected Button selectAllBtn;
	protected Button nextBtn;
	
	protected int assessmentId;
	protected int groupId;
	protected Assessment assessment;
	protected List<Student> students;
	protected SelectStudentAdapter adapter;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.select_students_screen);
		getIntentData();
		setTitle(R.string.select_students);
		
		students = dbManager.getStudentsByGroupId(groupId);
		adapter = new SelectStudentAdapter(this, students);
		
		initializeViews();
		checkEmptyList();
	}
	
	protected void getIntentData() {
		Intent intent = getIntent();
		if (intent != null && intent.hasExtra(DatabaseHelper.FIELD_ASSESSMENT_ID)) {
			assessmentId = intent.getIntExtra(DatabaseHelper.FIELD_ASSESSMENT_ID, 0);
			assessment = dbManager.getAssessmentById(assessmentId);
			groupId = (assessment != null) ? assessment.getGroupId() : 0;
		}
	}
	
	protected void initializeViews() {
		emptyView = (TextView) findViewById(R.id.emptyView);
		
		studentsList = (ListView) findViewById(R.id.studentsList);
		studentsList.setAdapter(adapter);
		studentsList.setOnItemClickListener(this);
		
		backBtn = (Button) findViewById(R.id.backBtn);
		backBtn.setVisibility(View.VISIBLE);
		backBtn.setOnClickListener(this);
		
		if (students != null && !students.isEmpty()) {
			selectAllBtn = (Button) findViewById(R.id.addNewBtn);
			selectAllBtn.setVisibility(View.VISIBLE);
			selectAllBtn.setText(R.string.select_all);
			selectAllBtn.setOnClickListener(this);
		}
		
		nextBtn = (Button) findViewById(R.id.nextBtn);
		nextBtn.setVisibility(adapter.getCheckedCount() > 0 ? View.VISIBLE : View.INVISIBLE);
		nextBtn.setOnClickListener(this);
		
		View titlebar = findViewById(R.id.titlebar);
		titlebar.setOnTouchListener(new OnSwipeTouchListener() {
			@Override
			public void onSwipeRight() {
				Intent intent = new Intent(SelectStudentsScreen.this, MainScreen.class);
				intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
				startActivity(intent);
				overridePendingTransition(R.anim.view_transition_in_right, R.anim.view_transition_out_right);
			}
		});
	}
	
	protected void checkEmptyList() {
		boolean studentsNotEmpty = (students != null && !students.isEmpty());
		studentsList.setVisibility(studentsNotEmpty ? View.VISIBLE : View.GONE);
		emptyView.setVisibility(studentsNotEmpty ? View.GONE : View.VISIBLE);
	}
	
	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.backBtn:
			onBackPressed();
			break;
		case R.id.addNewBtn:
			adapter.toggleSelectAll();
			nextBtn.setVisibility(adapter.getCheckedCount() > 0 ? View.VISIBLE : View.INVISIBLE);
			break;
		case R.id.nextBtn:
			List<AStudent> aStudents = Utils.studentsToAStudents(adapter.getCheckedStudents(), assessmentId);
			dbManager.addAStudents(aStudents);
			
			Intent intent = new Intent(this, SelectRubricScreen.class);
			intent.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
			intent.putExtra(DatabaseHelper.FIELD_ASSESSMENT_ID, assessmentId);
			startActivity(intent);
			break;
		}
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
		Student student = students.get(position);
		student.setChecked(!student.isChecked());
		adapter.notifyDataSetChanged();
		nextBtn.setVisibility(adapter.getCheckedCount() > 0 ? View.VISIBLE : View.INVISIBLE);
	}

}
