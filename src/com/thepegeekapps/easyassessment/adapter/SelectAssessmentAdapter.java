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
import com.thepegeekapps.easyassessment.model.Assessment;

public class SelectAssessmentAdapter extends BaseAdapter {
	
	protected Context context;
	protected List<Assessment> assessments;
	
	public SelectAssessmentAdapter(Context context, List<Assessment> assessments) {
		this.context = context;
		setAssessments(assessments);
	}

	@Override
	public int getCount() {
		return (assessments != null) ? assessments.size() : 0;
	}

	@Override
	public Assessment getItem(int position) {
		return (assessments != null && !assessments.isEmpty()) ? assessments.get(position) : null;
	}

	@Override
	public long getItemId(int position) {
		return (assessments != null && !assessments.isEmpty()) ? assessments.get(position).getId() : 0;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder holder = null;
		if (convertView == null) {
			LayoutInflater inflater = (LayoutInflater) context.getSystemService(Service.LAYOUT_INFLATER_SERVICE);
			convertView = inflater.inflate(R.layout.select_assessment_list_item, null);
			holder = new ViewHolder();
			holder.name = (TextView) convertView.findViewById(R.id.name);
			holder.taken = (TextView) convertView.findViewById(R.id.taken);
			holder.checked = (ImageView) convertView.findViewById(R.id.checked);
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}
		
		Assessment assessment = assessments.get(position);
		if (assessment != null) {
			holder.name.setText(assessment.getName());
			holder.taken.setText(assessment.getTakenDate());
				
			holder.checked.setVisibility(assessment.isChecked() ? View.VISIBLE : View.INVISIBLE);
		}
		
		return convertView;
	}
	
	public void setAssessments(List<Assessment> assessments) {
		this.assessments = (assessments != null) ? assessments : new LinkedList<Assessment>();
	}
	
	public int getCheckedCount() {
		int count = 0;
		for (int i=0; i<assessments.size(); i++)
			if (assessments.get(i).isChecked())
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
		for (int i=0; i<assessments.size(); i++)
			assessments.get(i).setChecked(true);
		notifyDataSetChanged();
	}
	
	protected void unselectAll() {
		for (int i=0; i<assessments.size(); i++)
			assessments.get(i).setChecked(false);
		notifyDataSetChanged();
	}
	
	public List<Assessment> getCheckedAssessments() {
		List<Assessment> checkedAssessments = new LinkedList<Assessment>();
		for (int i=0; i<assessments.size(); i++)
			if (assessments.get(i).isChecked())
				checkedAssessments.add(assessments.get(i));
		return checkedAssessments;
	}
	
	class ViewHolder {
		TextView name;
		TextView taken;
		ImageView checked;
	}

}
