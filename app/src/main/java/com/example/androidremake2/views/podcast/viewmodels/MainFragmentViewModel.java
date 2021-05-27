package com.example.androidremake2.views.podcast.viewmodels;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.SavedStateHandle;

import com.example.androidremake2.core.podcast.Podcast;
import com.example.androidremake2.core.podcast.PodcastService;
import com.example.androidremake2.injects.base.BaseViewModel;
import com.example.androidremake2.utils.Logs;

import java.util.List;

import javax.inject.Inject;

import dagger.hilt.android.lifecycle.HiltViewModel;
import io.reactivex.rxjava3.functions.Consumer;
import io.reactivex.rxjava3.schedulers.Schedulers;

@HiltViewModel
public class MainFragmentViewModel extends BaseViewModel {

    @Inject
    protected PodcastService podcastService;

    protected MutableLiveData<List<Podcast>> _bestPodcastsLiveData = new MutableLiveData<>();
    public LiveData<List<Podcast>> bestPodcastsLiveData = _bestPodcastsLiveData;

    public MutableLiveData<Integer> nextPage = new MutableLiveData<>(1);
    public MutableLiveData<Integer> podcastCountInPage = new MutableLiveData<>(0);

    @Inject
    public MainFragmentViewModel(SavedStateHandle savedStateHandle) {
        this.savedStateHandle = savedStateHandle;
    }

    public void getBestPodcasts(Integer page, String region) {
        podcastService.getBestPodcasts(page, region)
                .subscribeOn(Schedulers.io())
                .observeOn(Schedulers.io())
                .doOnSubscribe(disposable -> _loadingLiveData.postValue(LoadingStatus.LOADING))
                .doFinally(() -> _loadingLiveData.postValue(LoadingStatus.NOT_LOADING))
                .subscribe(getBestPodcastsResponse -> {

                    List<Podcast> allPodcasts = _bestPodcastsLiveData.getValue();
                    if (allPodcasts != null) {
                        allPodcasts.addAll(getBestPodcastsResponse.podcasts);
                    } else {
                        allPodcasts = getBestPodcastsResponse.podcasts;
                    }

                    _bestPodcastsLiveData.postValue(allPodcasts);

                    if (getBestPodcastsResponse.hasNext) {
                        nextPage.postValue(getBestPodcastsResponse.nextPageNumber);
                    } else {
                        nextPage.postValue(null);
                    }

                    podcastCountInPage.postValue(getBestPodcastsResponse.podcasts.size());
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) {
                        Logs.error(this, throwable.getLocalizedMessage());
                    }
                });
    }

}
