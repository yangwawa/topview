package com.yangwawa.topview;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.graphics.PixelFormat;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.yangwawa.topview.hook.WindowManagerHook;

import java.util.LinkedList;

public class TopView implements Application.ActivityLifecycleCallbacks {

    private static final String TAG = "TopView";
    private Context mContext = null;
    private Application mApp = null;
    private Activity mCurrentActivity = null;

    private LinkedList<View> mViews = new LinkedList<>();
    WindowManagerHook mWindowMgrHook = null;

    @Override
    public void onActivityCreated(@NonNull Activity activity, @Nullable Bundle bundle) {
        Log.v(TAG, "onActivityCreated" + activity);

    }

    @Override
    public void onActivityStarted(@NonNull Activity activity) {
        Log.v(TAG, "onActivityStarted" + activity);
    }

    @Override
    public void onActivityResumed(@NonNull Activity activity) {
        Log.v(TAG, "onActivityResumed" + activity);
        mCurrentActivity = activity;
        attchAll(activity);
    }

    @Override
    public void onActivityPaused(@NonNull Activity activity) {
        Log.v(TAG, "onActivityPaused" + activity);
        detachAll(activity);
    }

    @Override
    public void onActivityStopped(@NonNull Activity activity) {
        Log.v(TAG, "onActivityStopped" + activity);

    }

    @Override
    public void onActivitySaveInstanceState(@NonNull Activity activity, @NonNull Bundle bundle) {
        Log.v(TAG, "onActivitySaveInstanceState" + activity);
    }

    @Override
    public void onActivityDestroyed(@NonNull Activity activity) {
        Log.v(TAG, "onActivityDestroyed" + activity);
    }

    private static class Holder{
        private static TopView sHolder = new TopView();
    }
    public static TopView getInstance(){
        return Holder.sHolder;
    }
    private TopView(){}

    public void init(Application application){
        mApp = application;
        mContext = application.getApplicationContext();
        mApp.registerActivityLifecycleCallbacks(this);

        mWindowMgrHook = new WindowManagerHook();
        mWindowMgrHook.hook();
    }

    public void addView(View view){
        mViews.add(view);
        detachAll(mCurrentActivity);
        attchAll(mCurrentActivity);
    }

    private void attchAll(Activity activity){
        if(activity == null){
            return;
        }
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
    }

    private void detachAll(Activity activity){
        for(View view : mViews){
            try {
                activity.getWindowManager().removeView(view);
            }catch (Exception e){
                //TODO:
            }
        }
    }

    public void removeView(View view){
        mViews.remove(view);
    }
}
