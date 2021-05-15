package com.example.androidremake2.injects.base;

import android.os.Bundle;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

public abstract class BaseFragment extends Fragment implements BaseComponent {

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        initViewModels();

        subscribeObservers();
    }

    @Override
    public void initViewModels() {

    }

    @Override
    public void subscribeObservers() {

    }
}
