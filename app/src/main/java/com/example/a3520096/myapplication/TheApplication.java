package com.example.a3520096.myapplication;

import android.app.Application;

public class TheApplication extends Application {
    Model m;
    @Override
    public void onCreate() {
        super.onCreate();
        m = new Model();
    }
    public Model getModel() {
        return m;
    }
}
