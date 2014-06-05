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
import com.thepegeekapps.easyassessment.model.Rubric;

public class SelectRubricAdapter extends BaseAdapter {
	
	protected Context context;
	protected List<Rubric> rubrics;
	
	public SelectRubricAdapter(Context context, List<Rubric> rubrics) {
		this.context = context;
		setRubrics(rubrics);
	}

	@Override
	public int getCount() {
		return (rubrics != null) ? rubrics.size() : 0;
	}

	@Override
	public Rubric getItem(int position) {
		return (rubrics != null && !rubrics.isEmpty()) ? rubrics.get(position) : null;
	}

	@Override
	public long getItemId(int position) {
		return (rubrics != null && !rubrics.isEmpty()) ? rubrics.get(position).getId() : 0;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder holder = null;
		if (convertView == null) {
			LayoutInflater inflater = (LayoutInflater) context.getSystemService(Service.LAYOUT_INFLATER_SERVICE);
			convertView = inflater.inflate(R.layout.select_rubric_list_item, null);
			holder = new ViewHolder();
			holder.name = (TextView) convertView.findViewById(R.id.name);
			holder.count = (TextView) convertView.findViewById(R.id.count);
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}
		
		Rubric rubric = rubrics.get(position);
		
		holder.name.setText(rubric.getName());

		int count = (rubric.getCriterias() != null) ? rubric.getCriterias().size() : 0;
		String countStr = String.format(context.getResources().getString(R.string.criterias_count_pattern), count);
		holder.count.setText(countStr);

		return convertView;
	}
	
	public void setRubrics(List<Rubric> rubrics) {
		this.rubrics = (rubrics != null) ? rubrics : new LinkedList<Rubric>();
	}
	
	class ViewHolder {
		TextView name;
		TextView count;
	}


}
