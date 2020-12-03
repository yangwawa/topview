package com.yangwawa.topview.demo;

import android.app.Application;

import com.yangwawa.topview.TopView;

public class MainApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        TopView.getInstance().init();
    }
}
