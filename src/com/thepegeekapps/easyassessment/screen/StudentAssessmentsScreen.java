package com.thepegeekapps.easyassessment.screen;

import java.util.List;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import com.thepegeekapps.easyassessment.R;
import com.thepegeekapps.easyassessment.adapter.StudentAssessmentsAdapter;
import com.thepegeekapps.easyassessment.database.DatabaseHelper;
import com.thepegeekapps.easyassessment.listener.OnSwipeTouchListener;
import com.thepegeekapps.easyassessment.model.Assessment;
import com.thepegeekapps.easyassessment.model.Student;

public class StudentAssessmentsScreen extends BaseScreen implements OnClickListener {
	
	protected ListView assessmentsList;
	protected TextView emptyView;
	protected Button backBtn;
	
	protected int studentId;
	protected Student student;
	protected StudentAssessmentsAdapter adapter;
	protected List<Assessment> assessments;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.student_assessments_screen);
		getIntentData();
		setTitle(student != null ? student.getName() : getString(R.string.student_name));
		
		assessments = dbManager.getAssessmentsByStudentId(studentId);
		adapter = new StudentAssessmentsAdapter(this, assessments, student);
		
		initializeViews();
		checkEmptyList();
	}
	
	protected void getIntentData() {
		Intent intent = getIntent();
		if (intent != null && intent.hasExtra(DatabaseHelper.FIELD_STUDENT_ID)) {
			studentId = intent.getIntExtra(DatabaseHelper.FIELD_STUDENT_ID, 0);
			student = dbManager.getStudentById(studentId);
		}
	}
	
	protected void initializeViews() {
		emptyView = (TextView) findViewById(R.id.emptyView);
		
		assessmentsList = (ListView) findViewById(R.id.assessmentsList);
		assessmentsList.setAdapter(adapter);
		
		backBtn = (Button) findViewById(R.id.backBtn);
		backBtn.setVisibility(View.VISIBLE);
		backBtn.setOnClickListener(this);
		
		View titlebar = findViewById(R.id.titlebar);
		titlebar.setOnTouchListener(new OnSwipeTouchListener() {
			@Override
			public void onSwipeRight() {
				Intent intent = new Intent(StudentAssessmentsScreen.this, MainScreen.class);
				intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
				startActivity(intent);
				overridePendingTransition(R.anim.view_transition_in_right, R.anim.view_transition_out_right);
			}
		});
	}
	
	protected void checkEmptyList() {
		boolean assessmentsNotEmpty = (assessments != null && !assessments.isEmpty());
		assessmentsList.setVisibility(assessmentsNotEmpty ? View.VISIBLE : View.GONE);
		emptyView.setVisibility(assessmentsNotEmpty ? View.GONE : View.VISIBLE);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.backBtn:
			onBackPressed();
		}
	}

}
