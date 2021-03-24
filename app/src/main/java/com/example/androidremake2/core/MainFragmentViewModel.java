package com.example.androidremake2.core;

import androidx.hilt.Assisted;
import androidx.hilt.lifecycle.ViewModelInject;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.SavedStateHandle;

import com.example.androidremake2.core.podcast.Podcast;
import com.example.androidremake2.core.podcast.PodcastList;
import com.example.androidremake2.core.podcast.PodcastService;
import com.example.androidremake2.core.user.User;
import com.example.androidremake2.core.user.UserService;
import com.example.androidremake2.injects.base.BaseViewModel;
import com.example.androidremake2.utils.Logs;

import java.util.List;

import io.reactivex.rxjava3.disposables.Disposable;
import io.reactivex.rxjava3.functions.Action;
import io.reactivex.rxjava3.functions.Consumer;
import io.reactivex.rxjava3.schedulers.Schedulers;

public class MainFragmentViewModel extends BaseViewModel {

    protected UserService userService;
    protected PodcastService podcastService;

    protected MutableLiveData<List<User>> _usersLiveData = new MutableLiveData<>();
    public LiveData<List<User>> usersLiveData = _usersLiveData;

    protected MutableLiveData<List<Podcast>> _podcastsLiveData = new MutableLiveData<>();
    public LiveData<List<Podcast>> podcastsLiveData = _podcastsLiveData;

    protected MutableLiveData<Podcast> _podcastLiveData = new MutableLiveData<>();
    public LiveData<Podcast> podcastLiveData = _podcastLiveData;

    @ViewModelInject
    public MainFragmentViewModel(PodcastService podcastService, UserService userService, @Assisted SavedStateHandle savedStateHandle) {
        this.podcastService = podcastService;
        this.userService = userService;
        this.savedStateHandle = savedStateHandle;
    }

    public void getUsers() {
        userService.getUsers()
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
                .subscribe(new Consumer<List<User>>() {
                    @Override
                    public void accept(List<User> users) throws Throwable {
                        _usersLiveData.postValue(users);
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Throwable {
                        Logs.error(this, throwable.getMessage());
                    }
                });
    }

    public void getBestPodcasts() {
        podcastService.getBestPodcasts()
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
                .subscribe(new Consumer<PodcastList>() {
                    @Override
                    public void accept(PodcastList podcastList) throws Throwable {
                        _podcastsLiveData.postValue(podcastList.podcasts);
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
