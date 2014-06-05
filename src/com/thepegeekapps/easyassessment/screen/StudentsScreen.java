package com.thepegeekapps.easyassessment.screen;

import java.util.List;
import java.util.StringTokenizer;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.InputType;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ContextMenu.ContextMenuInfo;
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
import com.thepegeekapps.easyassessment.adapter.StudentsAdapter;
import com.thepegeekapps.easyassessment.database.DatabaseHelper;
import com.thepegeekapps.easyassessment.listener.OnSwipeTouchListener;
import com.thepegeekapps.easyassessment.model.Group;
import com.thepegeekapps.easyassessment.model.Student;

public class StudentsScreen extends BaseScreen implements OnClickListener, OnItemClickListener {
	
	protected ListView studentsList;
	protected TextView emptyView;
	protected Button backBtn;
	protected Button addNewBtn;
	
	protected int groupId;
	protected Group group;
	protected List<Student> students;
	protected StudentsAdapter adapter;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.students_screen);
		getIntentData();
		setTitle((group != null) ? group.getName() + " " + getString(R.string.students) : getString(R.string.students));
		students = dbManager.getStudentsByGroupId(groupId);
		adapter = new StudentsAdapter(this, students);
		initializeViews();
		checkEmptyList();
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
		
		studentsList = (ListView) findViewById(R.id.studentsList);
		studentsList.setAdapter(adapter);
		studentsList.setOnItemClickListener(this);
		registerForContextMenu(studentsList);
		
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
				Intent intent = new Intent(StudentsScreen.this, MainScreen.class);
				intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
				startActivity(intent);
				overridePendingTransition(R.anim.view_transition_in_right, R.anim.view_transition_out_right);
			}
		});
	}
	
	protected void refreshItems() {
		students = dbManager.getStudentsByGroupId(groupId);		
		adapter.setStudents(students);
		adapter.notifyDataSetChanged();
		checkEmptyList();
	}
	
	protected void checkEmptyList() {
		boolean studentsNotEmpty = (students != null && !students.isEmpty());
		studentsList.setVisibility(studentsNotEmpty ? View.VISIBLE : View.GONE);
		emptyView.setVisibility(studentsNotEmpty ? View.GONE : View.VISIBLE);
	}
	
	@Override
	public void onCreateContextMenu(ContextMenu menu, View v, ContextMenuInfo menuInfo) {
		if (v.getId() == R.id.studentsList) {
			AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) menuInfo;
			Student student = adapter.getItem(info.position);
			menu.setHeaderTitle(student.getName());
			
			String[] menuItems = new String[] {getString(R.string.edit), getString(R.string.delete)};
			if (menuItems != null)
				for (int i = 0; i < menuItems.length; i++)
					menu.add(Menu.NONE, i, i, menuItems[i]);
		}
	}
	
	@Override
	public boolean onContextItemSelected(MenuItem menuItem) {
		AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) menuItem.getMenuInfo();
		Student student = adapter.getItem(info.position);
		switch (menuItem.getItemId()) {
		case 0:
			showEditStudentDialog(student);
			break;
		case 1:
			showConfirmDeleteStudentDialog(student);
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
						showEditStudentDialog(null);
						break;
					}
				}
			})
			.create()
			.show();
	}
	
	protected void showEditStudentDialog(final Student student) {
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		
		LinearLayout ll = new LinearLayout(this);
		ll.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT));
		ll.setPadding(10, 10, 10, 10);
		
		final EditText input = new EditText(this);
		input.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT));
		input.setInputType(InputType.TYPE_CLASS_TEXT);
		input.setHint(R.string.student_name);
		if (student != null)
			input.setText(student.getName());
		
		ll.addView(input);
		
		builder
		.setTitle(R.string.enter_new_name)
			.setView(ll)
			.setPositiveButton(R.string.save, new DialogInterface.OnClickListener() {	
				public void onClick(DialogInterface dialog, int which) {
					String name = input.getText().toString().trim();
					processEditStudentName(student, name);
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
	
	protected void processEditStudentName(Student student, String name) {
		if (name != null && !name.equals("")) {
			StringTokenizer tokenizer = new StringTokenizer(name, ",");
			int size = tokenizer.countTokens();
			if (size > 1) {
				String currentName = tokenizer.nextToken().trim();
				if (student == null) {
					Student newStudent = new Student(0, groupId, currentName);
					dbManager.addStudent(newStudent);
				} else {
					student.setName(currentName);
					dbManager.updateStudent(student);
				}
				while (tokenizer.hasMoreTokens()) {
					String newName = tokenizer.nextToken().trim();
					Student newStudent = new Student(0, groupId, newName);
					dbManager.addStudent(newStudent);
				}
			} else {
				if (student == null) {
					Student newStudent = new Student(0, groupId, name);
					dbManager.addStudent(newStudent);
				} else {
					student.setName(name);
					dbManager.updateStudent(student);
				}
			}
			refreshItems();
		} else {
			Toast.makeText(this, R.string.empty_name, Toast.LENGTH_SHORT).show();
		}
	}
	
	protected void showConfirmDeleteStudentDialog(final Student student) {
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setTitle(R.string.delete)
		.setIcon(android.R.drawable.ic_dialog_alert)
		.setMessage(R.string.confirm_delete_student)
		.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				dbManager.deleteStudent(student);
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
		if (students != null && !students.isEmpty()) {
			Student student = students.get(position);
			if (student != null) {
				Intent intent = new Intent(this, StudentAssessmentsScreen.class);
				intent.putExtra(DatabaseHelper.FIELD_STUDENT_ID, student.getId());
				startActivity(intent);
			}
		}
	}
	
	@Override
	protected void processItems(String items) {
		processEditStudentName(null, items);
	}

}
