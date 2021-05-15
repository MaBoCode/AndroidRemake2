package com.example.androidremake2.views.search;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.SearchView;

import com.example.androidremake2.R;
import com.example.androidremake2.databinding.FrgSearchBinding;
import com.example.androidremake2.injects.base.BaseFragment;
import com.example.androidremake2.utils.Logs;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class SearchFragment extends BaseFragment implements View.OnFocusChangeListener, SearchView.OnQueryTextListener {

    protected FrgSearchBinding binding;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FrgSearchBinding.inflate(inflater, container, false);

        binding.searchView.setFocusedByDefault(true);
        binding.searchView.setOnQueryTextFocusChangeListener(this);
        binding.searchView.setOnQueryTextListener(this);

        return binding.getRoot();
    }

    @Override
    public boolean onQueryTextSubmit(String query) {
        Logs.debug(this, "[DBG] query: " + query);
        return false;
    }

    @Override
    public boolean onQueryTextChange(String newText) {
        return false;
    }

    @Override
    public void onFocusChange(View view, boolean hasFocus) {
        BottomNavigationView bottomNav = requireActivity().findViewById(R.id.bottomNavView);
        if (hasFocus) {
            bottomNav.setVisibility(View.GONE);
        } else {
            bottomNav.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onDestroy() {

        binding.searchView.setOnQueryTextFocusChangeListener(null);

        binding = null;

        super.onDestroy();
    }
}
