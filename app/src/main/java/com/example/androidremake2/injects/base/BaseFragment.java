package com.example.androidremake2.injects.base;

import android.os.Bundle;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.androidremake2.R;

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

    public void showHideLoader(BaseViewModel.LoadingStatus status) {
        int visiblity = status == BaseViewModel.LoadingStatus.LOADING ? View.VISIBLE : View.GONE;
        requireActivity().findViewById(R.id.loader).setVisibility(visiblity);
    }
}
