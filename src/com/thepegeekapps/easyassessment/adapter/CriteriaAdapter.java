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
import com.thepegeekapps.easyassessment.model.Criteria;
import com.thepegeekapps.easyassessment.model.Rubric;
import com.thepegeekapps.easyassessment.util.Utils;
import com.thepegeekapps.easyassessment.view.wheel.WheelView;
import com.thepegeekapps.easyassessment.view.wheel.adapters.ArrayWheelAdapter;

public class CriteriaAdapter extends BaseAdapter {
	
	protected Context context; 
	protected List<Criteria> criterias;
	
	public CriteriaAdapter(Context context, List<Criteria> criterias) {
		this.context = context;
		setCriterias(criterias);
	}

	@Override
	public int getCount() {
		return (criterias != null) ? criterias.size() : 0;
	}

	@Override
	public Criteria getItem(int position) {
		return (criterias != null && !criterias.isEmpty()) ? criterias.get(position) : null;
	}

	@Override
	public long getItemId(int position) {
		return (criterias != null && !criterias.isEmpty()) ? criterias.get(position).getId() : 0;
	}

	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {
		ViewHolder holder = null;
		if (convertView == null) {
			LayoutInflater inflater = (LayoutInflater) context.getSystemService(Service.LAYOUT_INFLATER_SERVICE);
			convertView = inflater.inflate(R.layout.criteria_list_item, null);
			holder = new ViewHolder();
			holder.name = (TextView) convertView.findViewById(R.id.name);
			holder.scale = (TextView) convertView.findViewById(R.id.scale);
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}
		
		final Criteria criteria = criterias.get(position);
		holder.name.setText(criteria.getName());
		
		String scaleStr = criteria.getStartScale() + " - " + criteria.getEndScale();
		holder.scale.setText(scaleStr);
		holder.scale.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				showSelectScaleDialog(criteria, v);
			}
		});

		return convertView;
	}

	public void setCriterias(List<Criteria> criterias) {
		this.criterias = (criterias != null) ? criterias : new LinkedList<Criteria>();
	}
	
	class ViewHolder {
		TextView name;
		TextView scale;
	}
	
	protected void showSelectScaleDialog(final Criteria criteria, final View scaleView) {		
		final String[] scaledItems = Utils.getScaleItems(Rubric.MIN_SCALE, Rubric.MAX_SCALE);
		
		LayoutInflater inflater = (LayoutInflater) context.getSystemService(Service.LAYOUT_INFLATER_SERVICE);
		ViewGroup dialogView = (ViewGroup) inflater.inflate(R.layout.select_scale_dialog, null);
		
		final WheelView startScaleWheel = (WheelView) dialogView.findViewById(R.id.selectStartScale);
		ArrayWheelAdapter<String> startScaleAdapter = new ArrayWheelAdapter<String>(context, scaledItems);
		startScaleWheel.setViewAdapter(startScaleAdapter);
		startScaleWheel.setCurrentItem(criteria.getStartScale());
		
		final WheelView endScaleWheel = (WheelView) dialogView.findViewById(R.id.selectEndScale);
		ArrayWheelAdapter<String> endScaleAdapter = new ArrayWheelAdapter<String>(context, scaledItems);
		endScaleWheel.setViewAdapter(endScaleAdapter);	
		endScaleWheel.setCurrentItem(criteria.getEndScale());
		
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
						criteria.setStartScale(startScale);
						criteria.setEndScale(endScale);
						DatabaseManager.getInstance(context).updateCriteria(criteria);
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
