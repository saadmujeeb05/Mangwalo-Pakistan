package com.aliyan.mangwalopakistan;

import android.app.Application;
import android.support.multidex.MultiDex;

import android.content.Context;


/**
 * Created by Aliyan on 5/1/2017.
 */
public class App extends Application {
    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        MultiDex.install(this);
    }
}