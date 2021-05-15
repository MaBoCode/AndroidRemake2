package com.example.androidremake2.views;

import android.os.Bundle;

import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.NavigationUI;
import androidx.preference.PreferenceManager;

import com.example.androidremake2.R;
import com.example.androidremake2.databinding.ActMainBinding;
import com.example.androidremake2.injects.base.BaseActivity;

import org.androidannotations.annotations.EActivity;

import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
@EActivity
public class MainActivity extends BaseActivity {

    protected ActMainBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        PreferenceManager.setDefaultValues(getApplicationContext(), R.xml.preferences, false);

        NavController navController = Navigation.findNavController(this, R.id.frg_nav_host);
        NavigationUI.setupWithNavController(binding.bottomNavView, navController);
    }
}