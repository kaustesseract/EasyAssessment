package com.thepegeekapps.easyassessment.screen;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.List;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.Window;
import android.widget.TextView;
import android.widget.Toast;

import com.dropbox.client2.DropboxAPI;
import com.dropbox.client2.android.AndroidAuthSession;
import com.dropbox.client2.session.AccessTokenPair;
import com.dropbox.client2.session.AppKeyPair;
import com.dropbox.client2.session.Session.AccessType;
import com.dropbox.client2.session.TokenPair;
import com.thepegeekapps.easyassessment.R;
import com.thepegeekapps.easyassessment.database.DatabaseManager;
import com.thepegeekapps.easyassessment.model.EntryRecord;
import com.thepegeekapps.easyassessment.storage.EntryHolder;

public abstract class BaseScreen extends Activity {
	
	public static final int DOWNLOAD_FROM_DROPBOX_REQUEST_CODE = 0;
	
	public static final String APP_KEY = "80b6mzr2ltk2jrb";
    public static final String APP_SECRET = "rboj1003akd4y8u";
    public static final String ACCOUNT_PREFS_NAME = "prefs";
    public static final String ACCESS_KEY_NAME = "ACCESS_KEY";
    public static final String ACCESS_SECRET_NAME = "ACCESS_SECRET";
    public static final AccessType ACCESS_TYPE = AccessType.DROPBOX;    
    
    protected DropboxAPI<AndroidAuthSession> mApi;
	
	protected DatabaseManager dbManager;
	protected ProgressDialog progressDialog;
	
	protected boolean loggedIn;
	protected boolean showDropboxDialog;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		dbManager = DatabaseManager.getInstance(this);
		
		AndroidAuthSession session = buildSession();
        mApi = new DropboxAPI<AndroidAuthSession>(session);      
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
                
                if (showDropboxDialog) {
                	showDropboxDialog = false;
                	showMyDropboxDialog();
                }
            } catch (IllegalStateException e) {
                Toast.makeText(this, "Couldn't authenticate with Dropbox:" + e.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
                e.printStackTrace();
            }
        }
    }
	
	protected void showMyDropboxDialog() {
		Intent intent = new Intent(this, DropboxScreen.class);
		startActivityForResult(intent, DOWNLOAD_FROM_DROPBOX_REQUEST_CODE);
	}
	
	protected void addFromCsv() {
		ConnectivityManager conManager = (ConnectivityManager) getSystemService(CONNECTIVITY_SERVICE);
		NetworkInfo info = conManager.getActiveNetworkInfo();
		if (info == null || info.getState() != NetworkInfo.State.CONNECTED) {
			Toast.makeText(this, R.string.no_connection, Toast.LENGTH_SHORT).show();
		} else {
			if (loggedIn) {
				showMyDropboxDialog();
            } else {
            	showDropboxDialog = true;
                mApi.getSession().startAuthentication(this);
            }
		}
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (requestCode == DOWNLOAD_FROM_DROPBOX_REQUEST_CODE && resultCode == RESULT_OK) {
			new ImportItemsTask().execute((Void[] ) null);
		}
	}
	
	protected void processItems(String items) {
		// override this in subclasses
	}
	
	@Override
	public void setTitle(int titleId) {
		TextView title = (TextView) findViewById(R.id.title);
		if (title != null)
			title.setText(titleId);
	}	
	
	@Override
	public void setTitle(CharSequence strTitle) {
		TextView title = (TextView) findViewById(R.id.title);
		if (title != null)
			title.setText(strTitle);
	}
	
	protected void showProgressDialog(String message) {
		if (progressDialog == null) {
			 progressDialog = new ProgressDialog(BaseScreen.this);
			 progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
		 }
		 progressDialog.setMessage(message);
		 progressDialog.show();
	}
	
	protected void hideProgressDialog() {
		if (progressDialog != null && progressDialog.isShowing()) {
			try {
				progressDialog.dismiss();
			} catch (IllegalArgumentException e) {
				// activity is destroyed, so dialog is not attached to window 
				e.printStackTrace();
			}
		}
	}
	
	protected AndroidAuthSession buildSession() {
        AppKeyPair appKeyPair = new AppKeyPair(APP_KEY, APP_SECRET);
        AndroidAuthSession session;

        String[] stored = getKeys();
        if (stored != null) {
            AccessTokenPair accessToken = new AccessTokenPair(stored[0], stored[1]);
            session = new AndroidAuthSession(appKeyPair, ACCESS_TYPE, accessToken);
        } else {
            session = new AndroidAuthSession(appKeyPair, ACCESS_TYPE);
        }

        return session;
    }
	
	protected String[] getKeys() {
        SharedPreferences prefs = getSharedPreferences(ACCOUNT_PREFS_NAME, 0);
        String key = prefs.getString(ACCESS_KEY_NAME, null);
        String secret = prefs.getString(ACCESS_SECRET_NAME, null);
        if (key != null && secret != null) {
        	String[] ret = new String[2];
        	ret[0] = key;
        	ret[1] = secret;
        	return ret;
        } else {
        	return null;
        }
    }

    protected void storeKeys(String key, String secret) {
        SharedPreferences prefs = getSharedPreferences(ACCOUNT_PREFS_NAME, 0);
        Editor edit = prefs.edit();
        edit.putString(ACCESS_KEY_NAME, key);
        edit.putString(ACCESS_SECRET_NAME, secret);
        edit.commit();
    }

    protected void clearKeys() {
        SharedPreferences prefs = getSharedPreferences(ACCOUNT_PREFS_NAME, 0);
        Editor edit = prefs.edit();
        edit.clear();
        edit.commit();
    }
    
    class ImportItemsTask extends AsyncTask<Void, Void, Void> {
		
		protected String items = "";
		
		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			showProgressDialog(getString(R.string.importing));
		}

		@Override
		protected Void doInBackground(Void... params) {
			List<EntryRecord> records = EntryHolder.getInstance(mApi).getDownloadedEntries();
			if (records != null && !records.isEmpty()) {
				for (EntryRecord record : records) {
					try {
						File f = new File(record.getDownloadedPath());
						if (f != null && f.exists()) {
							InputStream is = new FileInputStream(f);
							Reader r = new InputStreamReader(is);
							BufferedReader br = new BufferedReader(r);
							String name;
							while ((name = br.readLine()) != null) {
								items += name + ',';
							}
							is.close();
							r.close();
							br.close();
						}
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			}
			return null;
		}
		
		@Override
		protected void onPostExecute(Void result) {
			super.onPostExecute(result);
			EntryHolder entryHolder = EntryHolder.getInstance(mApi);
			entryHolder.setCurrent(entryHolder.getRoot());
			entryHolder.uncheckEntries(entryHolder.getRoot());
			hideProgressDialog();
			processItems(items);
		}
		
	}

}
