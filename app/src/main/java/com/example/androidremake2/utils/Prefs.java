package com.example.androidremake2.utils;

import android.content.Context;
import android.content.SharedPreferences;

import androidx.preference.PreferenceManager;

public class Prefs {

    public static final String PREFS_FILE = "fr.podcast";

    // KEYS
    public static final String PREFS_THEME = "PREFS_THEME";

    // VALUES
    public static final String PREFS_THEME_LIGHT = "Light";
    public static final String PREFS_THEME_DARK = "Dark";
    public static final String PREFS_THEME_DEFAULT = "System Default";


    public static SharedPreferences getSharedPreferences(Context context) {
        return PreferenceManager.getDefaultSharedPreferences(context);
    }

    public static String getPrefTheme(Context context) {
        return getSharedPreferences(context).getString(PREFS_THEME, PREFS_THEME_DEFAULT);
    }

    public static void setPrefTheme(Context context, String theme) {
        SharedPreferences.Editor editor = getSharedPreferences(context).edit();
        editor.putString(PREFS_THEME, theme);
        editor.apply();
    }

}
