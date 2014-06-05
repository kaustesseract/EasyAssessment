package com.thepegeekapps.easyassessment.adapter;

import java.util.LinkedList;
import java.util.List;

import android.app.AlertDialog;
import android.app.Service;
import android.content.Context;
import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.thepegeekapps.easyassessment.R;
import com.thepegeekapps.easyassessment.database.DatabaseManager;
import com.thepegeekapps.easyassessment.model.Rubric;
import com.thepegeekapps.easyassessment.util.Utils;
import com.thepegeekapps.easyassessment.view.wheel.WheelView;
import com.thepegeekapps.easyassessment.view.wheel.adapters.ArrayWheelAdapter;

public class RubricAdapter extends BaseAdapter {
	
	protected Context context; 
	protected List<Rubric> rubrics;
	
	public RubricAdapter(Context context, List<Rubric> rubrics) {
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
	public View getView(final int position, View convertView, ViewGroup parent) {
		ViewHolder holder = null;
		if (convertView == null) {
			LayoutInflater inflater = (LayoutInflater) context.getSystemService(Service.LAYOUT_INFLATER_SERVICE);
			convertView = inflater.inflate(R.layout.rubric_list_item, null);
			holder = new ViewHolder();
			holder.name = (TextView) convertView.findViewById(R.id.name);
			holder.count = (TextView) convertView.findViewById(R.id.count);
			holder.scale = (TextView) convertView.findViewById(R.id.scale);
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}
		
		final Rubric rubric = rubrics.get(position);
		holder.name.setText(rubric.getName());

		int count = (rubric.getCriterias() != null) ? rubric.getCriterias().size() : 0;
		String countStr = String.format(context.getResources().getString(R.string.criterias_count_pattern), count);
		holder.count.setText(countStr);
		
		String scaleStr = rubric.getStartScale() + " - " + rubric.getEndScale();
		holder.scale.setText(scaleStr);
		holder.scale.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				showSelectScaleDialog(rubric, v);
			}
		});

		return convertView;
	}

	public void setRubrics(List<Rubric> groups) {
		this.rubrics = (groups != null) ? groups : new LinkedList<Rubric>();
	}
	
	class ViewHolder {
		TextView name;
		TextView count;
		TextView scale;
	}
	
	protected void showSelectScaleDialog(final Rubric rubric, final View scaleView) {		
		final String[] scaledItems = Utils.getScaleItems(Rubric.MIN_SCALE, Rubric.MAX_SCALE);
		
		LayoutInflater inflater = (LayoutInflater) context.getSystemService(Service.LAYOUT_INFLATER_SERVICE);
		ViewGroup dialogView = (ViewGroup) inflater.inflate(R.layout.select_scale_dialog, null);
		
		final WheelView startScaleWheel = (WheelView) dialogView.findViewById(R.id.selectStartScale);
		ArrayWheelAdapter<String> startScaleAdapter = new ArrayWheelAdapter<String>(context, scaledItems);
		startScaleWheel.setViewAdapter(startScaleAdapter);
		startScaleWheel.setCurrentItem(rubric.getStartScale());
		
		final WheelView endScaleWheel = (WheelView) dialogView.findViewById(R.id.selectEndScale);
		ArrayWheelAdapter<String> endScaleAdapter = new ArrayWheelAdapter<String>(context, scaledItems);
		endScaleWheel.setViewAdapter(endScaleAdapter);	
		endScaleWheel.setCurrentItem(rubric.getEndScale());
		
		AlertDialog.Builder builder = new AlertDialog.Builder(context);
		builder.setTitle(R.string.select_scale)
		.setView(dialogView)
		.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {		
				try {
					int startScale = Integer.parseInt(scaledItems[startScaleWheel.getCurrentItem()]);
					int endScale = Integer.parseInt(scaledItems[endScaleWheel.getCurrentItem()]);
					if (startScale < endScale) {
						rubric.setStartScale(startScale);
						rubric.setEndScale(endScale);
						DatabaseManager.getInstance(context).updateRubric(rubric);
						((TextView) scaleView).setText(startScale + " - " + endScale);
					} else {
						Toast.makeText(context, R.string.scale_error, Toast.LENGTH_SHORT).show();
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
				dialog.dismiss();
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

}
