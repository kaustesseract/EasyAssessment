package com.thepegeekapps.easyassessment.adapter;

import java.util.LinkedList;
import java.util.List;

import android.app.Service;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.thepegeekapps.easyassessment.R;
import com.thepegeekapps.easyassessment.model.Group;

public class SelectGroupAdapter extends BaseAdapter {
	
	protected Context context;
	protected List<Group> groups;
	
	public SelectGroupAdapter(Context context, List<Group> groups) {
		this.context = context;
		setGroups(groups);
	}

	@Override
	public int getCount() {
		return (groups != null) ? groups.size() : 0;
	}

	@Override
	public Group getItem(int position) {
		return (groups != null && !groups.isEmpty()) ? groups.get(position) : null;
	}

	@Override
	public long getItemId(int position) {
		return (groups != null && !groups.isEmpty()) ? groups.get(position).getId() : 0;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder holder = null;
		if (convertView == null) {
			LayoutInflater inflater = (LayoutInflater) context.getSystemService(Service.LAYOUT_INFLATER_SERVICE);
			convertView = inflater.inflate(R.layout.select_group_list_item, null);
			holder = new ViewHolder();
			holder.name = (TextView) convertView.findViewById(R.id.name);
			holder.count = (TextView) convertView.findViewById(R.id.count);
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}
		
		Group group = groups.get(position);
		holder.name.setText(group.getName());

//		int count = (group.getAssessments() != null) ? group.getAssessments().size() : 0;
		String countStr = String.format(context.getResources().getString(R.string.assessments_count_pattern), group.getAssessmentsCount());
		holder.count.setText(countStr);

		return convertView;
	}

	public void setGroups(List<Group> groups) {
		this.groups = (groups != null) ? groups : new LinkedList<Group>();
	}
	
	class ViewHolder {
		TextView name;
		TextView count;
	}

}
