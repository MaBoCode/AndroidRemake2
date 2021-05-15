package com.example.androidremake2;

import android.app.Application;

import com.example.androidremake2.utils.Prefs;
import com.example.androidremake2.utils.ThemeUtils;

import dagger.hilt.android.HiltAndroidApp;

@HiltAndroidApp
public class App extends Application {

    @Override
    public void onCreate() {
        super.onCreate();

        String theme = Prefs.getPrefTheme(getApplicationContext());
        ThemeUtils.setTheme(theme);
    }
}
