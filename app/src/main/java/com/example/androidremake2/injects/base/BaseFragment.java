package com.example.androidremake2.injects.base;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.androidremake2.R;

import org.jetbrains.annotations.NotNull;

public abstract class BaseFragment extends Fragment implements BaseComponent {

    @Nullable
    @org.jetbrains.annotations.Nullable
    @Override
    public View onCreateView(@NonNull @NotNull LayoutInflater inflater, @Nullable @org.jetbrains.annotations.Nullable ViewGroup container, @Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {

        initViewModels();

        subscribeObservers();

        return super.onCreateView(inflater, container, savedInstanceState);
    }

    @Override
    public void initViewModels() {

    }

    @Override
    public void subscribeObservers() {

    }

    @Override
    public void unsubscribeObservers() {

    }

    public void showHideLoader(BaseViewModel.LoadingStatus status) {
        int visibility = status == BaseViewModel.LoadingStatus.LOADING ? View.VISIBLE : View.GONE;
        requireActivity().findViewById(R.id.loader).setVisibility(visibility);
    }

    @Override
    public void onDestroyView() {

        unsubscribeObservers();

        super.onDestroyView();
    }
}
