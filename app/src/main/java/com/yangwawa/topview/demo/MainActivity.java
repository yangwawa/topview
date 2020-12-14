package com.yangwawa.topview.demo;


import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.PixelFormat;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;

import com.blankj.utilcode.util.ConvertUtils;
import com.blankj.utilcode.util.ScreenUtils;
import com.yangwawa.topview.TopView;

public class MainActivity extends AppCompatActivity {

    private int mDialogWith = ScreenUtils.getScreenWidth();

    private View mTopView = null;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    public void startNewActivity(View view){
        Intent intent = new Intent(this, ImageActivity.class);
        startActivity(intent);
    }

    public void addNewTopView(View view){
        if(mTopView == null){
            TextView tv = new TextView(this);
            tv.setText("topview is always top");
            tv.setTextColor(Color.GREEN);
            tv.setGravity(Gravity.CENTER);
            tv.setBackgroundColor(Color.BLACK);
            mTopView = tv;
        }
        WindowManager.LayoutParams lp = new WindowManager.LayoutParams(WindowManager.LayoutParams.WRAP_CONTENT,
                WindowManager.LayoutParams.WRAP_CONTENT);
        lp.width = 600;
        lp.height = 300;
        lp.type = WindowManager.LayoutParams.TYPE_APPLICATION;
        lp.flags = WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE;
        lp.format = PixelFormat.TRANSLUCENT;
        TopView.getInstance().addView(mTopView, lp);
    }

    public void removeTopview(View v){
        TopView.getInstance().removeView(mTopView);
    }

    public void showNewDailog(View view){
        Dialog dialog = new Dialog(this);
        Button btn = new Button(this);
        btn.setText("new dialog");
        btn.setGravity(Gravity.TOP | Gravity.LEFT);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showNewDailog(view);
            }
        });
        dialog.setTitle("title");
        ViewGroup.LayoutParams lp = new ViewGroup.LayoutParams(mDialogWith, mDialogWith);
        dialog.addContentView(btn, lp);
        dialog.show();
        mDialogWith -= ConvertUtils.dp2px(40);
    }

    @Override
    protected void onDestroy() {
        TopView.getInstance().removeView(mTopView);
        super.onDestroy();
    }
}