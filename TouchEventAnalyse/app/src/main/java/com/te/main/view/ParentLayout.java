package com.te.main.view;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.widget.LinearLayout;

import com.te.main.util.SharedPreferencesUtil;
import com.te.main.util.TouchEventUtil;

/**
 * 父布局
 */
public class ParentLayout extends LinearLayout {
	private static final String TAG = ParentLayout.class.getName();
    public static final String PARENT_SUPER_DISPATCH = "parent_super_dispatch";
    public static final String PARENT_SUPER_INTERCEPT = "parent_super_intercept";
    public static final String PARENT_SUPER_TOUCH_EVENT = "parent_super_touch_event";
    public static final String PARENT_DISPATCH = "parent_dispatch";
    public static final String PARENT_INTERCEPT = "parent_intercept";
    public static final String PARENT_TOUCH_EVENT = "parent_touch_event";

    private Context mContext;

	public ParentLayout(Context context) {
		super(context);
        mContext = context;
	}

	public ParentLayout(Context context, AttributeSet attrs) {
		super(context, attrs);
        mContext = context;
	}

	public boolean dispatchTouchEvent(MotionEvent ev) {
		Log.e(TAG, "ParentLayout | dispatchTouchEvent --> " + TouchEventUtil.getTouchAction(ev.getAction()));
		if(SharedPreferencesUtil.getBoolean(mContext,PARENT_SUPER_DISPATCH,true)){
			return super.dispatchTouchEvent(ev);
		}else {
			return SharedPreferencesUtil.getBoolean(mContext,PARENT_DISPATCH,true);
		}
	}

	public boolean onInterceptTouchEvent(MotionEvent ev) {
		Log.i(TAG, "ParentLayout | onInterceptTouchEvent --> " + TouchEventUtil.getTouchAction(ev.getAction()));
		if(SharedPreferencesUtil.getBoolean(mContext,PARENT_SUPER_INTERCEPT,true)){
			return super.onInterceptTouchEvent(ev);
		}else {
			return SharedPreferencesUtil.getBoolean(mContext,PARENT_INTERCEPT,true);
		}
	}

	public boolean onTouchEvent(MotionEvent ev) {
		Log.d(TAG, "ParentLayout | onTouchEvent --> " + TouchEventUtil.getTouchAction(ev.getAction()));
		if(SharedPreferencesUtil.getBoolean(mContext,PARENT_SUPER_TOUCH_EVENT,true)){
			return super.onTouchEvent(ev);
		}else {
			return SharedPreferencesUtil.getBoolean(mContext,PARENT_TOUCH_EVENT,true);
		}
	}

}
