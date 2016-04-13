package com.te.main;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Spinner;

import com.te.main.util.SharedPreferencesUtil;
import com.te.main.util.TouchEventUtil;
import com.te.main.view.ChildLayout;
import com.te.main.view.ParentLayout;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = MainActivity.class.getName();
    private Spinner mMainDispatchSpinner = null;  //MainActivity
    private Spinner mMainTouchSpinner = null;  //MainActivity
    private Spinner mChildDispatchSpinner = null;     //ChildLayout
    private Spinner mChildInterceptSpinner = null;     //ChildLayout
    private Spinner mChildTouchSpinner = null;     //ChildLayout
    private Spinner mParentDispatchSpinner = null;    //ParentLayout
    private Spinner mParentInterceptSpinner = null;    //ParentLayout
    private Spinner mParentTouchSpinner = null;    //ParentLayout

    private static final String MAIN_SUPER_DISPATCH = "main_super_dispatch";
    private static final String MAIN_SUPER_TOUCH_EVENT = "main_super_touch_event";
    private static final String MAIN_DISPATCH = "main_dispatch";
    private static final String MAIN_TOUCH_EVENT = "main_touch_event";

    private Context mContext;


    private ParentLayout mParentLayout;
    private ChildLayout mChildLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mContext = this;
        setSpinner();
        mParentLayout = new ParentLayout(this);
        mChildLayout = new ChildLayout(this);
    }

    /**
     * 设置下拉框
     */
    private void setSpinner() {
        mMainDispatchSpinner = (Spinner)findViewById(R.id.sp_main_dispatch);
        mMainTouchSpinner = (Spinner)findViewById(R.id.sp_main_touch);

        mParentDispatchSpinner = (Spinner)findViewById(R.id.sp_parent_dispatch);
        mParentInterceptSpinner = (Spinner)findViewById(R.id.sp_parent_intercept);
        mParentTouchSpinner = (Spinner)findViewById(R.id.sp_parent_touch);

        mChildDispatchSpinner = (Spinner)findViewById(R.id.sp_child_dispatch);
        mChildInterceptSpinner = (Spinner)findViewById(R.id.sp_child_intercept);
        mChildTouchSpinner = (Spinner)findViewById(R.id.sp_child_touch);


        mMainDispatchSpinner.setOnItemSelectedListener(mMainDispatchSelectListener);
        mMainTouchSpinner.setOnItemSelectedListener(mMainTouchSelectListener);

        mParentDispatchSpinner.setOnItemSelectedListener(mParentDispatchSelectListener);
        mParentInterceptSpinner.setOnItemSelectedListener(mParentInterceptSelectListener);
        mParentTouchSpinner.setOnItemSelectedListener(mParentTouchSelectListener);

        mChildDispatchSpinner.setOnItemSelectedListener(mChildDispatchSelectListener);
        mChildInterceptSpinner.setOnItemSelectedListener(mChildInterceptSelectListener);
        mChildTouchSpinner.setOnItemSelectedListener(mChildTouchSelectListener);
    }

    private AdapterView.OnItemSelectedListener mMainDispatchSelectListener = new AdapterView.OnItemSelectedListener() {
        @Override
        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
            String[] mainDispatchSelect = getResources().getStringArray(R.array.main);
            switch (mainDispatchSelect[position]){
                case "super":
                    SharedPreferencesUtil.writeBoolean(mContext,MAIN_SUPER_DISPATCH,true);
                    break;
                case "true":
                    SharedPreferencesUtil.writeBoolean(mContext,MAIN_SUPER_DISPATCH,false);
                    SharedPreferencesUtil.writeBoolean(mContext,MAIN_DISPATCH,true);
                    break;
                case "false":
                    SharedPreferencesUtil.writeBoolean(mContext,MAIN_SUPER_DISPATCH,false);
                    SharedPreferencesUtil.writeBoolean(mContext,MAIN_DISPATCH,false);
                    break;
            }
        }

        @Override
        public void onNothingSelected(AdapterView<?> parent) {

        }
    };
    private AdapterView.OnItemSelectedListener mMainTouchSelectListener = new AdapterView.OnItemSelectedListener() {
        @Override
        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
            String[] mainTouchSelect = getResources().getStringArray(R.array.main);
            switch (mainTouchSelect[position]){
                case "super":
                    SharedPreferencesUtil.writeBoolean(mContext,MAIN_SUPER_TOUCH_EVENT,true);
                    break;
                case "true":
                    SharedPreferencesUtil.writeBoolean(mContext,MAIN_SUPER_TOUCH_EVENT,false);
                    SharedPreferencesUtil.writeBoolean(mContext,MAIN_TOUCH_EVENT,true);
                    break;
                case "false":
                    SharedPreferencesUtil.writeBoolean(mContext,MAIN_SUPER_TOUCH_EVENT,false);
                    SharedPreferencesUtil.writeBoolean(mContext,MAIN_TOUCH_EVENT,false);
                    break;
            }
        }

        @Override
        public void onNothingSelected(AdapterView<?> parent) {

        }
    };

    private AdapterView.OnItemSelectedListener mParentDispatchSelectListener = new AdapterView.OnItemSelectedListener() {
        @Override
        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
            String[] parentDispatchSelect = getResources().getStringArray(R.array.parent);
            switch (parentDispatchSelect[position]){
                case "super":
                    SharedPreferencesUtil.writeBoolean(mContext, ParentLayout.PARENT_SUPER_DISPATCH, true);
                    break;
                case "true":
                    SharedPreferencesUtil.writeBoolean(mContext, ParentLayout.PARENT_SUPER_DISPATCH, false);
                    SharedPreferencesUtil.writeBoolean(mContext, ParentLayout.PARENT_DISPATCH, true);
                    break;
                case "false":
                    SharedPreferencesUtil.writeBoolean(mContext, ParentLayout.PARENT_SUPER_DISPATCH, false);
                    SharedPreferencesUtil.writeBoolean(mContext, ParentLayout.PARENT_DISPATCH, false);
                    break;
            }
        }

        @Override
        public void onNothingSelected(AdapterView<?> parent) {

        }
    };
    private AdapterView.OnItemSelectedListener mParentInterceptSelectListener = new AdapterView.OnItemSelectedListener() {
        @Override
        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
            String[] parentInterceptSelect = getResources().getStringArray(R.array.parent);
            switch (parentInterceptSelect[position]){
                case "super":
                    SharedPreferencesUtil.writeBoolean(mContext, ParentLayout.PARENT_SUPER_INTERCEPT, true);
                    break;
                case "true":
                    SharedPreferencesUtil.writeBoolean(mContext, ParentLayout.PARENT_SUPER_INTERCEPT, false);
                    SharedPreferencesUtil.writeBoolean(mContext, ParentLayout.PARENT_INTERCEPT, true);
                    break;
                case "false":
                    SharedPreferencesUtil.writeBoolean(mContext, ParentLayout.PARENT_SUPER_INTERCEPT, false);
                    SharedPreferencesUtil.writeBoolean(mContext, ParentLayout.PARENT_INTERCEPT, false);
                    break;
            }
        }

        @Override
        public void onNothingSelected(AdapterView<?> parent) {

        }
    };
    private AdapterView.OnItemSelectedListener mParentTouchSelectListener = new AdapterView.OnItemSelectedListener() {
        @Override
        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
            String[] parentTouchSelect = getResources().getStringArray(R.array.parent);
            switch (parentTouchSelect[position]){
                case "super":
                    SharedPreferencesUtil.writeBoolean(mContext, ParentLayout.PARENT_SUPER_TOUCH_EVENT, true);
                    break;
                case "true":
                    SharedPreferencesUtil.writeBoolean(mContext, ParentLayout.PARENT_SUPER_TOUCH_EVENT, false);
                    SharedPreferencesUtil.writeBoolean(mContext, ParentLayout.PARENT_TOUCH_EVENT, true);
                    break;
                case "false":
                    SharedPreferencesUtil.writeBoolean(mContext, ParentLayout.PARENT_SUPER_TOUCH_EVENT, false);
                    SharedPreferencesUtil.writeBoolean(mContext, ParentLayout.PARENT_TOUCH_EVENT, false);
                    break;
            }
        }

        @Override
        public void onNothingSelected(AdapterView<?> parent) {

        }
    };

    private AdapterView.OnItemSelectedListener mChildDispatchSelectListener = new AdapterView.OnItemSelectedListener() {
        @Override
        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
            String[] childDispatchSelect = getResources().getStringArray(R.array.child);
            switch (childDispatchSelect[position]){
                case "super":
                    SharedPreferencesUtil.writeBoolean(mContext, ChildLayout.CHILD_SUPER_DISPATCH, true);
                    break;
                case "true":
                    SharedPreferencesUtil.writeBoolean(mContext, ChildLayout.CHILD_SUPER_DISPATCH, false);
                    SharedPreferencesUtil.writeBoolean(mContext, ChildLayout.CHILD_DISPATCH, true);
                    break;
                case "false":
                    SharedPreferencesUtil.writeBoolean(mContext, ChildLayout.CHILD_SUPER_DISPATCH, false);
                    SharedPreferencesUtil.writeBoolean(mContext, ChildLayout.CHILD_DISPATCH, false);
                    break;
            }
        }

        @Override
        public void onNothingSelected(AdapterView<?> parent) {

        }
    };
    private AdapterView.OnItemSelectedListener mChildInterceptSelectListener = new AdapterView.OnItemSelectedListener() {
        @Override
        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
            String[] childInterceptSelect = getResources().getStringArray(R.array.child);
            switch (childInterceptSelect[position]){
                case "super":
                    SharedPreferencesUtil.writeBoolean(mContext, ChildLayout.CHILD_SUPER_INTERCEPT, true);
                    break;
                case "true":
                    SharedPreferencesUtil.writeBoolean(mContext, ChildLayout.CHILD_SUPER_INTERCEPT, false);
                    SharedPreferencesUtil.writeBoolean(mContext, ChildLayout.CHILD_INTERCEPT, true);
                    break;
                case "false":
                    SharedPreferencesUtil.writeBoolean(mContext, ChildLayout.CHILD_SUPER_INTERCEPT, false);
                    SharedPreferencesUtil.writeBoolean(mContext, ChildLayout.CHILD_INTERCEPT, false);
                    break;
            }
        }

        @Override
        public void onNothingSelected(AdapterView<?> parent) {

        }
    };
    private AdapterView.OnItemSelectedListener mChildTouchSelectListener = new AdapterView.OnItemSelectedListener() {
        @Override
        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
            String[] childTouchSelect = getResources().getStringArray(R.array.child);
            switch (childTouchSelect[position]){
                case "super":
                    SharedPreferencesUtil.writeBoolean(mContext, ChildLayout.CHILD_SUPER_TOUCH_EVENT, true);
                    break;
                case "true":
                    SharedPreferencesUtil.writeBoolean(mContext, ChildLayout.CHILD_SUPER_TOUCH_EVENT, false);
                    SharedPreferencesUtil.writeBoolean(mContext, ChildLayout.CHILD_TOUCH_EVENT, true);
                    break;
                case "false":
                    SharedPreferencesUtil.writeBoolean(mContext, ChildLayout.CHILD_SUPER_TOUCH_EVENT, false);
                    SharedPreferencesUtil.writeBoolean(mContext, ChildLayout.CHILD_TOUCH_EVENT, false);
                    break;
            }
        }

        @Override
        public void onNothingSelected(AdapterView<?> parent) {

        }
    };


    public boolean dispatchTouchEvent(MotionEvent ev) {
        Log.w(TAG, "MainActivity | dispatchTouchEvent --> " + TouchEventUtil.getTouchAction(ev.getAction()));
        if(SharedPreferencesUtil.getBoolean(mContext,MAIN_SUPER_DISPATCH,true)){
            return super.dispatchTouchEvent(ev);
        }else {
            return SharedPreferencesUtil.getBoolean(mContext,MAIN_DISPATCH,true);
        }
    }

    public boolean onTouchEvent(MotionEvent ev) {
        Log.w(TAG, "MainActivity | onTouchEvent --> " + TouchEventUtil.getTouchAction(ev.getAction()));
        if(SharedPreferencesUtil.getBoolean(mContext,MAIN_SUPER_TOUCH_EVENT,true)){
            return super.onTouchEvent(ev);
        }else {
            return SharedPreferencesUtil.getBoolean(mContext,MAIN_TOUCH_EVENT,true);
        }
    }
}
