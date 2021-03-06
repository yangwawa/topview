package com.yangwawa.topview;

import android.app.Activity;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;

import com.yangwawa.topview.internal.WindowManagerHook;
import com.yangwawa.topview.internal.ActivityUtils;

import java.util.LinkedList;

public class TopView {

    private static final String TAG = "TopView";
    private Activity mCurrentActivity = null;
    private boolean mIsAttching = false;

    private LinkedList<ViewWrapper> mViews = new LinkedList<>();
    WindowManagerHook mWindowMgrHook = null;

    private static class Holder{
        private static TopView sHolder = new TopView();
    }
    public static TopView getInstance(){
        return Holder.sHolder;
    }
    private TopView(){
       init();
    }

    private void init(){
        mWindowMgrHook = new WindowManagerHook();
        mWindowMgrHook.hook();
    }

    public void addView(View view, WindowManager.LayoutParams lp){
        ViewWrapper vw = new ViewWrapper(view, lp);
        mViews.add(vw);
        Activity current = ActivityUtils.getTopActivity();
        if(mCurrentActivity != current){
            detachAll(mCurrentActivity);
        }
        mCurrentActivity = current;
        attchAll(mCurrentActivity);
    }

    public void attchAll(Activity activity){
        Log.v(TAG, "attchAll::" + activity);
        if(activity == null || mViews.isEmpty()){
            return;
        }
        mIsAttching = true;
        for(ViewWrapper vw : mViews){
            WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
            lp.copyFrom(vw.lp);
            activity.getWindowManager().addView(vw.view, lp);
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
        for(ViewWrapper vw : mViews){
            try {
                activity.getWindowManager().removeView(vw.view);
            }catch (Exception e){
                e.printStackTrace();
            }
        }
    }

    public void removeView(View view){
        if(mCurrentActivity != null){
            try {
                mCurrentActivity.getWindowManager().removeView(view);
            }catch (Exception e){
                e.printStackTrace();
            }
        }
        for(ViewWrapper vw : mViews){
            if(vw.view == view){
                mViews.remove(vw);
                break;
            }
        }
    }

    public boolean isAttching() {
        return mIsAttching;
    }

    public Activity getCurrentActivity(){
        return mCurrentActivity;
    }

    class ViewWrapper{
        final View view;
        final WindowManager.LayoutParams lp;
        ViewWrapper(View v, WindowManager.LayoutParams lp) {
            this.view = v;
            this.lp = lp;
        }
    }
}
