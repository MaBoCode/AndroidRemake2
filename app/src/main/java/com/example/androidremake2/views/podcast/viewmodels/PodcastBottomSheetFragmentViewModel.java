package com.example.androidremake2.views.podcast.viewmodels;

import android.os.Handler;
import android.os.Looper;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.androidremake2.core.podcast.PodcastEpisode;
import com.example.androidremake2.injects.base.BaseViewModel;

import javax.inject.Inject;

import dagger.hilt.android.lifecycle.HiltViewModel;

@HiltViewModel
public class PodcastBottomSheetFragmentViewModel extends BaseViewModel {

    protected MutableLiveData<Integer> _mediaButtonRes = new MutableLiveData<>();
    public LiveData<Integer> mediaButtonRes = _mediaButtonRes;

    protected MutableLiveData<PodcastEpisode> _podcastEpisode = new MutableLiveData<>();
    public LiveData<PodcastEpisode> podcastEpisode = _podcastEpisode;

    protected Handler handler = new Handler(Looper.getMainLooper());

    @Inject
    public PodcastBottomSheetFragmentViewModel() {

    }

    public void setPodcastEpisode(PodcastEpisode podcastEpisode) {
        _podcastEpisode.postValue(podcastEpisode);
    }
}
