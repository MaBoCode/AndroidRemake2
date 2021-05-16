package com.example.androidremake2.views.search.viewmodels;

import androidx.hilt.Assisted;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.SavedStateHandle;

import com.example.androidremake2.core.podcast.Podcast;
import com.example.androidremake2.core.podcast.PodcastService;
import com.example.androidremake2.core.podcast.SearchPodcastList;
import com.example.androidremake2.injects.base.BaseViewModel;
import com.example.androidremake2.utils.Logs;

import java.util.List;

import javax.inject.Inject;

import dagger.hilt.android.lifecycle.HiltViewModel;
import io.reactivex.rxjava3.disposables.Disposable;
import io.reactivex.rxjava3.functions.Action;
import io.reactivex.rxjava3.functions.Consumer;
import io.reactivex.rxjava3.schedulers.Schedulers;

@HiltViewModel
public class SearchFragmentViewModel extends BaseViewModel {

    protected PodcastService podcastService;

    protected MutableLiveData<List<Podcast>> _podcastsResultLiveData = new MutableLiveData<>();
    public LiveData<List<Podcast>> podcastsResultLiveData = _podcastsResultLiveData;

    protected String currentQuery;

    public Integer previousSearchOffset = null;
    public Integer nextSearchOffset = null;

    @Inject
    public SearchFragmentViewModel(PodcastService podcastService, @Assisted SavedStateHandle savedStateHandle) {
        this.podcastService = podcastService;
        this.savedStateHandle = savedStateHandle;
    }

    public void searchPodcasts(String query, String onlyIn, Integer offset) {
        search(query, "podcast", onlyIn, offset);
    }

    public void search(String query, String type, String onlyIn, Integer offset) {
        podcastService.searchPodcasts(query, type, onlyIn, offset)
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
                .subscribe(new Consumer<SearchPodcastList>() {
                    @Override
                    public void accept(SearchPodcastList searchPodcastList) throws Throwable {
                        previousSearchOffset = nextSearchOffset;
                        nextSearchOffset = searchPodcastList.offset;

                        List<Podcast> allPodcasts = _podcastsResultLiveData.getValue();
                        if (allPodcasts != null) {
                            allPodcasts.addAll(searchPodcastList.results);
                        } else {
                            allPodcasts = searchPodcastList.results;
                        }
                        _podcastsResultLiveData.postValue(allPodcasts);
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Throwable {
                        Logs.error(this, throwable.getLocalizedMessage());
                    }
                });
    }

    public void clearSearchResults() {
        List<Podcast> podcasts = _podcastsResultLiveData.getValue();

        if (podcasts == null) {
            Logs.debug(this, "[DBG] null podcasts");
            return;
        }
        podcasts.clear();
        _podcastsResultLiveData.postValue(podcasts);
    }

    public void setCurrentQuery(String currentQuery) {
        this.currentQuery = currentQuery;
    }

    public String getCurrentQuery() {
        return currentQuery;
    }
}
