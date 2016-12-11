package com.erraticduck.audioslideshow;

import java.io.File;
import java.io.FileFilter;

import android.app.Activity;
import android.app.DialogFragment;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

public class FileBrowser extends Activity {

	private File[] listOfFiles;
	private File[] listOfAudioFolders;

	private ListView mFileList;
	private Adapter mAdapter;

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);
		mFileList = (ListView) findViewById(R.id.fileList);

		mAdapter = new Adapter();
		mFileList.setAdapter(mAdapter);
		mFileList.setOnItemClickListener(new AdapterView.OnItemClickListener() {

			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
				final File clickedFolderName = listOfFiles[arg2];
				if (listOfAudioFolders.length > 0) {
					DialogFragment d = new AudioSelectionDialog();
					Bundle args = new Bundle(2);
					args.putStringArray("listOfAudioFolders",
							fileArrayToStringArray(listOfAudioFolders));
					args.putString("clickedFolderName", clickedFolderName.getAbsolutePath());
					d.setArguments(args);
					d.show(getFragmentManager(), "audioselection");
				} else {
					Intent i = new Intent(getApplicationContext(), SlideShow.class);
					i.putExtra("path", clickedFolderName.getAbsolutePath());
					startActivity(i);
				}
			}
		});
	}

	@Override
	protected void onStart() {
		super.onStart();
		init();
	}

	private void init() {
		File folder;
		folder = new File(getImagesLocation());
		listOfFiles = folder.listFiles(new FileFilter() {

			@Override
			public boolean accept(File pathname) {
				return pathname.isDirectory() && !pathname.getName().contains("audio")
						&& !pathname.getName().equalsIgnoreCase(".android_secure")
						&& !pathname.getName().equalsIgnoreCase("lost.dir")
						&& !pathname.getName().equalsIgnoreCase("android");
			}

		});
		listOfAudioFolders = folder.listFiles(new FileFilter() {

			@Override
			public boolean accept(File pathname) {
				return pathname.isDirectory() && pathname.getName().contains("audio");
			}

		});
		mAdapter.notifyDataSetChanged();
	}

	public String getImagesLocation() {
		return PreferenceManager.getDefaultSharedPreferences(this).getString("images_location",
				Environment.getExternalStorageDirectory().getAbsolutePath());
	}

	private class Adapter extends BaseAdapter {

		public int getCount() {
			return listOfFiles == null ? 0 : listOfFiles.length;
		}

		public Object getItem(int arg0) {
			return listOfFiles == null ? null : listOfFiles[arg0];
		}

		public long getItemId(int arg0) {
			return arg0;
		}

		public View getView(int arg0, View arg1, ViewGroup arg2) {
			TextView name = new TextView(getApplicationContext());
			name.setTextSize(20);
			name.setPadding(15, 15, 15, 15);
			File f = (File) getItem(arg0);
			name.setText(f.getName());
			return name;
		}

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.activity_main, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
			case R.id.menu_settings:
				Intent i = new Intent(getApplicationContext(), SettingsActivity.class);
				startActivity(i);
				return true;
			case R.id.menu_refresh:
				init();
				return true;
		}
		return super.onOptionsItemSelected(item);
	}

	public static String[] fileArrayToStringArray(File[] files) {
		String[] fileNames = new String[files.length];
		for (int i = 0; i < files.length; i++) {
			fileNames[i] = files[i].getAbsolutePath();
		}
		return fileNames;
	}

}
