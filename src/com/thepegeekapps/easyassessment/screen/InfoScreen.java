package com.thepegeekapps.easyassessment.screen;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

import com.thepegeekapps.easyassessment.R;
import com.thepegeekapps.easyassessment.listener.OnSwipeTouchListener;

public class InfoScreen extends BaseScreen implements OnClickListener {

	protected View contactUs;
	protected View visitWebsite;
//	protected View rateApp;
	protected View reportBug;
	protected View tellFriend;
//	protected View watchVideo;
	
	protected View peGeek;
	protected View fitnessTests;
	protected View peGames;
	protected View sportsRules;
	protected View swimGames;
	
	protected Button backBtn;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.info_screen);
		setTitle(R.string.help_and_support);
		initializeViews();
	}
	
	protected void initializeViews() {
		contactUs = findViewById(R.id.contactUs);
		contactUs.setOnClickListener(this);
		visitWebsite = findViewById(R.id.visitWebsite);
		visitWebsite.setOnClickListener(this);
//		rateApp = findViewById(R.id.rateApp);
//		rateApp.setOnClickListener(this);
		reportBug = findViewById(R.id.reportBug);
		reportBug.setOnClickListener(this);
		tellFriend = findViewById(R.id.tellFriend);
		tellFriend.setOnClickListener(this);
//		watchVideo = findViewById(R.id.watchVideo);
//		watchVideo.setOnClickListener(this);
		
		peGeek = findViewById(R.id.peGeek);
		peGeek.setOnClickListener(this);
		fitnessTests = findViewById(R.id.fitnessTests);
		fitnessTests.setOnClickListener(this);
		peGames = findViewById(R.id.peGames);
		peGames.setOnClickListener(this);
		sportsRules = findViewById(R.id.sportsRules);
		sportsRules.setOnClickListener(this);
		swimGames = findViewById(R.id.swimGames);
		swimGames.setOnClickListener(this);
		
		backBtn = (Button) findViewById(R.id.backBtn);
		backBtn.setVisibility(View.VISIBLE);
		backBtn.setOnClickListener(this);
		
		View titlebar = findViewById(R.id.titlebar);
		titlebar.setOnTouchListener(new OnSwipeTouchListener() {
			@Override
			public void onSwipeRight() {
				Intent intent = new Intent(InfoScreen.this, MainScreen.class);
				intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
				startActivity(intent);
				overridePendingTransition(R.anim.view_transition_in_right, R.anim.view_transition_out_right);
			}
		});
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.contactUs:
			contactUs();
			break;
		case R.id.visitWebsite:
			visitWebsite();
			break;
		case R.id.reportBug:
			reportBug();
			break;
		case R.id.tellFriend:
			tellFriend();
			break;
		case R.id.peGeek:
			showApp(peGeek.getTag().toString());
			break;
		case R.id.fitnessTests:
			showApp(fitnessTests.getTag().toString());
			break;
		case R.id.peGames:
			showApp(peGames.getTag().toString());
			break;
		case R.id.sportsRules:
			showApp(sportsRules.getTag().toString());
			break;
		case R.id.swimGames:
			showApp(swimGames.getTag().toString());
			break;		
		case R.id.backBtn:
			onBackPressed();
			break;
		}
	}
	
	protected void contactUs() {
		Intent intent = new Intent(Intent.ACTION_SEND);
//		intent.setType("plain/text"); // in emulator
		intent.setType("message/rfc822"); // in real devices
		intent.putExtra(Intent.EXTRA_EMAIL, new String[] {"support@thepegeekapps.com"});
		startActivity(Intent.createChooser(intent, getString(R.string.email_app_select)));
	}
	
	protected void visitWebsite() {
		Intent intent = new Intent(Intent.ACTION_VIEW);
		intent.setData(Uri.parse("http://www.thepegeekapps.com"));
		startActivity(intent);
	}
	
	protected void reportBug() {
		String msgText = String.format(getString(R.string.report_bug_template), 
				android.os.Build.BRAND, 
				android.os.Build.MODEL,
				android.os.Build.VERSION.RELEASE);
	
		Intent intent = new Intent(Intent.ACTION_SEND);
		intent.setType("message/rfc822");
		intent.putExtra(Intent.EXTRA_EMAIL, new String[] {"support@thepegeekapps.com"});
		intent.putExtra(Intent.EXTRA_TEXT, msgText);
		startActivity(Intent.createChooser(intent, getString(R.string.email_app_select)));
	}
	
	protected void tellFriend() {
		Intent intent = new Intent(Intent.ACTION_SEND);
		intent.setType("message/rfc822");
		intent.putExtra(Intent.EXTRA_EMAIL, new String[] {"support@thepegeekapps.com"});
		intent.putExtra(Intent.EXTRA_TEXT, getString(R.string.tell_prewritten_message));
		startActivity(Intent.createChooser(intent, getString(R.string.email_app_select)));
	}
	
	protected void showApp(String url) {
		Intent intent = new Intent(Intent.ACTION_VIEW);
		intent.setData(Uri.parse(url));
		startActivity(intent);
	}
	
}
