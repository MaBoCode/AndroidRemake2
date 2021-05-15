package com.example.androidremake2.views.settings;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;

import androidx.preference.PreferenceFragmentCompat;

import com.example.androidremake2.R;
import com.example.androidremake2.utils.Prefs;
import com.example.androidremake2.utils.ThemeUtils;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class SettingsFragment extends PreferenceFragmentCompat implements SharedPreferences.OnSharedPreferenceChangeListener {

    @Override
    public void onStart() {
        super.onStart();

        BottomNavigationView bottomNav = requireActivity().findViewById(R.id.bottomNavView);
        if (bottomNav.getVisibility() == View.GONE) {
            bottomNav.setVisibility(View.VISIBLE);
        }
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
