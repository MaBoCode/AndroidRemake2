package com.example.androidremake2.views.podcast.viewmodels;

import androidx.hilt.Assisted;
import androidx.lifecycle.SavedStateHandle;

import com.example.androidremake2.injects.base.BaseViewModel;

import javax.inject.Inject;

import dagger.hilt.android.lifecycle.HiltViewModel;

@HiltViewModel
public class PodcastBottomSheetFragmentViewModel extends BaseViewModel {

    @Inject
    public PodcastBottomSheetFragmentViewModel(@Assisted SavedStateHandle savedStateHandle) {
        this.savedStateHandle = savedStateHandle;
    }
}
