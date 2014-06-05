package com.thepegeekapps.easyassessment.adapter;

import java.util.LinkedList;
import java.util.List;

import android.app.Service;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.thepegeekapps.easyassessment.R;
import com.thepegeekapps.easyassessment.model.Student;

public class SelectStudentAdapter extends BaseAdapter {
	
	protected Context context;
	protected List<Student> students;
	
	public SelectStudentAdapter(Context context, List<Student> students) {
		this.context = context;
		setStudents(students);
	}

	@Override
	public int getCount() {
		return (students != null) ? students.size() : 0;
	}

	@Override
	public Student getItem(int position) {
		return (students != null && !students.isEmpty()) ? students.get(position) : null;
	}

	@Override
	public long getItemId(int position) {
		return (students != null && !students.isEmpty()) ? students.get(position).getId() : 0;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder holder = null;
		if (convertView == null) {
			LayoutInflater inflater = (LayoutInflater) context.getSystemService(Service.LAYOUT_INFLATER_SERVICE);
			convertView = inflater.inflate(R.layout.select_students_list_item, null);
			holder = new ViewHolder();
			holder.name = (TextView) convertView.findViewById(R.id.name);
			holder.checked = (ImageView) convertView.findViewById(R.id.checked);
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}
		
		Student student = students.get(position);

		holder.name.setText(student.getName());
			
		int visibility = student.isChecked() ? View.VISIBLE : View.INVISIBLE;
		holder.checked.setVisibility(visibility);

		return convertView;
	}
	
	public void setStudents(List<Student> students) {
		this.students = (students != null) ? students : new LinkedList<Student>();
	}
	
	public int getCheckedCount() {
		int count = 0;
		for (int i=0; i<students.size(); i++)
			if (students.get(i).isChecked())
				count++;
		return count;
	}
	
	public void toggleSelectAll() {
		int checkedCount = getCheckedCount();
		if (checkedCount > 0)
			unselectAll();
		else
			selectAll();
	}
	
	protected void selectAll() {
		for (int i=0; i<students.size(); i++)
			students.get(i).setChecked(true);
		notifyDataSetChanged();
	}
	
	protected void unselectAll() {
		for (int i=0; i<students.size(); i++)
			students.get(i).setChecked(false);
		notifyDataSetChanged();
	}
	
	public List<Student> getCheckedStudents() {
		List<Student> checkedStudents = new LinkedList<Student>();
		for (int i=0; i<students.size(); i++)
			if (students.get(i).isChecked())
				checkedStudents.add(students.get(i));
		return checkedStudents;
	}
	
	class ViewHolder {
		TextView name;
		ImageView checked;
	}

}
