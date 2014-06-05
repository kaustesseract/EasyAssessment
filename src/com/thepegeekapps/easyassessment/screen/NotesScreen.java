package com.thepegeekapps.easyassessment.screen;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.ThumbnailUtils;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.provider.MediaStore;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.thepegeekapps.easyassessment.R;
import com.thepegeekapps.easyassessment.database.DatabaseHelper;
import com.thepegeekapps.easyassessment.listener.OnSwipeTouchListener;
import com.thepegeekapps.easyassessment.model.AStudent;

public class NotesScreen extends BaseScreen implements OnClickListener {
	
	public static final int ADD_PHOTO_REQUEST_CODE = 0;
	public static final int ADD_VIDEO_REQUEST_CODE = 1;
	
	protected EditText note;
	protected LinearLayout selectImage;
	protected ImageView image;
	protected LinearLayout selectVideo;
	protected ImageView video;
	protected Button backBtn; 
	
	protected int aStudentId;
	protected AStudent aStudent;
	
	Handler handler = new Handler();
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.notes_screen);
		getIntentData();
		setTitle(aStudent != null ? aStudent.getName() + " " + getString(R.string.notes) : getString(R.string.notes));
		initializeViews();
		populateFields();
	}
	
	protected void getIntentData() {
		Intent intent = getIntent();
		if (intent != null && intent.hasExtra(DatabaseHelper.FIELD_ID)) {
			aStudentId = intent.getIntExtra(DatabaseHelper.FIELD_ID, 0);
			aStudent = dbManager.getAStudentById(aStudentId);
		}
	}
	
	protected void initializeViews() {
		note = (EditText) findViewById(R.id.note);
		
		selectImage = (LinearLayout) findViewById(R.id.selectImage);
		selectImage.setOnClickListener(this);
		
		selectVideo = (LinearLayout) findViewById(R.id.selectVideo);
		selectVideo.setOnClickListener(this);
		
		image = (ImageView) findViewById(R.id.image);
		image.setOnClickListener(this);
		
		video = (ImageView) findViewById(R.id.video);
		video.setOnClickListener(this);
		
		backBtn = (Button) findViewById(R.id.backBtn);
		backBtn.setVisibility(View.VISIBLE);
		backBtn.setOnClickListener(this);
		
		View titlebar = findViewById(R.id.titlebar);
		titlebar.setOnTouchListener(new OnSwipeTouchListener() {
			@Override
			public void onSwipeRight() {
				Intent intent = new Intent(NotesScreen.this, MainScreen.class);
				intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
				startActivity(intent);
				overridePendingTransition(R.anim.view_transition_in_right, R.anim.view_transition_out_right);
			}
		});
	}
	
	protected void populateFields() {
		handler.postDelayed(new Runnable() {
			@Override
			public void run() {
				if (aStudent != null) {					
					if (aStudent.getImagePath() != null) {
						int dstWidth = image.getWidth();
						int dstHeight = image.getHeight();
						Bitmap src = BitmapFactory.decodeFile(aStudent.getImagePath());
						if (src != null) {
							Bitmap scaled = Bitmap.createScaledBitmap(src, dstWidth, dstHeight, true);
							image.setImageBitmap(scaled);
						}
					}
					if (aStudent.getVideoPath() != null) {
						int dstWidth = video.getWidth();
						int dstHeight = video.getHeight();
						Bitmap src = ThumbnailUtils.createVideoThumbnail(aStudent.getVideoPath(), MediaStore.Video.Thumbnails.FULL_SCREEN_KIND);
						if (src != null) {
							Bitmap scaled = Bitmap.createScaledBitmap(src, dstWidth, dstHeight, true);
							video.setImageBitmap(scaled);
						}
					}
					note.setText(aStudent.getNote());
				}
			}
		}, 200);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.selectImage:
			showSelectActionAddPhotoDialog();
			break;
		case R.id.selectVideo:
			showSelectActionAddVideoDialog();
			break;
		case R.id.backBtn:
			onBackPressed();
			break;
		case R.id.image:
			if (aStudent != null && aStudent.getImagePath() != null) {
				File f = new File(aStudent.getImagePath());
				if (f != null && f.exists()) {
					Intent intent = new Intent(Intent.ACTION_VIEW);
					intent.setDataAndType(Uri.fromFile(f), "image/*");
					startActivity(intent);
				}
			}
			break;
		case R.id.video:
			if (aStudent != null && aStudent.getVideoPath() != null) {
				File f = new File(aStudent.getVideoPath());
				if (f != null && f.exists()) {
					Intent intent = new Intent(Intent.ACTION_VIEW);
					intent.setDataAndType(Uri.parse(aStudent.getVideoPath()), "video/*");
					startActivity(intent);
				}
			}
			break;
		}
	}
	
	@Override
	public void onBackPressed() {
		if (aStudent != null) {
			aStudent.setNote(note.getText().toString());
			dbManager.updateAStudent(aStudent, false);
			Intent data = new Intent();
			data.putExtra(DatabaseHelper.FIELD_NOTE, aStudent.getNote());
			data.putExtra(DatabaseHelper.FIELD_IMAGE_PATH, aStudent.getImagePath());
			data.putExtra(DatabaseHelper.FIELD_VIDEO_PATH, aStudent.getVideoPath());
			setResult(RESULT_OK, data);
			finish();
		} else {
			super.onBackPressed();
		}
	}
	
	protected void showSelectActionAddPhotoDialog() {
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		
		PackageManager pm = getPackageManager();
		String[] items = null;
		if (pm.hasSystemFeature(PackageManager.FEATURE_CAMERA)) {
			items = new String[] {getString(R.string.take_photo), getString(R.string.select_from_library), getString(R.string.remove_photo)};
		} else {
			items = new String[] {getString(R.string.select_from_library), getString(R.string.remove_photo)};
		}
		
		builder.setTitle(R.string.select_option)
		.setItems(items, new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				switch (which) {
				case 0:
					try { 
						Intent cameraIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
			            startActivityForResult(Intent.createChooser(cameraIntent, "Take photo"), ADD_PHOTO_REQUEST_CODE);
					} catch (Exception e) {
						e.printStackTrace();
					}
		            break;
				case 1:
					Intent intent = new Intent(Intent.ACTION_PICK);
					intent.setType("image/*");
					startActivityForResult(intent, ADD_PHOTO_REQUEST_CODE);
				case 2:
					deletePhoto();
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
	
	protected void showSelectActionAddVideoDialog() {
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		
		PackageManager pm = getPackageManager();
		String[] items = null;
		if (pm.hasSystemFeature(PackageManager.FEATURE_CAMERA)) {
			items = new String[] {getString(R.string.take_video), getString(R.string.select_from_library), getString(R.string.remove_video)};
		} else {
			items = new String[] {getString(R.string.select_from_library), getString(R.string.remove_video)};
		}
		
		builder.setTitle(R.string.select_option)
		.setItems(items, new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				switch (which) {
				case 0:
					try { 
						Intent cameraIntent = new Intent(android.provider.MediaStore.ACTION_VIDEO_CAPTURE);
			            startActivityForResult(Intent.createChooser(cameraIntent, "Take video"), ADD_VIDEO_REQUEST_CODE);
					} catch (Exception e) {
						e.printStackTrace();
					}
		            break;
				case 1:
					Intent intent = new Intent(Intent.ACTION_PICK);
					intent.setType("video/*");
					startActivityForResult(intent, ADD_VIDEO_REQUEST_CODE);
				case 2:
					deleteVideo();
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
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (resultCode == RESULT_OK) {
			switch (requestCode) {
			case ADD_PHOTO_REQUEST_CODE:
				if (aStudent != null && aStudent.getImagePath() != null)
					deletePhoto();
				(new AddPhotoTask(data)).execute((Void[]) null);
				break;
			case ADD_VIDEO_REQUEST_CODE:
				if (aStudent != null && aStudent.getVideoPath() != null)
					deleteVideo();
				(new AddVideoTask(data)).execute((Void[]) null);
				break;
			}
		}
	}
	
	protected void deletePhoto() {
		try {
			File f = new File(aStudent.getImagePath());
			if (f != null && f.exists())
				f.delete();
			aStudent.setImagePath(null);
			image.setImageResource(R.drawable.image_empty);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	protected void deleteVideo() {
		try {
			File f = new File(aStudent.getVideoPath());
			if (f != null && f.exists())
				f.delete();
			aStudent.setVideoPath(null);
			video.setImageResource(R.drawable.video_empty);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	class AddPhotoTask extends AsyncTask<Void, Void, Boolean> {
		
		protected Intent data;
		
		public AddPhotoTask(Intent data) {
			this.data = data;
		}
		
		@Override
		protected void onPreExecute() {
			showProgressDialog(getString(R.string.importing));
		}

		@Override
		protected Boolean doInBackground(Void... params) {
			Bitmap bitmap = null, scaled = null;
			BitmapFactory.Options options = new BitmapFactory.Options();
			
			DisplayMetrics metrics = getResources().getDisplayMetrics();
			int screenHeight = metrics.heightPixels;
			
			try {
				if (data != null && data.hasExtra("data")) {
					// image captured from camera
					bitmap = (Bitmap) data.getExtras().get("data");
					int bitmapWidth = bitmap.getWidth();
					int bitmapHeight = bitmap.getHeight();
					
					int reqWidth = bitmapWidth, reqHeight = bitmapHeight; 
					if (bitmapWidth > bitmapHeight) {
						while (reqWidth > screenHeight) {
							reqWidth--;
							reqHeight--;
						}
					} else {
						while (reqHeight > screenHeight) {
							reqWidth--;
							reqHeight--;
						}
					}
					
					scaled = Bitmap.createScaledBitmap(bitmap, reqWidth, reqHeight, true);
				} else {
					// image taken from gallery
					String realPath = getRealPathFromURI(data.getData());
					
					options.inJustDecodeBounds = true;
					bitmap = BitmapFactory.decodeFile(realPath, options);
					
					int reqWidth = options.outWidth, reqHeight = options.outHeight; 
					if (reqWidth > reqHeight) {
						while (reqWidth > screenHeight) {
							reqWidth--;
							reqHeight--;
						}
						options.inSampleSize = Math.round(options.outWidth / reqWidth); 
					} else {
						while (reqHeight > screenHeight) {
							reqWidth--;
							reqHeight--;
						}
						options.inSampleSize = Math.round(options.outHeight / reqHeight);
					}
					
					options.inJustDecodeBounds = false;
					scaled = BitmapFactory.decodeFile(realPath, options);
				}
				
				if (scaled != null) {
					String path = saveImage(scaled);
					aStudent.setImagePath(path);
					return true;
				} else {
					return false;
				}
			} catch (Exception e) {
				e.printStackTrace();
				return false;
			}
		}
		
		@Override
		protected void onPostExecute(Boolean result) {
			hideProgressDialog();
			if (result) {
				int dstWidth = image.getWidth();
				int dstHeight = image.getHeight();
				Bitmap src = BitmapFactory.decodeFile(aStudent.getImagePath());
				Bitmap scaled = Bitmap.createScaledBitmap(src, dstWidth, dstHeight, true);
				image.setImageBitmap(scaled);
			} else {
				Toast.makeText(NotesScreen.this, R.string.add_photo_error, Toast.LENGTH_SHORT).show();
			}
		}
		
		public String getRealPathFromURI(Uri contentUri) {
		    String result = "";
		    try {
		    	String[] proj = { MediaStore.Images.Media.DATA };
		    	Cursor c = NotesScreen.this.getContentResolver().query(contentUri, proj, null, null, null);
			    if (c != null && c.moveToFirst()) {
			    	result = c.getString(c.getColumnIndex(MediaStore.Images.Media.DATA));
			    }
			    if (c != null)
			    	c.close();
		    } catch (Exception e) {
				e.printStackTrace();
			}
		    return result;
		}
		
		protected String saveImage(Bitmap image) throws FileNotFoundException, IOException {
			File imageDir = new File(Environment.getExternalStorageDirectory(), "/easyassessment/photos/");
			if (!imageDir.exists())
				imageDir.mkdirs();
			
			String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.US).format(new Date());
			String filename = "IMAGE_" + timeStamp + ".png";
			
			File tmpFile = new File(imageDir, filename);
			FileOutputStream fos = new FileOutputStream(tmpFile);
			image.compress(Bitmap.CompressFormat.PNG, 0, fos);
			fos.close();
			return tmpFile.getAbsolutePath();
		}
		
	}
	
	class AddVideoTask extends AsyncTask<Void, Void, Boolean> {
		
		protected Intent data;
		
		public AddVideoTask(Intent data) {
			this.data = data;
		}
		
		@Override
		protected void onPreExecute() {
			showProgressDialog(getString(R.string.importing));
		}

		@Override
		protected Boolean doInBackground(Void... params) {
			try {
				String path = saveVideo(data.getData());
		    	aStudent.setVideoPath(path);
				return true;
			} catch (Exception e) {
				e.printStackTrace();
				return false;
			}
		}
		
		@Override
		protected void onPostExecute(Boolean result) {
			hideProgressDialog();
			if (result) {
				int dstWidth = video.getWidth();
				int dstHeight = video.getHeight();
				Bitmap src = ThumbnailUtils.createVideoThumbnail(aStudent.getVideoPath(), MediaStore.Video.Thumbnails.FULL_SCREEN_KIND);
				Bitmap scaled = Bitmap.createScaledBitmap(src, dstWidth, dstHeight, true);
				video.setImageBitmap(scaled);
			} else {
				Toast.makeText(NotesScreen.this, R.string.add_video_error, Toast.LENGTH_SHORT).show();
			}
		}
		
		protected String saveVideo(Uri uri) throws FileNotFoundException, IOException {
			String videoPath = getRealPathFromURI(uri);
			FileInputStream fis = new FileInputStream(videoPath);
			
			String ext = videoPath.substring(videoPath.lastIndexOf('.'));
			String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.US).format(new Date());
			String filename = "VIDEO_" + timeStamp + ext;
			
			File videoDir = new File(Environment.getExternalStorageDirectory(), "/easyassessment/videos/");
			if (!videoDir.exists())
				videoDir.mkdirs();
			
			File tmpFile = new File(videoDir, filename);
			FileOutputStream fos = new FileOutputStream(tmpFile);
			byte[] buf = new byte[1024];
			int len;
			while ((len = fis.read(buf)) > 0)
				fos.write(buf, 0, len);
			fis.close();
			fos.close();
			return tmpFile.getAbsolutePath();
		}
		
		public String getRealPathFromURI(Uri contentUri) {
			String result = "";
			try {
				String[] proj = { MediaStore.Images.Media.DATA };
			    Cursor c = getContentResolver().query(contentUri, proj, null, null, null);
			    if (c != null && c.moveToFirst())
			    	result = c.getString(c.getColumnIndex(MediaStore.Images.Media.DATA));
			    if (c != null)
			    	c.close();
			} catch (Exception e) {
				e.printStackTrace();
			}
		    return result;
		}
		
	}

}
