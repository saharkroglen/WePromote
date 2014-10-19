package com.wepromote.controls;

import com.wepromote.lib.Constants;

import android.content.Context;
import android.support.v4.view.MotionEventCompat;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.util.Log;
import android.view.GestureDetector;
import android.view.GestureDetector.SimpleOnGestureListener;
import android.view.MotionEvent;
import android.view.View;

public class PagingControllableViewPager extends ViewPager {

	
	private boolean enabled;

	public PagingControllableViewPager(Context context, AttributeSet attrs) {
	    super(context, attrs);
	    this.enabled = true;	    
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {
	    if (this.enabled) {
	        return super.onTouchEvent(event);
	    }

	    return false;
	}

	@Override
	public boolean onInterceptTouchEvent(MotionEvent event) {
	    if (this.enabled) {
	        return super.onInterceptTouchEvent(event);
	    }

	    return false;
	}

	public void setPagingEnabled(boolean enabled) {
		Log.v(Constants.TAG,String.format("Setup Paging is set to %s",enabled));
	    this.enabled = enabled;
	}
	
	
	
}
