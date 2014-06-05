package com.thepegeekapps.easyassessment.screen;

import java.util.List;

import android.content.Intent;
import android.os.Bundle;
import android.text.format.Time;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import com.thepegeekapps.easyassessment.R;
import com.thepegeekapps.easyassessment.adapter.SelectRubricAdapter;
import com.thepegeekapps.easyassessment.database.DatabaseHelper;
import com.thepegeekapps.easyassessment.listener.OnSwipeTouchListener;
import com.thepegeekapps.easyassessment.model.ACriteria;
import com.thepegeekapps.easyassessment.model.AStudent;
import com.thepegeekapps.easyassessment.model.Assessment;
import com.thepegeekapps.easyassessment.model.Rubric;
import com.thepegeekapps.easyassessment.util.Utils;

public class SelectRubricScreen extends BaseScreen implements OnClickListener, OnItemClickListener {
	
	protected ListView rubricsList;
	protected TextView emptyView;
	protected Button backBtn;
	
	protected int assessmentId;
	protected Assessment assessment;
	protected List<Rubric> rubrics;
	protected SelectRubricAdapter adapter;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.select_rubric_screen);
		getIntentData();
		setTitle(R.string.select_rubric);
		
		rubrics = dbManager.getRubrics();
		adapter = new SelectRubricAdapter(this, rubrics);
		
		initializeViews();
		checkEmptyList();
	}
	
	protected void getIntentData() {
		Intent intent = getIntent();
		if (intent != null && intent.hasExtra(DatabaseHelper.FIELD_ASSESSMENT_ID)) {
			assessmentId = intent.getIntExtra(DatabaseHelper.FIELD_ASSESSMENT_ID, 0);
			assessment = dbManager.getAssessmentById(assessmentId);
		}
	}
	
	protected void initializeViews() {
		emptyView = (TextView) findViewById(R.id.emptyView);
		
		rubricsList = (ListView) findViewById(R.id.rubricsList);
		rubricsList.setAdapter(adapter);
		rubricsList.setOnItemClickListener(this);
		
		backBtn = (Button) findViewById(R.id.backBtn);
		backBtn.setVisibility(View.VISIBLE);
		backBtn.setOnClickListener(this);
		
		View titlebar = findViewById(R.id.titlebar);
		titlebar.setOnTouchListener(new OnSwipeTouchListener() {
			@Override
			public void onSwipeRight() {
				Intent intent = new Intent(SelectRubricScreen.this, MainScreen.class);
				intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
				startActivity(intent);
				overridePendingTransition(R.anim.view_transition_in_right, R.anim.view_transition_out_right);
			}
		});
	}
	
	protected void checkEmptyList() {
		boolean rubricsNotEmpty = (rubrics != null && !rubrics.isEmpty());
		rubricsList.setVisibility(rubricsNotEmpty ? View.VISIBLE : View.GONE);
		emptyView.setVisibility(rubricsNotEmpty ? View.GONE : View.VISIBLE);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.backBtn:
			onBackPressed();
			break;
		}
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
		Rubric rubric = rubrics.get(position);
		
		if (assessment != null) {
			assessment.setRubricId(rubric.getId());
			assessment.setRubricName(rubric.getName());
			
			Time time = new Time();
			time.setToNow();
			String takenDate = (time.month+1) + "/" + time.monthDay + "/" + time.year;
			assessment.setTakenDate(takenDate);
			
			dbManager.updateAssessment(assessment);
		}
		
		List<AStudent> aStudents = dbManager.getAStudentsByAssessmentId(assessmentId);
		List<ACriteria> aCriterias = Utils.criteriasToACriterias(aStudents, rubric.getCriterias());
		dbManager.addACriterias(aCriterias);
		
		Intent intent = new Intent(this, AssessmentScreen.class);
		intent.putExtra(DatabaseHelper.FIELD_ASSESSMENT_ID, assessmentId);
		startActivity(intent);
	}

}
