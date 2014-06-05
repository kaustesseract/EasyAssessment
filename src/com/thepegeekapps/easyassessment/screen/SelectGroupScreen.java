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
import com.thepegeekapps.easyassessment.adapter.SelectGroupAdapter;
import com.thepegeekapps.easyassessment.database.DatabaseHelper;
import com.thepegeekapps.easyassessment.listener.OnSwipeTouchListener;
import com.thepegeekapps.easyassessment.model.Group;

public class SelectGroupScreen extends BaseScreen implements OnItemClickListener, OnClickListener {
	
	public static final int START_ASSESSMENTS_SCREEN = 0;
	public static final int START_SELECT_ASSESSMENTS_SCREEN = 1;
	
	protected ListView groupsList;
	protected TextView emptyView;
	protected Button backBtn;
	
	protected List<Group> groups;
	protected SelectGroupAdapter adapter;
	
	protected int screen;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.select_group_screen);
		setTitle(R.string.select_group);
		getIntentData();
		
		groups = dbManager.getGroups();
		adapter = new SelectGroupAdapter(this, groups);
		
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
		if (intent != null && intent.hasExtra("screen"))
			screen = intent.getIntExtra("screen", 0);
	}
	
	protected void initializeViews() {
		emptyView = (TextView) findViewById(R.id.emptyView);
		
		groupsList = (ListView) findViewById(R.id.groupsList);
		groupsList.setAdapter(adapter);
		groupsList.setOnItemClickListener(this);
		
		backBtn = (Button) findViewById(R.id.backBtn);
		backBtn.setVisibility(View.VISIBLE);
		backBtn.setOnClickListener(this);
		
		View titlebar = findViewById(R.id.titlebar);
		titlebar.setOnTouchListener(new OnSwipeTouchListener() {
			@Override
			public void onSwipeRight() {
				Intent intent = new Intent(SelectGroupScreen.this, MainScreen.class);
				intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
				startActivity(intent);
				overridePendingTransition(R.anim.view_transition_in_right, R.anim.view_transition_out_right);
			}
		});
	}
	
	protected void refreshItems() {
		groups = dbManager.getGroups();		
		adapter.setGroups(groups);
		adapter.notifyDataSetChanged();
		checkEmptyList();
	}
	
	protected void checkEmptyList() {
		boolean groupsNotEmpty = (groups != null && !groups.isEmpty());
		groupsList.setVisibility(groupsNotEmpty ? View.VISIBLE : View.GONE);
		emptyView.setVisibility(groupsNotEmpty ? View.GONE : View.VISIBLE);
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
		Group group = groups.get(position);
		Class<? extends BaseScreen> clas = (screen == START_ASSESSMENTS_SCREEN) ? AssessmentsScreen.class : SelectAssessmentsScreen.class;
		Intent intent = new Intent(this, clas);
		intent.putExtra(DatabaseHelper.FIELD_GROUP_ID, group.getId());
		startActivity(intent);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.backBtn:
			onBackPressed();
			break;
		}
	}

}
