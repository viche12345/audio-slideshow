package com.erraticduck.audioslideshow;

import android.content.Context;
import android.content.Intent;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.GestureDetector.OnGestureListener;
import android.widget.ImageSwitcher;

public class GestureImageSwitcher extends ImageSwitcher implements OnGestureListener {

	private Intent i = new Intent("com.erraticduck.audioslideshow");

	public GestureImageSwitcher(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public GestureImageSwitcher(Context context) {
		super(context);
	}

	@Override
	public boolean onDown(MotionEvent arg0) {
		return false;
	}

	@Override
	public boolean onFling(MotionEvent arg0, MotionEvent arg1, float arg2, float arg3) {
		Log.d(getClass().getName(), "fling!");
		i.removeExtra("action");
		if (arg2 < 0) {
			i.putExtra("action", SlideShow.BACKWARD);
		} else if (arg2 > 0) {
			i.putExtra("action", SlideShow.FORWARD);
		}
		getContext().sendBroadcast(i);
		return true;
	}

	@Override
	public void onLongPress(MotionEvent arg0) {

	}

	@Override
	public boolean onScroll(MotionEvent arg0, MotionEvent arg1, float arg2, float arg3) {
		return false;
	}

	@Override
	public void onShowPress(MotionEvent arg0) {

	}

	@Override
	public boolean onSingleTapUp(MotionEvent arg0) {
		return false;
	}

}
