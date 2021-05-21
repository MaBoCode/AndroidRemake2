package com.example.androidremake2.views;

import androidx.hilt.Assisted;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.SavedStateHandle;

import com.example.androidremake2.core.podcast.Podcast;
import com.example.androidremake2.core.podcast.PodcastEpisode;
import com.example.androidremake2.core.podcast.PodcastService;
import com.example.androidremake2.injects.base.BaseViewModel;
import com.example.androidremake2.utils.Logs;

import javax.inject.Inject;

import dagger.hilt.android.lifecycle.HiltViewModel;
import io.reactivex.rxjava3.disposables.Disposable;
import io.reactivex.rxjava3.functions.Action;
import io.reactivex.rxjava3.functions.Consumer;
import io.reactivex.rxjava3.schedulers.Schedulers;

@HiltViewModel
public class MainActivityViewModel extends BaseViewModel {

    @Inject
    protected PodcastService podcastService;

    protected MutableLiveData<Podcast> _podcastLiveData = new MutableLiveData<>();
    public LiveData<Podcast> podcastLiveData = _podcastLiveData;

    protected MutableLiveData<PodcastEpisode> _playingPodcastEpisode = new MutableLiveData<>();
    public LiveData<PodcastEpisode> playingPodcastEpisode = _playingPodcastEpisode;

    @Inject
    public MainActivityViewModel(@Assisted SavedStateHandle savedStateHandle) {
        this.savedStateHandle = savedStateHandle;
    }

    public void getPodcast(String id) {
        podcastService.getPodcast(id)
                .subscribeOn(Schedulers.io())
                .observeOn(Schedulers.io())
                .doOnSubscribe(new Consumer<Disposable>() {
                    @Override
                    public void accept(Disposable disposable) throws Throwable {
                        _loadingLiveData.postValue(LoadingStatus.LOADING);
                    }
                })
                .doFinally(new Action() {
                    @Override
                    public void run() throws Throwable {
                        _loadingLiveData.postValue(LoadingStatus.NOT_LOADING);
                    }
                })
                .subscribe(new Consumer<Podcast>() {
                    @Override
                    public void accept(Podcast podcast) throws Throwable {

                        _podcastLiveData.postValue(podcast);
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Throwable {
                        Logs.error(this, throwable.getLocalizedMessage());
                    }
                });
    }
}
