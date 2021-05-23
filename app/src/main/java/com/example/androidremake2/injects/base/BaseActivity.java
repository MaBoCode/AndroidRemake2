package com.example.androidremake2.injects.base;

import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

import com.example.androidremake2.R;

public abstract class BaseActivity extends AppCompatActivity {

    public void initViewModels() {

    }

    public void subscribeObservers() {

    }

    public void unsubscribeObservers() {

    }

    public void showHideLoader(BaseViewModel.LoadingStatus status) {
        int visibility = status == BaseViewModel.LoadingStatus.LOADING ? View.VISIBLE : View.GONE;
        this.findViewById(R.id.loader).setVisibility(visibility);
    }

}
