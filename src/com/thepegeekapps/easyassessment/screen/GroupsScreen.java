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
import com.thepegeekapps.easyassessment.adapter.GroupAdapter;
import com.thepegeekapps.easyassessment.database.DatabaseHelper;
import com.thepegeekapps.easyassessment.listener.OnSwipeTouchListener;
import com.thepegeekapps.easyassessment.model.Group;

public class GroupsScreen extends BaseScreen implements OnClickListener, OnItemClickListener {
	
	protected ListView groupsList;
	protected TextView emptyView;
	protected Button backBtn;
	protected Button addNewBtn;
	
	protected List<Group> groups;
	protected GroupAdapter adapter;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.groups_screen);
		setTitle(R.string.groups);
		
		groups = dbManager.getGroups();
		adapter = new GroupAdapter(this, groups);
		
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
		
		groupsList = (ListView) findViewById(R.id.groupsList);
		groupsList.setAdapter(adapter);
		groupsList.setOnItemClickListener(this);
		registerForContextMenu(groupsList);
		
		backBtn = (Button) findViewById(R.id.backBtn);
		backBtn.setVisibility(View.VISIBLE);
		backBtn.setOnClickListener(this);
		
		addNewBtn = (Button) findViewById(R.id.addNewBtn);
		addNewBtn.setVisibility(View.VISIBLE);
		addNewBtn.setOnClickListener(this);
		
		View titlebar = findViewById(R.id.titlebar);
		titlebar.setOnTouchListener(new OnSwipeTouchListener() {
			@Override
			public void onSwipeRight() {
				Intent intent = new Intent(GroupsScreen.this, MainScreen.class);
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
	
	@Override
	public void onCreateContextMenu(ContextMenu menu, View v, ContextMenuInfo menuInfo) {
		if (v.getId() == R.id.groupsList) {
			AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) menuInfo;
			Group group = adapter.getItem(info.position);
			menu.setHeaderTitle(group.getName());
			
			String[] menuItems = new String[] {getString(R.string.edit), getString(R.string.delete)};
			if (menuItems != null)
				for (int i = 0; i < menuItems.length; i++)
					menu.add(Menu.NONE, i, i, menuItems[i]);
		}
	}
	
	@Override
	public boolean onContextItemSelected(MenuItem menuItem) {
		AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) menuItem.getMenuInfo();
		Group group = adapter.getItem(info.position);
		switch (menuItem.getItemId()) {
		case 0:
			showEditGroupDialog(group);
			break;
		case 1:
			showConfirmDeleteGroupDialog(group);
			break;	
		}
		return true;
	}
	
	protected void checkEmptyList() {
		boolean groupsNotEmpty = (groups != null && !groups.isEmpty());
		groupsList.setVisibility(groupsNotEmpty ? View.VISIBLE : View.GONE);
		emptyView.setVisibility(groupsNotEmpty ? View.GONE : View.VISIBLE);
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
						showEditGroupDialog(null);
						break;
					}
				}
			})
			.create()
			.show();
	}
	
	protected void showEditGroupDialog(final Group group) {
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		
		LinearLayout ll = new LinearLayout(this);
		ll.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT));
		ll.setPadding(10, 10, 10, 10);
		
		final EditText input = new EditText(this);
		input.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT));
		input.setInputType(InputType.TYPE_CLASS_TEXT);
		input.setHint(R.string.group_name);
		if (group != null)
			input.setText(group.getName());
		
		ll.addView(input);
		
		builder
		.setTitle(R.string.enter_new_name)
			.setView(ll)
			.setPositiveButton(R.string.save, new DialogInterface.OnClickListener() {	
				public void onClick(DialogInterface dialog, int which) {
					String name = input.getText().toString().trim();
					processEditGroupName(group, name);
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
	
	protected void processEditGroupName(Group group, String name) {
		if (name != null && !name.equals("")) {
			StringTokenizer tokenizer = new StringTokenizer(name, ",");
			int size = tokenizer.countTokens();
			if (size > 1) {
				String currentName = tokenizer.nextToken().trim();
				if (group == null) {
					Group newGroup = new Group(0, currentName);
					dbManager.addGroup(newGroup);
				} else {
					group.setName(currentName);
					dbManager.updateGroup(group);
				}
				while (tokenizer.hasMoreTokens()) {
					String newName = tokenizer.nextToken().trim();
					Group newGroup = new Group(0, newName);
					dbManager.addGroup(newGroup);
				}
			} else {
				if (group == null) {
					Group newGroup = new Group(0, name);
					dbManager.addGroup(newGroup);
				} else {
					group.setName(name);
					dbManager.updateGroup(group);
				}
			}
			refreshItems();
		} else {
			Toast.makeText(this, R.string.empty_name, Toast.LENGTH_SHORT).show();
		}
	}
	
	protected void showConfirmDeleteGroupDialog(final Group group) {
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setTitle(R.string.delete)
		.setIcon(android.R.drawable.ic_dialog_alert)
		.setMessage(R.string.confirm_delete_group)
		.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				dbManager.deleteGroup(group);
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
		Group group = groups.get(position);
		Intent intent = new Intent(this, StudentsScreen.class);
		intent.putExtra(DatabaseHelper.FIELD_GROUP_ID, group.getId());
		startActivity(intent);
	}
	
	@Override
	protected void processItems(String items) {
		processEditGroupName(null, items);
	}

}
