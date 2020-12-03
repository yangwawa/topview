package com.yangwawa.topview.demo;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.blankj.utilcode.util.ConvertUtils;
import com.blankj.utilcode.util.ScreenUtils;

public class ImageActivity extends AppCompatActivity {
    private int mDialogWith = ScreenUtils.getScreenWidth();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image);
    }

    public void nextActivity(View v){
        Intent intent = new Intent(this, ImageActivity.class);
        startActivity(intent);
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
}