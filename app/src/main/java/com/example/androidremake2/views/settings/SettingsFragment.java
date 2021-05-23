package com.example.androidremake2.views.settings;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;

import androidx.interpolator.view.animation.FastOutSlowInInterpolator;
import androidx.preference.PreferenceFragmentCompat;

import com.example.androidremake2.R;
import com.example.androidremake2.utils.Logs;
import com.example.androidremake2.utils.Prefs;
import com.example.androidremake2.utils.ThemeUtils;
import com.example.androidremake2.views.utils.AnimationUtils;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.util.Arrays;

public class SettingsFragment extends PreferenceFragmentCompat implements SharedPreferences.OnSharedPreferenceChangeListener {

    @Override
    public void onStart() {
        super.onStart();

        Logs.debug(this, "");

        BottomNavigationView bottomNav = requireActivity().findViewById(R.id.bottomNavView);

        if (bottomNav.getVisibility() == View.VISIBLE) {
            return;
        }

        bottomNav.setVisibility(View.VISIBLE);

        new AnimationUtils.Builder()
                .setObjects(Arrays.asList(bottomNav))
                .setAnimateAlphaIn(true)
                .setTranslationYBegin(100f)
                .setInterpolator(new FastOutSlowInInterpolator())
                .start();
    }

    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        setPreferencesFromResource(R.xml.preferences, rootKey);
        getPreferenceManager().getSharedPreferences().registerOnSharedPreferenceChangeListener(this);
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        if (key.equals(Prefs.PREFS_THEME)) {
            String theme = sharedPreferences.getString(key, Prefs.PREFS_THEME_DEFAULT);
            ThemeUtils.setTheme(theme);
        }
    }

    @Override
    public void onDestroy() {

        getPreferenceManager().getSharedPreferences().unregisterOnSharedPreferenceChangeListener(this);

        super.onDestroy();
    }
}
