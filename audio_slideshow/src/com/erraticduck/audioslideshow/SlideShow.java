package com.erraticduck.audioslideshow;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.widget.ImageSwitcher;
import android.widget.ImageView;
import android.widget.Toast;
import android.widget.ViewSwitcher.ViewFactory;

public class SlideShow extends Activity implements ViewFactory {

	public static final int BACKWARD = 0;
	public static final int FORWARD = 1;

	private File listOfPics[];
	private ArrayList<CharSequence> audioList;
	private String pathString;

	private static int index;
	public final int IMAGE_MAX_SIZE = 1280;
	public final int TRANSITION_TIME = 3000;
	private Timer slideshowTimer;
	private boolean timerStopped = true;

	private Animation imageFadeIn;
	private Animation imageFadeOut;

	private ImageSwitcher mSwitcher;

	private MediaPlayer mp = new MediaPlayer();

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.slideshow);

		index = 0;

		imageFadeIn = new AlphaAnimation(0, 1);
		imageFadeIn.setDuration(700);
		imageFadeOut = new AlphaAnimation(1, 0);
		imageFadeOut.setDuration(700);

		pathString = getIntent().getStringExtra("path");
		Log.i("SlideShow", "Selected pictures path: " + pathString);
		listOfPics = new File(pathString).listFiles();
		if (listOfPics.length == 0) {
			Toast.makeText(this, "No images found!", Toast.LENGTH_LONG).show();
			finish();
		}
		Collections.shuffle(Arrays.asList(listOfPics));

		mSwitcher = (ImageSwitcher) findViewById(R.id.imageSwitcher1);
		mSwitcher.setFactory(this);
		mSwitcher.setInAnimation(imageFadeIn);
		mSwitcher.setOutAnimation(imageFadeOut);

		startAudio();
	}

	public class Receiver extends BroadcastReceiver {

		@Override
		public void onReceive(Context arg0, Intent arg1) {
			if (arg1.getAction().equals("com.erraticduck.audioslideshow")
					&& arg1.hasExtra("action")) {
				switch (arg1.getIntExtra("action", 0)) {
				case BACKWARD:
					previousImage(null);
					break;
				case FORWARD:
					nextImage(null);
					break;
				}
				if (!timerStopped)
					stopTimer();
			}
		}
	}

	public void startAudio() {
		final Random gen = new Random();

		if (getIntent().hasExtra("audio_files")) {
			audioList = getIntent().getCharSequenceArrayListExtra("audio_files");
		}

		if (audioList != null && audioList.size() > 0) {
			playFile(audioList.get(gen.nextInt(audioList.size())) + "");
			mp.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {

				public void onCompletion(MediaPlayer mp) {
					playFile(audioList.get(gen.nextInt(audioList.size())) + "");
				}
			});
		}
	}

	private void playFile(String path) {
		try {
			mp.reset();
			mp.setDataSource(path);
			mp.prepare();
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		} catch (IllegalStateException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		mp.start();
	}

	@Override
	protected void onResume() {
		super.onResume();
		startOrResumeTimer();
	}

	private void startOrResumeTimer() {
		slideshowTimer = new Timer();
		slideshowTimer.schedule(new TimerTask() {

			@Override
			public void run() {
				nextImage(null);
			}
		}, 0, TRANSITION_TIME);
		timerStopped = false;
		Toast.makeText(getApplicationContext(), "Slide show started!", Toast.LENGTH_SHORT).show();
		findViewById(R.id.buttonContainer).setVisibility(View.GONE);
	}

	private void stopTimer() {
		slideshowTimer.cancel();
		timerStopped = true;
		Toast.makeText(getApplicationContext(), "Slide show stopped", Toast.LENGTH_SHORT).show();
		findViewById(R.id.buttonContainer).setVisibility(View.VISIBLE);
	}

	public void onImageClick(View v) {
		if (!timerStopped)
			stopTimer();
		else
			startOrResumeTimer();
	}

	public void viewInGallery(View v) {
		Intent intent = new Intent();
		intent.setAction(android.content.Intent.ACTION_VIEW);
		intent.setDataAndType(Uri.fromFile(listOfPics[index]), "image/png");
		startActivity(intent);
	}

	public void previousImage(View v) {
		index--;
		if (index < 0) {
			index = listOfPics.length - 1;
			return;
		}
		switchImage(index);
	}

	public void nextImage(View v) {
		index++;
		if (index == listOfPics.length)
			index = 0;
		switchImage(index);
	}

	public void switchImage(final int index) {
		runOnUiThread(new Runnable() {

			public void run() {
				Bitmap b = decodeFile(listOfPics[index]);
				BitmapDrawable drawable = new BitmapDrawable(getResources(), b);
				mSwitcher.setImageDrawable(drawable);
			}
		});
	}

	private Bitmap decodeFile(File f) {
		Bitmap b = null;
		try {
			// Decode image size
			BitmapFactory.Options o = new BitmapFactory.Options();
			o.inJustDecodeBounds = true;

			FileInputStream fis = new FileInputStream(f);
			BitmapFactory.decodeStream(fis, null, o);
			fis.close();

			int scale = 1;
			if (o.outHeight > IMAGE_MAX_SIZE || o.outWidth > IMAGE_MAX_SIZE) {
				scale = (int) Math.pow(
						2,
						(int) Math.round(Math.log(IMAGE_MAX_SIZE
								/ (double) Math.max(o.outHeight, o.outWidth))
								/ Math.log(0.5)));
			}

			// Decode with inSampleSize
			BitmapFactory.Options o2 = new BitmapFactory.Options();
			o2.inSampleSize = scale;
			fis = new FileInputStream(f);
			b = BitmapFactory.decodeStream(fis, null, o2);
			fis.close();
		} catch (IOException e) {
		}
		return b;
	}

	public View makeView() {
		ImageView iView = new ImageView(this);
		iView.setScaleType(ImageView.ScaleType.FIT_CENTER);
		iView.setLayoutParams(new ImageSwitcher.LayoutParams(LayoutParams.MATCH_PARENT,
				LayoutParams.MATCH_PARENT));
		iView.setBackgroundColor(Color.BLACK);
		return iView;
	}

	@Override
	protected void onPause() {
		super.onPause();
		stopTimer();
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		mp.release();
	}

}
