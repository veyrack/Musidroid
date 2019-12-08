package com.example.a3520096.myapplication;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.app.Activity;


public class MainActivity extends Activity {
    TheApplication app;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        app = (TheApplication)(this.getApplication());

    }
    public void onClickExit(View v){
        finish();
    }

    public void TouchActivity (View v){
        Intent intent = new Intent(this,TouchActivity.class);
        startActivity(intent);

    }
}
