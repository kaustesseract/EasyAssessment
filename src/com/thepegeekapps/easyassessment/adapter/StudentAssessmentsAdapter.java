package com.thepegeekapps.easyassessment.adapter;

import java.io.File;
import java.util.LinkedList;
import java.util.List;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.media.ThumbnailUtils;
import android.net.Uri;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.SeekBar;
import android.widget.TextView;

import com.thepegeekapps.easyassessment.R;
import com.thepegeekapps.easyassessment.database.DatabaseManager;
import com.thepegeekapps.easyassessment.model.ACriteria;
import com.thepegeekapps.easyassessment.model.AStudent;
import com.thepegeekapps.easyassessment.model.Assessment;
import com.thepegeekapps.easyassessment.model.Student;

public class StudentAssessmentsAdapter extends BaseAdapter {
	
	protected Context context;
	protected List<Assessment> assessments;
	protected Student student;
	
	public StudentAssessmentsAdapter(Context context, List<Assessment> assessments, Student student) {
		this.context = context;
		setAssessments(assessments);
		this.student = student;
	}

	@Override
	public int getCount() {
		return (assessments != null) ? assessments.size() : 0;
	}

	@Override
	public Assessment getItem(int position) {
		return (assessments != null) ? assessments.get(position) : null;
	}

	@Override
	public long getItemId(int position) {
		return (assessments != null && assessments.get(position) != null) ? assessments.get(position).getId() : 0;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder holder = null;
		if (convertView == null) {
			LayoutInflater inflater = (LayoutInflater) context.getSystemService(Service.LAYOUT_INFLATER_SERVICE);
			convertView = inflater.inflate(R.layout.student_assessments_list_item, null);
			holder = new ViewHolder();
			holder.assessmentName = (TextView) convertView.findViewById(R.id.assessmentName);
			holder.rubricName = (TextView) convertView.findViewById(R.id.rubricName);
			holder.criterias = (LinearLayout) convertView.findViewById(R.id.criterias);
			holder.notesLayout = (LinearLayout) convertView.findViewById(R.id.notesLayout);
			holder.note = (TextView) convertView.findViewById(R.id.note);
			holder.photo = (ImageView) convertView.findViewById(R.id.photo);
			holder.video = (ImageView) convertView.findViewById(R.id.video);
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}
		
		Assessment assessment = assessments.get(position);
		if (assessment != null) {
			holder.assessmentName.setText(assessment.getName());
			holder.rubricName.setText(assessment.getRubricName());
			if (holder.criterias.getChildCount() == 0) {
				populateCriterias(holder.criterias, assessment.getId());
			}
			
			final AStudent aStudent = DatabaseManager.getInstance(context).getAStudent(assessment.getId(), student.getId());
			if (aStudent.hasExtraInfo()) {
				holder.notesLayout.setVisibility(View.VISIBLE);
				
				if (aStudent.hasNote()) {
					holder.note.setVisibility(View.VISIBLE);
					holder.note.setText(aStudent.getNote());
				} else {
					holder.note.setVisibility(View.GONE);
				}
				
				if (aStudent.hasImage()) {
					holder.photo.setVisibility(View.VISIBLE);
					int dstWidth = 100;
					int dstHeight = 70;
					Bitmap src = BitmapFactory.decodeFile(aStudent.getImagePath());
					if (src != null) {
						Bitmap scaled = Bitmap.createScaledBitmap(src, dstWidth, dstHeight, true);
						holder.photo.setImageBitmap(scaled);
					}
					holder.photo.setOnClickListener(new OnClickListener() {
						@Override
						public void onClick(View v) {
							File f = new File(aStudent.getImagePath());
							if (f != null && f.exists()) {
								Intent intent = new Intent(Intent.ACTION_VIEW);
								intent.setDataAndType(Uri.fromFile(f), "image/*");
								context.startActivity(intent);
							}
						}
					});
				} else {
					holder.photo.setVisibility(View.GONE);
				}
				
				if (aStudent.hasVideo()) {
					holder.video.setVisibility(View.VISIBLE);
					int dstWidth = 100;
					int dstHeight = 70;
					Bitmap src = ThumbnailUtils.createVideoThumbnail(aStudent.getVideoPath(), MediaStore.Video.Thumbnails.FULL_SCREEN_KIND);
					if (src != null) {
						Bitmap scaled = Bitmap.createScaledBitmap(src, dstWidth, dstHeight, true);
						holder.video.setImageBitmap(scaled);
					}
					holder.video.setOnClickListener(new OnClickListener() {
						@Override
						public void onClick(View v) {
							File f = new File(aStudent.getVideoPath());
							if (f != null && f.exists()) {
								Intent intent = new Intent(Intent.ACTION_VIEW);
								intent.setDataAndType(Uri.parse(aStudent.getVideoPath()), "video/*");
								context.startActivity(intent);
							}
						}
					});
				} else {
					holder.video.setVisibility(View.GONE);
				}
			} else {
				holder.notesLayout.setVisibility(View.GONE);
			}
		}
		
		return convertView;
	}
	
	protected void populateCriterias(LinearLayout parent, int assessmentId) {
		AStudent aStudent = DatabaseManager.getInstance(context).getAStudent(assessmentId, student.getId());
		if (aStudent != null && aStudent.getACriterias() != null && !aStudent.getACriterias().isEmpty()) {
			List<ACriteria> aCriterias = aStudent.getACriterias();
			for (int i=0; i<aCriterias.size(); i++) {
				LinearLayout layout = getACriteriaLayout(aCriterias.get(i));
				parent.addView(layout);
				if (i < aCriterias.size()-1) {
					View v = getCriteriasDivider();
					parent.addView(v);
				}
 			}
		}
	}
	
	protected LinearLayout getACriteriaLayout(final ACriteria aCriteria) {
		LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		LinearLayout aCriteriaLayout = (LinearLayout) inflater.inflate(R.layout.a_criteria_layout, null);
		
		TextView name = (TextView) aCriteriaLayout.findViewById(R.id.aCriteriaName);
		name.setText(aCriteria.getName());
		
		final TextView value = (TextView) aCriteriaLayout.findViewById(R.id.aCriteriaValue);
		value.setText(String.valueOf(aCriteria.getValue()));
		
		SeekBar seekBar = (SeekBar) aCriteriaLayout.findViewById(R.id.aCriteriaSeekBar);
		seekBar.setMax(aCriteria.getEndScale() - aCriteria.getStartScale());
		seekBar.setProgress(aCriteria.getValue() - aCriteria.getStartScale());
		seekBar.setEnabled(false);

		return aCriteriaLayout;
	}
	
	protected View getCriteriasDivider() {
		View v = new View(context);
		v.setLayoutParams(new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT, 1));
		v.setBackgroundColor(Color.GRAY);
		return v;
	}
	
	public void setAssessments(List<Assessment> assessments) {
		this.assessments = (assessments != null) ? assessments : new LinkedList<Assessment>();
	}
	
	class ViewHolder {
		TextView assessmentName;
		TextView rubricName;
		LinearLayout criterias;
		LinearLayout notesLayout;
		TextView note;
		ImageView photo;
		ImageView video;
	}

}
