package com.yangwawa.topview;

import android.app.Activity;
import android.graphics.PixelFormat;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;

import com.blankj.utilcode.util.ActivityUtils;
import com.yangwawa.topview.hook.WindowManagerHook;

import java.util.LinkedList;

public class TopView {

    private static final String TAG = "TopView";
    private Activity mCurrentActivity = null;
    private boolean mIsAttching = false;

    private LinkedList<View> mViews = new LinkedList<>();
    WindowManagerHook mWindowMgrHook = null;

    private static class Holder{
        private static TopView sHolder = new TopView();
    }
    public static TopView getInstance(){
        return Holder.sHolder;
    }
    private TopView(){}

    public void init(){
        mWindowMgrHook = new WindowManagerHook();
        mWindowMgrHook.hook();
    }

    public void addView(View view){
        mViews.add(view);
        if(mCurrentActivity == null){
            mCurrentActivity = ActivityUtils.getTopActivity();
        }else {
            detachAll(mCurrentActivity);
        }
        attchAll(mCurrentActivity);
    }

    public void attchAll(Activity activity){
        Log.v(TAG, "attchAll::" + activity);
        if(activity == null || mViews.isEmpty()){
            return;
        }
        mIsAttching = true;
        for(View view : mViews){
            WindowManager.LayoutParams lp = new WindowManager.LayoutParams(WindowManager.LayoutParams.WRAP_CONTENT,
                    WindowManager.LayoutParams.WRAP_CONTENT);
            lp.width = 300;
            lp.height = 100;
            lp.type = WindowManager.LayoutParams.TYPE_APPLICATION;
            lp.flags = WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE;
            lp.format = PixelFormat.TRANSLUCENT;
            activity.getWindowManager().addView(view, lp);
        }
        mCurrentActivity = activity;
        mIsAttching = false;
    }
    public void detachAll(){
        if(mCurrentActivity != null){
            detachAll(mCurrentActivity);
        }
    }

    public void detachAll(Activity activity){
        Log.v(TAG, "detachAll::" + activity);
        for(View view : mViews){
            try {
                activity.getWindowManager().removeView(view);
            }catch (Exception e){
                //TODO:
                e.printStackTrace();
            }
        }
    }

    public void removeView(View view){
        mViews.remove(view);
    }

    public boolean isAttching() {
        return mIsAttching;
    }

    public Activity getCurrentActivity(){
        return mCurrentActivity;
    }
}
