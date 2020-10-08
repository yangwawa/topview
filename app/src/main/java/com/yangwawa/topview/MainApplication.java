package com.yangwawa.topview;

import android.app.Application;

public class MainApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        TopView.getInstance().init(this);
    }
}
