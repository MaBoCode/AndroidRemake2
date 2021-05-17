package com.example.androidremake2.views.podcast.viewmodels;

import androidx.hilt.Assisted;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.SavedStateHandle;

import com.example.androidremake2.core.podcast.GetBestPodcastsResponse;
import com.example.androidremake2.core.podcast.Podcast;
import com.example.androidremake2.core.podcast.PodcastService;
import com.example.androidremake2.core.user.UserService;
import com.example.androidremake2.injects.base.BaseViewModel;
import com.example.androidremake2.utils.Logs;
import com.example.androidremake2.views.utils.events.SingleLiveEvent;

import java.util.List;

import javax.inject.Inject;

import dagger.hilt.android.lifecycle.HiltViewModel;
import io.reactivex.rxjava3.disposables.Disposable;
import io.reactivex.rxjava3.functions.Action;
import io.reactivex.rxjava3.functions.Consumer;
import io.reactivex.rxjava3.schedulers.Schedulers;

@HiltViewModel
public class MainFragmentViewModel extends BaseViewModel {

    protected UserService userService;
    protected PodcastService podcastService;

    protected MutableLiveData<List<Podcast>> _bestPodcastsLiveData = new MutableLiveData<>();
    public LiveData<List<Podcast>> bestPodcastsLiveData = _bestPodcastsLiveData;

    public MutableLiveData<Integer> nextPage = new MutableLiveData<>(1);
    public MutableLiveData<Integer> podcastCountInPage = new MutableLiveData<>(0);

    protected SingleLiveEvent<Podcast> _podcastLiveData = new SingleLiveEvent<>();
    public LiveData<Podcast> podcastLiveData = _podcastLiveData;

    @Inject
    public MainFragmentViewModel(PodcastService podcastService, UserService userService, @Assisted SavedStateHandle savedStateHandle) {
        this.podcastService = podcastService;
        this.userService = userService;
        this.savedStateHandle = savedStateHandle;
    }

    public void getBestPodcasts(Integer page, String region) {
        podcastService.getBestPodcasts(page, region)
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
                .subscribe(new Consumer<GetBestPodcastsResponse>() {
                    @Override
                    public void accept(GetBestPodcastsResponse getBestPodcastsResponse) throws Throwable {

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
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Throwable {
                        Logs.error(this, throwable.getLocalizedMessage());
                    }
                });
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
