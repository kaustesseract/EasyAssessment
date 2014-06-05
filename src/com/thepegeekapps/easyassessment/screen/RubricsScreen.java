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
import com.thepegeekapps.easyassessment.adapter.RubricAdapter;
import com.thepegeekapps.easyassessment.database.DatabaseHelper;
import com.thepegeekapps.easyassessment.listener.OnSwipeTouchListener;
import com.thepegeekapps.easyassessment.model.Rubric;

public class RubricsScreen extends BaseScreen implements OnClickListener, OnItemClickListener {
	
	protected ListView rubricsList;
	protected TextView emptyView;
	protected Button backBtn;
	protected Button addNewBtn;
	
	protected List<Rubric> rubrics;
	protected RubricAdapter adapter;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.rubrics_screen);
		setTitle(R.string.rubrics);
		
		rubrics = dbManager.getRubrics();
		adapter = new RubricAdapter(this, rubrics);
		
		initializeViews();
		checkEmptyList();
	}
	
	@Override
	protected void onResume() {
		super.onResume();
		refreshItems();
	}
	
	protected void initializeViews() {
		emptyView = (TextView) findViewById(R.id.emptyView);
		
		rubricsList = (ListView) findViewById(R.id.rubricsList);
		rubricsList.setAdapter(adapter);
		rubricsList.setOnItemClickListener(this);
		registerForContextMenu(rubricsList);
		
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
				Intent intent = new Intent(RubricsScreen.this, MainScreen.class);
				intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
				startActivity(intent);
				overridePendingTransition(R.anim.view_transition_in_right, R.anim.view_transition_out_right);
			}
		});
	}
	
	protected void refreshItems() {
		rubrics = dbManager.getRubrics();		
		adapter.setRubrics(rubrics);
		adapter.notifyDataSetChanged();
		checkEmptyList();
	}
	
	@Override
	public void onCreateContextMenu(ContextMenu menu, View v, ContextMenuInfo menuInfo) {
		if (v.getId() == R.id.rubricsList) {
			AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) menuInfo;
			Rubric rubric = adapter.getItem(info.position);
			menu.setHeaderTitle(rubric.getName());
			
			String[] menuItems = new String[] {getString(R.string.edit), getString(R.string.delete)};
			if (menuItems != null)
				for (int i = 0; i < menuItems.length; i++)
					menu.add(Menu.NONE, i, i, menuItems[i]);
		}
	}
	
	@Override
	public boolean onContextItemSelected(MenuItem menuItem) {
		AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) menuItem.getMenuInfo();
		Rubric rubric = adapter.getItem(info.position);
		switch (menuItem.getItemId()) {
		case 0:
			showEditRubricDialog(rubric);
			break;
		case 1:
			showConfirmDeleteRubricDialog(rubric);
			break;	
		}
		return true;
	}
	
	protected void checkEmptyList() {
		boolean rubricsNotEmpty = (rubrics != null && !rubrics.isEmpty());
		rubricsList.setVisibility(rubricsNotEmpty ? View.VISIBLE : View.GONE);
		emptyView.setVisibility(rubricsNotEmpty ? View.GONE : View.VISIBLE);
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
						showEditRubricDialog(null);
						break;
					}
				}
			})
			.create()
			.show();
	}
	
	protected void showEditRubricDialog(final Rubric rubric) {
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		
		LinearLayout ll = new LinearLayout(this);
		ll.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT));
		ll.setPadding(10, 10, 10, 10);
		
		final EditText input = new EditText(this);
		input.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT));
		input.setInputType(InputType.TYPE_CLASS_TEXT);
		input.setHint(R.string.rubric_name);
		if (rubric != null)
			input.setText(rubric.getName());
		
		ll.addView(input);
		
		builder
		.setTitle(R.string.enter_new_name)
			.setView(ll)
			.setPositiveButton(R.string.save, new DialogInterface.OnClickListener() {	
				public void onClick(DialogInterface dialog, int which) {
					String name = input.getText().toString().trim();
					processEditRubricName(rubric, name);
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
	
	protected void processEditRubricName(Rubric rubric, String name) {
		if (name != null && !name.equals("")) {
			StringTokenizer tokenizer = new StringTokenizer(name, ",");
			int size = tokenizer.countTokens();
			if (size > 1) {
				String currentName = tokenizer.nextToken().trim();
				if (rubric == null) {
					Rubric newRubric = new Rubric(0, currentName);
					dbManager.addRubric(newRubric);
				} else {
					rubric.setName(currentName);
					dbManager.updateRubric(rubric);
				}
				while (tokenizer.hasMoreTokens()) {
					String newName = tokenizer.nextToken().trim();
					Rubric newRubric = new Rubric(0, newName);
					dbManager.addRubric(newRubric);
				}
			} else {
				if (rubric == null) {
					Rubric newRubric = new Rubric(0, name);
					dbManager.addRubric(newRubric);
				} else {
					rubric.setName(name);
					dbManager.updateRubric(rubric);
				}
			}
			refreshItems();
		} else {
			Toast.makeText(this, R.string.empty_name, Toast.LENGTH_SHORT).show();
		}
	}
	
	protected void showConfirmDeleteRubricDialog(final Rubric rubric) {
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setTitle(R.string.delete)
		.setIcon(android.R.drawable.ic_dialog_alert)
		.setMessage(R.string.confirm_delete_rubric)
		.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				dbManager.deleteRubric(rubric);
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
		Rubric rubric = rubrics.get(position);
		Intent intent = new Intent(this, CriteriasScreen.class);
		intent.putExtra(DatabaseHelper.FIELD_RUBRIC_ID, rubric.getId());
		startActivity(intent);
	}
	
	@Override
	protected void processItems(String items) {
		super.processItems(items);
		processEditRubricName(null, items);
	}

}
