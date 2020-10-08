package com.yangwawa.topview;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {

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
        TopView.getInstance().addView(tv);
    }

    public void showNewDailog(View view){
        Dialog dialog = new Dialog(this);
        TextView tv = new TextView(this);
        tv.setText("hello");
        dialog.setTitle("title");
        dialog.setContentView(tv);
        dialog.show();
    }
}