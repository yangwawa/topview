package com.yangwawa.topview.demo;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.blankj.utilcode.util.ConvertUtils;
import com.blankj.utilcode.util.ScreenUtils;
import com.yangwawa.topview.TopView;

public class MainActivity extends AppCompatActivity {

    private int mDialogWith = ScreenUtils.getScreenWidth();
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
        TextView tv = new TextView(this);
        tv.setText("topview");
        tv.setTextColor(Color.RED);
        TopView.getInstance().addView(tv);
    }

    public void showNewDailog(View view){
        Dialog dialog = new Dialog(this);
        Button btn = new Button(this);
        btn.setText("new dialog");
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
        TopView.getInstance().detachAll();
        super.onDestroy();
    }
}