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
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.thepegeekapps.easyassessment.R;
import com.thepegeekapps.easyassessment.adapter.CriteriaAdapter;
import com.thepegeekapps.easyassessment.database.DatabaseHelper;
import com.thepegeekapps.easyassessment.listener.OnSwipeTouchListener;
import com.thepegeekapps.easyassessment.model.Criteria;
import com.thepegeekapps.easyassessment.model.Rubric;

public class CriteriasScreen extends BaseScreen implements OnClickListener {
	
	protected ListView criteriasList;
	protected TextView emptyView;
	protected Button backBtn;
	protected Button addNewBtn;
	
	protected int rubricId;
	protected Rubric rubric;
	protected List<Criteria> criterias;
	protected CriteriaAdapter adapter;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.criterias_screen);
		getIntentData();
		setTitle((rubric != null) ? rubric.getName() + " " + getString(R.string.criterias) : getString(R.string.criterias));
		criterias = dbManager.getCriteriasByRubricId(rubricId);
		adapter = new CriteriaAdapter(this, criterias);
		initializeViews();
		checkEmptyList();
	}
	
	protected void getIntentData() {
		Intent intent = getIntent();
		if (intent != null && intent.hasExtra(DatabaseHelper.FIELD_RUBRIC_ID)) {
			rubricId = intent.getIntExtra(DatabaseHelper.FIELD_RUBRIC_ID, 0);
			rubric = dbManager.getRubricById(rubricId);
		}
	}
	
	protected void initializeViews() {
		emptyView = (TextView) findViewById(R.id.emptyView);
		
		criteriasList = (ListView) findViewById(R.id.criteriasList);
		criteriasList.setAdapter(adapter);
		registerForContextMenu(criteriasList);
		
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
				Intent intent = new Intent(CriteriasScreen.this, MainScreen.class);
				intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
				startActivity(intent);
				overridePendingTransition(R.anim.view_transition_in_right, R.anim.view_transition_out_right);
			}
		});
	}
	
	protected void refreshItems() {
		criterias = dbManager.getCriteriasByRubricId(rubricId);	
		adapter.setCriterias(criterias);
		adapter.notifyDataSetChanged();
		checkEmptyList();
	}
	
	protected void checkEmptyList() {
		boolean criteriasNotEmpty = (criterias != null && !criterias.isEmpty());
		criteriasList.setVisibility(criteriasNotEmpty ? View.VISIBLE : View.GONE);
		emptyView.setVisibility(criteriasNotEmpty ? View.GONE : View.VISIBLE);
	}
	
	@Override
	public void onCreateContextMenu(ContextMenu menu, View v, ContextMenuInfo menuInfo) {
		if (v.getId() == R.id.criteriasList) {
			AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) menuInfo;
			Criteria criteria = adapter.getItem(info.position);
			menu.setHeaderTitle(criteria.getName());
			
			String[] menuItems = new String[] {getString(R.string.edit), getString(R.string.delete)};
			if (menuItems != null)
				for (int i = 0; i < menuItems.length; i++)
					menu.add(Menu.NONE, i, i, menuItems[i]);
		}
	}
	
	@Override
	public boolean onContextItemSelected(MenuItem menuItem) {
		AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) menuItem.getMenuInfo();
		Criteria criteria = adapter.getItem(info.position);
		switch (menuItem.getItemId()) {
		case 0:
			showEditCriteriaDialog(criteria);
			break;
		case 1:
			showConfirmDeleteCriteriaDialog(criteria);
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
						showEditCriteriaDialog(null);
						break;
					}
				}
			})
			.create()
			.show();
	}
		
	protected void showEditCriteriaDialog(final Criteria criteria) {
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		
		LinearLayout ll = new LinearLayout(this);
		ll.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT));
		ll.setPadding(10, 10, 10, 10);
		
		final EditText input = new EditText(this);
		input.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT));
		input.setInputType(InputType.TYPE_CLASS_TEXT);
		input.setHint(R.string.criteria_name);
		if (criteria != null)
			input.setText(criteria.getName());
		
		ll.addView(input);
		
		builder
		.setTitle(R.string.enter_new_name)
			.setView(ll)
			.setPositiveButton(R.string.save, new DialogInterface.OnClickListener() {	
				public void onClick(DialogInterface dialog, int which) {
					String name = input.getText().toString().trim();
					processEditCriteriaName(criteria, name);
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
	
	protected void processEditCriteriaName(Criteria criteria, String name) {
		if (name != null && !name.equals("")) {
			StringTokenizer tokenizer = new StringTokenizer(name, ",");
			int size = tokenizer.countTokens();
			if (size > 1) {
				String currentName = tokenizer.nextToken().trim();
				if (criteria == null) {
					Criteria newCriteria = new Criteria(0, rubricId, currentName, rubric.getStartScale(), rubric.getEndScale());
					dbManager.addCriteria(newCriteria);
				} else {
					criteria.setName(currentName);
					dbManager.updateCriteria(criteria);
				}
				while (tokenizer.hasMoreTokens()) {
					String newName = tokenizer.nextToken().trim();
					Criteria newCriteria = new Criteria(0, rubricId, newName, rubric.getStartScale(), rubric.getEndScale());
					dbManager.addCriteria(newCriteria);
				}
			} else {
				if (criteria == null) {
					Criteria newCriteria = new Criteria(0, rubricId, name, rubric.getStartScale(), rubric.getEndScale());
					dbManager.addCriteria(newCriteria);
				} else {
					criteria.setName(name);
					dbManager.updateCriteria(criteria);
				}
			}
			refreshItems();
		} else {
			Toast.makeText(this, R.string.empty_name, Toast.LENGTH_SHORT).show();
		}
	}
	
	protected void showConfirmDeleteCriteriaDialog(final Criteria criteria) {
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setTitle(R.string.delete)
		.setIcon(android.R.drawable.ic_dialog_alert)
		.setMessage(R.string.confirm_delete_criteria)
		.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				dbManager.deleteCriteria(criteria);
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
	protected void processItems(String items) {
		super.processItems(items);
		processEditCriteriaName(null, items);
	}

}
