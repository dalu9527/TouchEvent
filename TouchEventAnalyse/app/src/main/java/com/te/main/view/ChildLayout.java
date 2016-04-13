package com.te.main.view;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.widget.LinearLayout;

import com.te.main.util.SharedPreferencesUtil;
import com.te.main.util.TouchEventUtil;

/**
 * 子布局
 */
public class ChildLayout extends LinearLayout {
	private static final String TAG = ChildLayout.class.getName();
    public static final String CHILD_SUPER_DISPATCH = "child_super_dispatch";
    public static final String CHILD_SUPER_INTERCEPT = "child_super_intercept";
    public static final String CHILD_SUPER_TOUCH_EVENT = "child_super_touch_event";
    public static final String CHILD_DISPATCH = "child_dispatch";
    public static final String CHILD_INTERCEPT = "child_intercept";
    public static final String CHILD_TOUCH_EVENT = "child_touch_event";

    private Context mContext;

	public ChildLayout(Context context) {
		super(context);
        mContext = context;
	}

	public ChildLayout(Context context, AttributeSet attrs) {
		super(context, attrs);
        mContext = context;
	}

	public boolean dispatchTouchEvent(MotionEvent ev) {
		Log.e(TAG, "ChildLayout | dispatchTouchEvent --> " + TouchEventUtil.getTouchAction(ev.getAction()));
		if(SharedPreferencesUtil.getBoolean(mContext,CHILD_SUPER_DISPATCH,true)){
			return super.dispatchTouchEvent(ev);
		}else {
			return SharedPreferencesUtil.getBoolean(mContext,CHILD_DISPATCH,true);
		}
	}

	public boolean onInterceptTouchEvent(MotionEvent ev) {
		Log.i(TAG, "ChildLayout | onInterceptTouchEvent --> " + TouchEventUtil.getTouchAction(ev.getAction()));
		if(SharedPreferencesUtil.getBoolean(mContext,CHILD_SUPER_INTERCEPT,true)){
			return super.onInterceptTouchEvent(ev);
		}else {
			return SharedPreferencesUtil.getBoolean(mContext,CHILD_INTERCEPT,true);
		}
	}

	public boolean onTouchEvent(MotionEvent ev) {
		Log.d(TAG, "ChildLayout | onTouchEvent --> " + TouchEventUtil.getTouchAction(ev.getAction()));
		if(SharedPreferencesUtil.getBoolean(mContext,CHILD_SUPER_TOUCH_EVENT,true)){
			return super.onTouchEvent(ev);
		}else {
			return SharedPreferencesUtil.getBoolean(mContext,CHILD_TOUCH_EVENT,true);
		}
	}

}
