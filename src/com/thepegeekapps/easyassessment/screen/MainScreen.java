package com.thepegeekapps.easyassessment.screen;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

import com.thepegeekapps.easyassessment.R;

public class MainScreen extends BaseScreen implements OnClickListener {
	
	protected Button setUpBtn;
	protected Button assessmentsBtn;
	protected Button sendResultsBtn;
	protected Button helpAndSupportBtn;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main_screen);
		setTitle(R.string.app_name);
		initializeViews();
	}
	
	protected void initializeViews() {
		setUpBtn = (Button) findViewById(R.id.setUpBtn);
		setUpBtn.setOnClickListener(this);
		
		assessmentsBtn = (Button) findViewById(R.id.assessmentsBtn);
		assessmentsBtn.setOnClickListener(this);
		
		sendResultsBtn = (Button) findViewById(R.id.sendResultsBtn);
		sendResultsBtn.setOnClickListener(this);
		
		helpAndSupportBtn = (Button) findViewById(R.id.helpAndSupportBtn);
		helpAndSupportBtn.setOnClickListener(this);
	}
	
	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.setUpBtn:
			Intent intent = new Intent(this, SetupScreen.class);
			startActivity(intent);
			break;
		case R.id.assessmentsBtn:
			intent = new Intent(this, SelectGroupScreen.class);
			intent.putExtra("screen", SelectGroupScreen.START_ASSESSMENTS_SCREEN);
			startActivity(intent);
			break;
		case R.id.sendResultsBtn:
			intent = new Intent(this, SelectGroupScreen.class);
			intent.putExtra("screen", SelectGroupScreen.START_SELECT_ASSESSMENTS_SCREEN);
			startActivity(intent);
			break;
		case R.id.helpAndSupportBtn:
			intent = new Intent(this, InfoScreen.class);
			startActivity(intent);
			break;
		}
	}

}
