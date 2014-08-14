package ee.tools.componentcalculator;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;

public class NonSwipeableViewPager extends ViewPager{

	boolean enabled;
	String tag = "Non Swipe-Able View Pager";
	
	public NonSwipeableViewPager(Context context, AttributeSet attrs) {
	    super(context, attrs);
	    this.enabled = true;
	}
	
	public NonSwipeableViewPager(Context context) {
		super(context);
		enabled = true;
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
	    this.enabled = enabled;
	}	
}