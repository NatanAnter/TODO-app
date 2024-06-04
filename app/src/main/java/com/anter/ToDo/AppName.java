package com.anter.ToDo;

import android.app.Application;
import android.util.Log;

import com.google.android.material.color.DynamicColors;

public class AppName extends Application {
    @Override
    public void onCreate() {
//        AppCompatDelegate.setDefaultNightMode(darkModeStatus);
        super.onCreate();
        Utils.setAppName(this);
    }
    public void setDynamic(boolean isDynamic) {
        if (isDynamic) DynamicColors.applyToActivitiesIfAvailable(this);
        else DynamicColors.applyToActivitiesIfAvailable(this,R.style.AppTheme);
    }
}


























