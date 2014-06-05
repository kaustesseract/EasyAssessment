package com.thepegeekapps.easyassessment.screen;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

import com.thepegeekapps.easyassessment.R;
import com.thepegeekapps.easyassessment.listener.OnSwipeTouchListener;

public class SetupScreen extends BaseScreen implements OnClickListener {
	
	protected Button groupsBtn;
	protected Button rubricsBtn;
	protected Button backBtn;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.setup_screen);
		setTitle(R.string.set_up);
		initializeViews();
	}
	
	protected void initializeViews() {
		groupsBtn = (Button) findViewById(R.id.groupsBtn);
		groupsBtn.setOnClickListener(this);
		
		rubricsBtn = (Button) findViewById(R.id.rubricsBtn);
		rubricsBtn.setOnClickListener(this);
		
		backBtn = (Button) findViewById(R.id.backBtn);
		backBtn.setVisibility(View.VISIBLE);
		backBtn.setOnClickListener(this);
		
		View titlebar = findViewById(R.id.titlebar);
		titlebar.setOnTouchListener(new OnSwipeTouchListener() {
			@Override
			public void onSwipeRight() {
				Intent intent = new Intent(SetupScreen.this, MainScreen.class);
				intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
				startActivity(intent);
				overridePendingTransition(R.anim.view_transition_in_right, R.anim.view_transition_out_right);
			}
		});
	}
	
	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.groupsBtn:
			Intent intent = new Intent(this, GroupsScreen.class);
			startActivity(intent);
			break;
		case R.id.rubricsBtn:
			intent = new Intent(this, RubricsScreen.class);
			startActivity(intent);
			break;
		case R.id.backBtn:
			onBackPressed();
			break;
		}
	}

}
