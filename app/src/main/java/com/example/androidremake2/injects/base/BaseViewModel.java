package com.example.androidremake2.injects.base;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.SavedStateHandle;
import androidx.lifecycle.ViewModel;

import com.example.androidremake2.views.utils.events.SingleLiveEvent;

public abstract class BaseViewModel extends ViewModel {

    public enum LoadingStatus {
        LOADING,
        NOT_LOADING
    }

    protected SavedStateHandle savedStateHandle;

    protected SingleLiveEvent<LoadingStatus> _loadingLiveData = new SingleLiveEvent<>();
    public LiveData<LoadingStatus> loadingLiveData = _loadingLiveData;

}
