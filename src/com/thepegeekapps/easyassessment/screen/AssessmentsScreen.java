package com.thepegeekapps.easyassessment.screen;

import java.util.List;
import java.util.StringTokenizer;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.InputType;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.thepegeekapps.easyassessment.R;
import com.thepegeekapps.easyassessment.adapter.AssessmentAdapter;
import com.thepegeekapps.easyassessment.database.DatabaseHelper;
import com.thepegeekapps.easyassessment.listener.OnSwipeTouchListener;
import com.thepegeekapps.easyassessment.model.Assessment;
import com.thepegeekapps.easyassessment.model.Group;

public class AssessmentsScreen extends BaseScreen implements OnClickListener, OnItemClickListener {
	
	protected ListView assessmentsList;
	protected TextView emptyView;
	protected Button backBtn;
	protected Button addNewBtn;
	
	protected int groupId;
	protected Group group;
	protected List<Assessment> assessments;
	protected AssessmentAdapter adapter;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.assessments_screen);
		getIntentData();
		setTitle((group != null) ? group.getName() + " " + getString(R.string.assessments) : getString(R.string.assessments));
		assessments = dbManager.getAssessmentsByGroupId(groupId);
		adapter = new AssessmentAdapter(this, assessments);
		initializeViews();
		checkEmptyList();
	}
	
	@Override
	protected void onResume() {
		super.onResume();
		refreshItems();
	}
	
	protected void getIntentData() {
		Intent intent = getIntent();
		if (intent != null && intent.hasExtra(DatabaseHelper.FIELD_GROUP_ID)) {
			groupId = intent.getIntExtra(DatabaseHelper.FIELD_GROUP_ID, 0);
			group = dbManager.getGroupById(groupId);
		}
	}
	
	protected void initializeViews() {
		emptyView = (TextView) findViewById(R.id.emptyView);
		
		assessmentsList = (ListView) findViewById(R.id.assessmentsList);
		assessmentsList.setAdapter(adapter);
		assessmentsList.setOnItemClickListener(this);
		registerForContextMenu(assessmentsList);
		
		addNewBtn = (Button) findViewById(R.id.addNewBtn);
		addNewBtn.setVisibility(View.VISIBLE);
		addNewBtn.setOnClickListener(this);
		
		backBtn = (Button) findViewById(R.id.backBtn);
		backBtn.setVisibility(View.VISIBLE);
		backBtn.setOnClickListener(this);
		
		View titlebar = findViewById(R.id.titlebar);
		titlebar.setOnTouchListener(new OnSwipeTouchListener() {
			@Override
			public void onSwipeRight() {
				Intent intent = new Intent(AssessmentsScreen.this, MainScreen.class);
				intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
				startActivity(intent);
				overridePendingTransition(R.anim.view_transition_in_right, R.anim.view_transition_out_right);
			}
		});
	}
	
	protected void refreshItems() {
		assessments = dbManager.getAssessmentsByGroupId(groupId);		
		adapter.setAssessments(assessments);
		adapter.notifyDataSetChanged();
		checkEmptyList();
	}
	
	protected void checkEmptyList() {
		boolean assessmentsNotEmpty = (assessments != null && !assessments.isEmpty());
		assessmentsList.setVisibility(assessmentsNotEmpty ? View.VISIBLE : View.GONE);
		emptyView.setVisibility(assessmentsNotEmpty ? View.GONE : View.VISIBLE);
	}
	
	@Override
	public void onCreateContextMenu(ContextMenu menu, View v, ContextMenuInfo menuInfo) {
		if (v.getId() == R.id.assessmentsList) {
			AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) menuInfo;
			Assessment assessment = adapter.getItem(info.position);
			menu.setHeaderTitle(assessment.getName());
			
			String[] menuItems = new String[] {getString(R.string.edit), getString(R.string.delete)};
			if (menuItems != null)
				for (int i = 0; i < menuItems.length; i++)
					menu.add(Menu.NONE, i, i, menuItems[i]);
		}
	}
	
	@Override
	public boolean onContextItemSelected(MenuItem menuItem) {
		AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) menuItem.getMenuInfo();
		Assessment assessment = adapter.getItem(info.position);
		switch (menuItem.getItemId()) {
		case 0:
			showEditAssessmentDialog(assessment);
			break;
		case 1:
			showConfirmDeleteAssessmentDialog(assessment);
			break;	
		}
		return true;
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.addNewBtn:
			showSelectAddMethodDialog();
			break;
		case R.id.backBtn:
			onBackPressed();
			break;
		}
	}
	
	protected void showSelectAddMethodDialog() {
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setTitle(R.string.select_add_method)
			.setItems(R.array.add_method, new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int which) {
					switch (which) {
					case 0:
						addFromCsv();
						break;
					case 1:
						showEditAssessmentDialog(null);
						break;
					}
				}
			})
			.create()
			.show();
	}
	
	protected void showEditAssessmentDialog(final Assessment assessment) {
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		
		LinearLayout ll = new LinearLayout(this);
		ll.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT));
		ll.setPadding(10, 10, 10, 10);
		
		final EditText input = new EditText(this);
		input.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT));
		input.setInputType(InputType.TYPE_CLASS_TEXT);
		input.setHint(R.string.assessment_name);
		if (assessment != null)
			input.setText(assessment.getName());
		
		ll.addView(input);
		
		builder
		.setTitle(R.string.enter_new_name)
			.setView(ll)
			.setPositiveButton(R.string.save, new DialogInterface.OnClickListener() {	
				public void onClick(DialogInterface dialog, int which) {
					String name = input.getText().toString().trim();
					processEditAssessmentName(assessment, name);
					dialog.dismiss();
				}
			})
			.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int which) {
					dialog.dismiss();
				}
			})
			.create()
			.show();
	}
	
	protected void processEditAssessmentName(Assessment assessment, String name) {
		if (name != null && !name.equals("")) {
			StringTokenizer tokenizer = new StringTokenizer(name, ",");
			int size = tokenizer.countTokens();
			if (size > 1) {
				String currentName = tokenizer.nextToken().trim();
				if (assessment == null) {
					Assessment newAssessment = new Assessment(0, groupId, group.getName(), 0, null, currentName, getString(R.string.assessment_not_taken));
					dbManager.addAssessment(newAssessment);
				} else {
					assessment.setName(currentName);
					dbManager.updateAssessment(assessment);
				}
				while (tokenizer.hasMoreTokens()) {
					String newName = tokenizer.nextToken().trim();
					Assessment newAssessment = new Assessment(0, groupId, group.getName(), 0, null, newName, getString(R.string.assessment_not_taken));
					dbManager.addAssessment(newAssessment);
				}
			} else {
				if (assessment == null) {
					Assessment newAssessment = new Assessment(0, groupId, group.getName(), 0, null, name, getString(R.string.assessment_not_taken));
					dbManager.addAssessment(newAssessment);
				} else {
					assessment.setName(name);
					dbManager.updateAssessment(assessment);
				}
			}
			refreshItems();
		} else {
			Toast.makeText(this, R.string.empty_name, Toast.LENGTH_SHORT).show();
		}
	}
	
	protected void showConfirmDeleteAssessmentDialog(final Assessment assessment) {
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setTitle(R.string.delete)
		.setIcon(android.R.drawable.ic_dialog_alert)
		.setMessage(R.string.confirm_delete_assessment)
		.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				dbManager.deleteAssessment(assessment);
				dialog.dismiss();
				refreshItems();
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

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
		Assessment assessment = assessments.get(position);
		if (assessment != null) {
			if (assessment.isTaken()) {
				Intent intent = new Intent(this, AssessmentScreen.class);
				intent.putExtra(DatabaseHelper.FIELD_ASSESSMENT_ID, assessment.getId());
				startActivity(intent);
			} else {
				if (!assessment.isStudentsSelected()) {
					Intent intent = new Intent(this, SelectStudentsScreen.class);
					intent.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
					intent.putExtra(DatabaseHelper.FIELD_ASSESSMENT_ID, assessment.getId());
					startActivity(intent);
				} else if (!assessment.isRubricSelected()) {
					Intent intent = new Intent(this, SelectRubricScreen.class);
					intent.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
					intent.putExtra(DatabaseHelper.FIELD_ASSESSMENT_ID, assessment.getId());
					startActivity(intent);
				}
			}
		}
		
	}
	
	@Override
	protected void processItems(String items) {
		super.processItems(items);
		processEditAssessmentName(null, items);
	}

}
