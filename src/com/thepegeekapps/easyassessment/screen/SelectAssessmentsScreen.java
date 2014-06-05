package com.thepegeekapps.easyassessment.screen;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.dropbox.client2.ProgressListener;
import com.dropbox.client2.android.AndroidAuthSession;
import com.dropbox.client2.session.TokenPair;
import com.thepegeekapps.easyassessment.R;
import com.thepegeekapps.easyassessment.adapter.SelectAssessmentAdapter;
import com.thepegeekapps.easyassessment.database.DatabaseHelper;
import com.thepegeekapps.easyassessment.listener.OnSwipeTouchListener;
import com.thepegeekapps.easyassessment.model.AStudent;
import com.thepegeekapps.easyassessment.model.Assessment;
import com.thepegeekapps.easyassessment.model.Criteria;
import com.thepegeekapps.easyassessment.model.Group;
import com.thepegeekapps.easyassessment.util.Utils;

public class SelectAssessmentsScreen extends BaseScreen implements OnClickListener, OnItemClickListener {
	
	protected ListView assessmentsList;
	protected TextView emptyView;
	protected Button backBtn;
	protected Button selectAllBtn;
	protected Button exportBtn;
	
	protected int groupId;
	protected Group group;
	protected List<Assessment> assessments;
	protected SelectAssessmentAdapter adapter;
	
	protected boolean showConfirmUploadingDialog;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.select_assessments_screen);
		getIntentData();
		setTitle((group != null) ? group.getName() + " " + getString(R.string.assessments) : getString(R.string.assessments));
		assessments = dbManager.getAssessmentsByGroupId(groupId);
		adapter = new SelectAssessmentAdapter(this, assessments);
		initializeViews();
		checkEmptyList();
	}
	
	@Override
    protected void onResume() {
        super.onResume();
        
        AndroidAuthSession session = mApi.getSession();
        if (session.authenticationSuccessful()) {
            try {
                session.finishAuthentication();
                TokenPair tokens = session.getAccessTokenPair();
                storeKeys(tokens.key, tokens.secret);
                loggedIn = true;
                
                if (showConfirmUploadingDialog) {
                	showConfirmUploadingDialog = false;
                	List<Assessment> selected = getSelectedAssessments();
					if (selected == null || selected.isEmpty()) {
						Toast.makeText(this, R.string.no_selected_assessments, Toast.LENGTH_SHORT).show();
					} else {
						ConnectivityManager conManager = (ConnectivityManager) getSystemService(CONNECTIVITY_SERVICE);
						NetworkInfo info = conManager.getActiveNetworkInfo();
						if (info == null || info.getState() != NetworkInfo.State.CONNECTED) {
							Toast.makeText(this, R.string.no_connection, Toast.LENGTH_SHORT).show();
						} else {
							exportToDropbox(selected);
						}
					}
                }
            } catch (IllegalStateException e) {
                Toast.makeText(this, "Couldn't authenticate with Dropbox:" + e.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
                e.printStackTrace();
            }
        }
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
		
		assessmentsList = (ListView) findViewById(R.id.assessmentsList);
		assessmentsList.setAdapter(adapter);
		assessmentsList.setOnItemClickListener(this);
		
		backBtn = (Button) findViewById(R.id.backBtn);
		backBtn.setVisibility(View.VISIBLE);
		backBtn.setOnClickListener(this);
		
		if (assessments != null && !assessments.isEmpty()) {
			selectAllBtn = (Button) findViewById(R.id.addNewBtn);
			selectAllBtn.setVisibility(View.VISIBLE);
			selectAllBtn.setText(R.string.select_all);
			selectAllBtn.setOnClickListener(this);
		}
		
		exportBtn = (Button) findViewById(R.id.nextBtn);
		exportBtn.setText(R.string.export);
		exportBtn.setVisibility(adapter.getCheckedCount() > 0 ? View.VISIBLE : View.INVISIBLE);
		exportBtn.setOnClickListener(this);
		
		View titlebar = findViewById(R.id.titlebar);
		titlebar.setOnTouchListener(new OnSwipeTouchListener() {
			@Override
			public void onSwipeRight() {
				Intent intent = new Intent(SelectAssessmentsScreen.this, MainScreen.class);
				intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
				startActivity(intent);
				overridePendingTransition(R.anim.view_transition_in_right, R.anim.view_transition_out_right);
			}
		});
	}
	
	protected void checkEmptyList() {
		boolean assessmentsNotEmpty = (assessments != null && !assessments.isEmpty());
		assessmentsList.setVisibility(assessmentsNotEmpty ? View.VISIBLE : View.GONE);
		emptyView.setVisibility(assessmentsNotEmpty ? View.GONE : View.VISIBLE);
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
		Assessment assessment = assessments.get(position);
		assessment.setChecked(!assessment.isChecked());
		adapter.notifyDataSetChanged();
		exportBtn.setVisibility(adapter.getCheckedCount() > 0 ? View.VISIBLE : View.INVISIBLE);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.backBtn:
			onBackPressed();
			break;
		case R.id.addNewBtn:
			adapter.toggleSelectAll();
			exportBtn.setVisibility(adapter.getCheckedCount() > 0 ? View.VISIBLE : View.INVISIBLE);
			break;
		case R.id.nextBtn:
			showSelectExportMethodDialog();
			break;
		}
	}
	
	protected void showSelectExportMethodDialog() {
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setTitle(R.string.export_title)
			.setItems(R.array.select_export_method, new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int which) {
					List<Assessment> selected = getSelectedAssessments();
					switch (which) {
					case 0:
						ConnectivityManager conManager = (ConnectivityManager) getSystemService(CONNECTIVITY_SERVICE);
						NetworkInfo info = conManager.getActiveNetworkInfo();
						if (info == null || info.getState() != NetworkInfo.State.CONNECTED) {
							Toast.makeText(SelectAssessmentsScreen.this, R.string.no_connection, Toast.LENGTH_SHORT).show();
						} else {
							if (selected == null || selected.isEmpty()) {
								Toast.makeText(SelectAssessmentsScreen.this, R.string.no_selected_assessments, Toast.LENGTH_SHORT).show();
							} else {
								if (loggedIn) {
									exportToDropbox(selected);
					            } else {
					            	showConfirmUploadingDialog = true;
					                mApi.getSession().startAuthentication(SelectAssessmentsScreen.this);
					            }
							}							
						}
						break;
					case 1:
						exportViaEmail(selected);
						break;
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
	
	protected List<Assessment> getSelectedAssessments() {
		List<Assessment> selected = new LinkedList<Assessment>();
		for (int i=0; i<assessments.size(); i++) {
			if (assessments.get(i).isChecked())
				selected.add(assessments.get(i));
		}
		return selected;
	}
	
	protected void exportToDropbox(List<Assessment> selected) {
		new DropboxUploadTask(selected).execute((Void[]) null);
	}
	
	protected void exportViaEmail(List<Assessment> selected) {
		new EmailUploadTask(selected).execute((Void[]) null);
	}
	
	class EmailUploadTask extends AsyncTask<Void, Void, Boolean> {
		
		protected ProgressDialog dialog;
		protected List<Assessment> selectedAssessments;
		protected String error;
		protected List<String> filePaths;
		
		public EmailUploadTask(List<Assessment> selectedAssessments) {
			this.selectedAssessments = selectedAssessments;
			filePaths = new LinkedList<String>();
			dialog = new ProgressDialog(SelectAssessmentsScreen.this);
    		dialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
    		dialog.setMessage(getString(R.string.please_wait));
    		dialog.setIndeterminate(true);
		}
		
		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			dialog.show();
		}
		
		@Override
		protected Boolean doInBackground(Void... params) {
			try {
				File tempFolder = new File(Environment.getExternalStorageDirectory()+"/easyassessments/temp");
				if (tempFolder == null || !tempFolder.exists())
					tempFolder.mkdirs();
				
				if (selectedAssessments != null && !selectedAssessments.isEmpty()) {
					for (Assessment assessment : selectedAssessments) {
						List<AStudent> aStudents = dbManager.getAStudentsByAssessmentId(assessment.getId());
						
						// create assessment csv file
						File assessmentFile = new File(tempFolder, assessment.getName()+".csv");
						FileOutputStream fos = new FileOutputStream(assessmentFile);
						
						String assesmentCSV = getAssessmentCSV(assessment, aStudents);
						fos.write(assesmentCSV.getBytes());
						fos.flush();
						
						filePaths.add(assessmentFile.getAbsolutePath());
						fos.close();
						
						// create student csv file
						if (aStudents != null && !aStudents.isEmpty()) {
							for (AStudent aStudent : aStudents) {
								File aStudentFile = new File(tempFolder, assessment.getName()+"_"+aStudent.getName()+".csv");
								FileOutputStream fos2 = new FileOutputStream(aStudentFile);
								
								String aStudentCsv = getAStudentCSV(assessment, aStudent);
								fos2.write(aStudentCsv.getBytes());
								fos2.close();
								
								filePaths.add(aStudentFile.getAbsolutePath());
								fos2.close();
							}
						}
					}
				}
			} catch (Exception e) {
				e.printStackTrace();
				error = getString(R.string.upload_error);
				return false;
			}
			return true;
		}
		
		@Override
		protected void onPostExecute(Boolean result) {
			super.onPostExecute(result);
			if (dialog != null && dialog.isShowing())
				dialog.dismiss();
			if (!result.booleanValue()) {
				Toast.makeText(SelectAssessmentsScreen.this, error, Toast.LENGTH_SHORT).show();
			} else {
				sendEmailIntent();
			}
		}
		
		protected void sendEmailIntent() {
			Intent intent = new Intent(Intent.ACTION_SEND_MULTIPLE);
			intent.setType("text/plain");
			String subject = getExtraSubject();
			intent.putExtra(Intent.EXTRA_SUBJECT, subject);
			ArrayList<Uri> uris = new ArrayList<Uri>();
			for (String filePath : filePaths) {
				File file = new File(filePath);
				Uri uri = Uri.fromFile(file);
				uris.add(uri);
			}
			intent.putParcelableArrayListExtra(Intent.EXTRA_STREAM, uris);
			startActivity(Intent.createChooser(intent, getString(R.string.send_email)));
		}
		
		protected String getExtraSubject() {
			String subject = "";
			if (selectedAssessments != null && !selectedAssessments.isEmpty()) {
				for (int i=0; i<selectedAssessments.size(); i++) {
					subject += selectedAssessments.get(i).getName();
					if (i != selectedAssessments.size()-1)
						subject += ", ";
				}
			}
			return subject;
		}
		
		protected String getAssessmentCSV(Assessment assessment, List<AStudent> aStudents) {
			String result = "";
			result += "Group," + assessment.getGroupName() + "\n";
			result += "Rubric," + assessment.getRubricName() + "\n";
			if (aStudents != null && !aStudents.isEmpty()) {
				result += ",";
				for (int i=0; i<aStudents.size(); i++) {
					result += aStudents.get(i).getName();
					result += (i < aStudents.size()-1) ? "," : "\n";
				}
				List<Criteria> criterias = dbManager.getCriteriasByRubricId(assessment.getRubricId());
				for (int i=0; i<criterias.size(); i++) {
					result += criterias.get(i).getName() + ",";
					for (int j=0; j<aStudents.size(); j++) {
						result += aStudents.get(j).getValueByCriteriaId(criterias.get(i).getId());
						result += (j < aStudents.size()-1) ? "," : "\n";
					}
				}
				result += "Comments,";
				for (int i=0; i<aStudents.size(); i++) {
					result += aStudents.get(i).getNote();
					result += (i < aStudents.size()-1) ? "," : "\n";
				}
			}
			return result;
		}
		
		protected String getAStudentCSV(Assessment assessment, AStudent aStudent) {
			List<AStudent> aStudents = new LinkedList<AStudent>();
			aStudents.add(aStudent);
			return getAssessmentCSV(assessment, aStudents);
		}
		
	}
	
	class DropboxUploadTask extends AsyncTask<Void, Long, Boolean> {
		
		protected ProgressDialog dialog;
		protected List<Assessment> selectedAssessments;
		protected String error;
		
		protected String currentName;
		
		public DropboxUploadTask(List<Assessment> selectedAssessments) {
			this.selectedAssessments = selectedAssessments;
			dialog = new ProgressDialog(SelectAssessmentsScreen.this);
    		dialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
    		dialog.setMessage(getString(R.string.uploading));
    		dialog.setIndeterminate(true);
		}
		
		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			dialog.show();
		}

		@Override
		protected Boolean doInBackground(Void... params) {
			try {
				String rootFolder = "/Easy Assessments";
				try { mApi.createFolder(rootFolder); } catch (Exception e) { e.printStackTrace(); }
				
				for (Assessment assessment : selectedAssessments) {
					String assessmentFolder = rootFolder + "/" + assessment.getName();
					try { mApi.createFolder(assessmentFolder); } catch (Exception e) { e.printStackTrace(); }
					
					List<AStudent> aStudents = dbManager.getAStudentsByAssessmentId(assessment.getId());
					
					// export assessment csv
					currentName = assessment.getName() + ".csv";
					String assessmentPath = assessmentFolder + "/" + currentName;
					String assessmentCSV = getAssessmentCSV(assessment, aStudents);
					ByteArrayInputStream bais = new ByteArrayInputStream(assessmentCSV.getBytes());
					try {
						mApi.putFileOverwrite(assessmentPath, bais, assessmentCSV.getBytes().length, new ProgressListener() {
							@Override
							public void onProgress(long bytes, long total) {
								publishProgress(bytes, total);
							}
						});
					} catch (Exception e) {
						e.printStackTrace();
					} finally {
						if (bais != null)
							bais.close();
					}
					
					if (aStudents != null && !aStudents.isEmpty()) {
						for (AStudent aStudent  :aStudents) {
							String aStudentFolder = assessmentFolder + "/" + aStudent.getName();
							try { mApi.createFolder(aStudentFolder); } catch (Exception e) { e.printStackTrace(); }
							
							// export image
							if (aStudent.hasImage()) {
								currentName = aStudent.getName() + " : Photo [" + aStudent.getImagePath().substring(aStudent.getImagePath().lastIndexOf("/")+1) + "]";
								String imagePath = aStudentFolder + "/" + aStudent.getImagePath().substring(aStudent.getImagePath().lastIndexOf("/")+1);
								File file = new File(aStudent.getImagePath());
								if (file != null && file.exists()) {
									FileInputStream is = new FileInputStream(file);
									try {
										mApi.putFileOverwrite(imagePath, is, file.length(), new ProgressListener() {
											@Override
											public void onProgress(long bytes, long total) {
												publishProgress(bytes, total);
											}
										});
									} catch (Exception e) {
										e.printStackTrace();
									} finally {
										if (is != null)
											is.close();
									}
								}
							}
							
							// export video
							if (aStudent.hasVideo()) {
								currentName = aStudent.getName() + " : Video [" + aStudent.getVideoPath().substring(aStudent.getVideoPath().lastIndexOf("/")+1) + "]";
								String videoPath = aStudentFolder + "/" + aStudent.getVideoPath().substring(aStudent.getVideoPath().lastIndexOf("/")+1);
								File file = new File(aStudent.getVideoPath());
								if (file != null && file.exists()) {
									FileInputStream is = new FileInputStream(file);
									try {
										mApi.putFileOverwrite(videoPath, is, file.length(), new ProgressListener() {
											@Override
											public void onProgress(long bytes, long total) {
												publishProgress(bytes, total);
											}
										});
									} catch (Exception e) {
										e.printStackTrace();
									} finally {
										if (is != null)
											is.close();
									}
								}
							}
							
							// export student csv
							currentName = assessment.getName()+"_"+aStudent.getName() + ".csv";
							String csvPath = aStudentFolder + "/" + currentName;
							String studentCSV = getAStudentCSV(assessment, aStudent);
							ByteArrayInputStream bais2 = new ByteArrayInputStream(assessmentCSV.getBytes());
							try {
								mApi.putFileOverwrite(csvPath, bais2, studentCSV.getBytes().length, new ProgressListener() {
									@Override
									public void onProgress(long bytes, long total) {
										publishProgress(bytes, total);
									}
								});
							} catch (Exception e) {
								e.printStackTrace();
							} finally {
								if (bais2 != null)
									bais2.close();
							}
							
						}
					}
				}
			} catch (Exception e) {
				e.printStackTrace();
				error = getString(R.string.upload_error);
				return false;
			}
			return true;
		}
		
		@Override
		protected void onProgressUpdate(Long... values) {
			String message = String.format(getString(R.string.upload_pattern), 
					currentName, Utils.getReadableFilesize(values[0]), Utils.getReadableFilesize(values[1]));
			dialog.setMessage(message);
			super.onProgressUpdate(values);
		}
		
		@Override
		public void onPostExecute(Boolean result) {
			if (dialog != null && dialog.isShowing())
				dialog.dismiss();
			if (!result.booleanValue()) {
				Toast.makeText(SelectAssessmentsScreen.this, error, Toast.LENGTH_SHORT).show();
			} else {
				Intent intent = new Intent(SelectAssessmentsScreen.this, MainScreen.class);
				intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
				startActivity(intent);
				overridePendingTransition(R.anim.view_transition_in_right, R.anim.view_transition_out_right);
			}
		}
		
		protected String getAssessmentCSV(Assessment assessment, List<AStudent> aStudents) {
			String result = "";
			result += "Group," + assessment.getGroupName() + "\n";
			result += "Rubric," + assessment.getRubricName() + "\n";
			if (aStudents != null && !aStudents.isEmpty()) {
				result += ",";
				for (int i=0; i<aStudents.size(); i++) {
					result += aStudents.get(i).getName();
					result += (i < aStudents.size()-1) ? "," : "\n";
				}
				List<Criteria> criterias = dbManager.getCriteriasByRubricId(assessment.getRubricId());
				for (int i=0; i<criterias.size(); i++) {
					result += criterias.get(i).getName() + ",";
					for (int j=0; j<aStudents.size(); j++) {
						result += aStudents.get(j).getValueByCriteriaId(criterias.get(i).getId());
						result += (j < aStudents.size()-1) ? "," : "\n";
					}
				}
				result += "Comments,";
				for (int i=0; i<aStudents.size(); i++) {
					result += aStudents.get(i).getNote();
					result += (i < aStudents.size()-1) ? "," : "\n";
				}
			}
			return result;
		}
		
		protected String getAStudentCSV(Assessment assessment, AStudent aStudent) {
			List<AStudent> aStudents = new LinkedList<AStudent>();
			aStudents.add(aStudent);
			return getAssessmentCSV(assessment, aStudents);
		}
		
	}

}
