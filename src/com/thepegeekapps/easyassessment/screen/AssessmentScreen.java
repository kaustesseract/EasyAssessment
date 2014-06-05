package com.thepegeekapps.easyassessment.screen;

import java.util.List;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.ViewFlipper;

import com.thepegeekapps.easyassessment.R;
import com.thepegeekapps.easyassessment.database.DatabaseHelper;
import com.thepegeekapps.easyassessment.listener.OnSwipeTouchListener;
import com.thepegeekapps.easyassessment.model.ACriteria;
import com.thepegeekapps.easyassessment.model.AStudent;
import com.thepegeekapps.easyassessment.model.Assessment;
import com.thepegeekapps.easyassessment.model.Group;
import com.thepegeekapps.easyassessment.model.Rubric;
import com.thepegeekapps.easyassessment.util.Utils;

public class AssessmentScreen extends BaseScreen implements OnClickListener {
	
	public static final int ADD_NOTES_REQUEST_CODE = 0;
	
	protected TextView groupName;
	protected TextView rubricName;
	protected ViewFlipper flipper;
	protected Button previousBtn;
	protected Button groupListBtn;
	protected Button nextBtn;
	protected Button notesBtn;
	protected Button finishBtn;
	
	protected int assessmentId;
	protected Assessment assessment;
	protected List<AStudent> aStudents;
	protected int currentStudentPosition;
	protected AStudent currentStudent;
	protected Group group;
	protected Rubric rubric;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.assessment_screen);
		getIntentData();
		initializeViews();
		setCurrentStudent(0);
	}
	
	protected void getIntentData() {
		Intent intent = getIntent();
		if (intent != null && intent.hasExtra(DatabaseHelper.FIELD_ASSESSMENT_ID)) {
			assessmentId = intent.getIntExtra(DatabaseHelper.FIELD_ASSESSMENT_ID, 0);
			aStudents = dbManager.getAStudentsByAssessmentId(assessmentId);
			assessment = dbManager.getAssessmentById(assessmentId);
			if (assessment != null) {
				group = dbManager.getGroupById(assessment.getGroupId());
				rubric = dbManager.getRubricById(assessment.getRubricId());
			} 
		}
	}
	
	protected void initializeViews() {
		groupName = (TextView) findViewById(R.id.groupName);
		if (group != null)
			groupName.setText(group.getName());
		
		rubricName = (TextView) findViewById(R.id.rubricName);
		if (rubric != null)
			rubricName.setText(rubric.getName());
		
		flipper = (ViewFlipper) findViewById(R.id.flipper);
		populateViewFlipper();
		
		notesBtn = (Button) findViewById(R.id.notesBtn);
		notesBtn.setOnClickListener(this);
		
		finishBtn = (Button) findViewById(R.id.finishBtn);
		finishBtn.setOnClickListener(this);
		
		previousBtn = (Button) findViewById(R.id.backBtn);
		previousBtn.setText(R.string.previous);
		previousBtn.setVisibility(View.VISIBLE);
		previousBtn.setOnClickListener(this);
		
		groupListBtn = (Button) findViewById(R.id.addNewBtn);
		groupListBtn.setText(R.string.group_list);
		groupListBtn.setVisibility(View.VISIBLE);
		groupListBtn.setOnClickListener(this);
		
		nextBtn = (Button) findViewById(R.id.nextBtn);
		nextBtn.setText(R.string.next);
		nextBtn.setVisibility(View.VISIBLE);
		nextBtn.setOnClickListener(this);
		
		View titlebar = findViewById(R.id.titlebar);
		titlebar.setOnTouchListener(new OnSwipeTouchListener() {
			@Override
			public void onSwipeRight() {
				Intent intent = new Intent(AssessmentScreen.this, MainScreen.class);
				intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
				startActivity(intent);
				overridePendingTransition(R.anim.view_transition_in_right, R.anim.view_transition_out_right);
			}
		});
	}
	
	protected void populateViewFlipper() {
		if (aStudents == null || aStudents.isEmpty())
			return;
		for (AStudent aStudent : aStudents) {
			LinearLayout aStudentLayout = new LinearLayout(this);
			aStudentLayout.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT));
			aStudentLayout.setOrientation(LinearLayout.VERTICAL);
			
			List<ACriteria> aCriterias = aStudent.getACriterias();
			if (aCriterias != null && !aCriterias.isEmpty()) {
				for (ACriteria aCriteria : aCriterias) {
					LinearLayout aCriteriaLayout = getACriteriaLayout(aCriteria);
					aStudentLayout.addView(aCriteriaLayout);
				}
			}

			flipper.addView(aStudentLayout);
		}
	}
	
	protected LinearLayout getACriteriaLayout(final ACriteria aCriteria) {
		LayoutInflater inflater = (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);
		LinearLayout aCriteriaLayout = (LinearLayout) inflater.inflate(R.layout.a_criteria_layout, null);
		
		TextView name = (TextView) aCriteriaLayout.findViewById(R.id.aCriteriaName);
		name.setText(aCriteria.getName());
		
		final TextView value = (TextView) aCriteriaLayout.findViewById(R.id.aCriteriaValue);
		value.setText(String.valueOf(aCriteria.getValue()));
		
		SeekBar seekBar = (SeekBar) aCriteriaLayout.findViewById(R.id.aCriteriaSeekBar);
		seekBar.setMax(aCriteria.getEndScale() - aCriteria.getStartScale());
		seekBar.setProgress(aCriteria.getValue() - aCriteria.getStartScale());
		seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
			@Override
			public void onStopTrackingTouch(SeekBar seekBar) {
				aCriteria.setValue(aCriteria.getStartScale() + seekBar.getProgress());
			}
			@Override
			public void onStartTrackingTouch(SeekBar seekBar) {}
			@Override
			public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
				value.setText(String.valueOf(aCriteria.getStartScale() + progress));
			}
		});

		return aCriteriaLayout;
	}
	
	protected void previousStudent() {
		if (currentStudentPosition == 0)
			return;
		currentStudentPosition--;
		flipper.setInAnimation(this, R.anim.view_transition_in_right);
		flipper.setOutAnimation(this, R.anim.view_transition_out_right);
		setCurrentStudent(currentStudentPosition);
	}
	
	protected void nextStudent() {
		if (currentStudentPosition == aStudents.size()-1)
			return;
		currentStudentPosition++;
		flipper.setInAnimation(this, R.anim.view_transition_in_left);
		flipper.setOutAnimation(this, R.anim.view_transition_out_left);
		setCurrentStudent(currentStudentPosition);
	}
	
	protected void setCurrentStudent(int position) {
		if (aStudents == null || aStudents.isEmpty())
			return;
		if (position < 0 || position >= aStudents.size())
			return;
		currentStudentPosition = position;
		currentStudent = aStudents.get(position);
		
		setTitle(currentStudent.getName());
		
		flipper.setDisplayedChild(currentStudentPosition);
		
		previousBtn.setVisibility(currentStudentPosition == 0 ? View.INVISIBLE : View.VISIBLE);
		nextBtn.setVisibility(currentStudentPosition == aStudents.size()-1 ? View.INVISIBLE : View.VISIBLE);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.notesBtn:
			Intent intent = new Intent(this, NotesScreen.class);
			if (currentStudent != null)
				intent.putExtra(DatabaseHelper.FIELD_ID, currentStudent.getId());
			startActivityForResult(intent, ADD_NOTES_REQUEST_CODE);
			break;
		case R.id.finishBtn:
			new FinishTask().execute((Void[]) null);
			break;
		case R.id.backBtn:
			previousStudent();
			break;
		case R.id.addNewBtn:
			showGroupListDialog();
			break;
		case R.id.nextBtn:
			nextStudent();
			break;
		}
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (requestCode == ADD_NOTES_REQUEST_CODE && resultCode == RESULT_OK) {
			if (currentStudent != null && data != null) {
				currentStudent.setNote(data.getStringExtra(DatabaseHelper.FIELD_NOTE));
				currentStudent.setImagePath(data.getStringExtra(DatabaseHelper.FIELD_IMAGE_PATH));
				currentStudent.setVideoPath(data.getStringExtra(DatabaseHelper.FIELD_VIDEO_PATH));
			}
		}
	}
	
	protected void showGroupListDialog() {
		CharSequence[] items = Utils.aStudentsAsStringArray(aStudents);
		
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setTitle(R.string.select_student)
			.setItems(items, new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int which) {
					if (which != currentStudentPosition) {
						if (which < currentStudentPosition) {
							flipper.setInAnimation(AssessmentScreen.this, R.anim.view_transition_in_right);
							flipper.setOutAnimation(AssessmentScreen.this, R.anim.view_transition_out_right);
						} else {
							flipper.setInAnimation(AssessmentScreen.this, R.anim.view_transition_in_left);
							flipper.setOutAnimation(AssessmentScreen.this, R.anim.view_transition_out_left);
						}
						setCurrentStudent(which);
					}
					dialog.dismiss();
				}
			})
			.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int which) {
					dialog.dismiss();
				}
			})
			.create()
			.show();
	}
	
	class FinishTask extends AsyncTask<Void, Void, Void> {
		
		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			showProgressDialog(getString(R.string.please_wait));
		}

		@Override
		protected Void doInBackground(Void... params) {
			dbManager.updateAStudents(aStudents, true);
			return null;
		}
		
		@Override
		protected void onPostExecute(Void result) {
			super.onPostExecute(result);
			hideProgressDialog();
			Intent intent = new Intent(AssessmentScreen.this, MainScreen.class);
			intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
			startActivity(intent);
			overridePendingTransition(R.anim.view_transition_in_right, R.anim.view_transition_out_right);
		}
		
	}

}
