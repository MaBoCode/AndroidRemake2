package com.example.androidremake2.views;

import android.support.v4.media.MediaBrowserCompat;

import androidx.annotation.Nullable;
import androidx.hilt.Assisted;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.SavedStateHandle;

import com.example.androidremake2.core.podcast.Podcast;
import com.example.androidremake2.core.podcast.PodcastEpisode;
import com.example.androidremake2.core.podcast.PodcastService;
import com.example.androidremake2.injects.base.BaseViewModel;
import com.example.androidremake2.services.podcast.PodcastServiceConnection;
import com.example.androidremake2.utils.Logs;
import com.google.android.exoplayer2.ExoPlayer;
import com.google.android.exoplayer2.MediaItem;
import com.google.android.exoplayer2.Player;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import dagger.hilt.android.lifecycle.HiltViewModel;
import io.reactivex.rxjava3.disposables.Disposable;
import io.reactivex.rxjava3.functions.Action;
import io.reactivex.rxjava3.functions.Consumer;
import io.reactivex.rxjava3.schedulers.Schedulers;

@HiltViewModel
public class MainActivityViewModel extends BaseViewModel implements Player.EventListener {

    protected PodcastServiceConnection podcastServiceConnection;

    @Inject
    protected PodcastService podcastService;

    protected ExoPlayer exoPlayer;

    protected MutableLiveData<Boolean> _isMediaPlayingLiveData = new MutableLiveData<>(false);
    public LiveData<Boolean> isMediaPlayingLiveData = _isMediaPlayingLiveData;

    protected MutableLiveData<Podcast> _podcastLiveData = new MutableLiveData<>();
    public LiveData<Podcast> podcastLiveData = _podcastLiveData;

    protected MutableLiveData<PodcastEpisode> _playingEpisodeLiveData = new MutableLiveData<>();
    public LiveData<PodcastEpisode> playingEpisodeLiveData = _playingEpisodeLiveData;

    protected MutableLiveData<List<MediaBrowserCompat.MediaItem>> _episodesMediaItems = new MutableLiveData<>();
    public LiveData<List<MediaBrowserCompat.MediaItem>> episodesMediaItems = _episodesMediaItems;

    @Inject
    public MainActivityViewModel(PodcastServiceConnection podcastServiceConnection, ExoPlayer exoPlayer, @Assisted SavedStateHandle savedStateHandle) {
        this.savedStateHandle = savedStateHandle;
        this.podcastServiceConnection = podcastServiceConnection;
        this.exoPlayer = exoPlayer;

        this.exoPlayer.addListener(this);

        /*
        this.podcastServiceConnection.subscribe(PodcastForegroundService.EMPTY_MEDIA_ROOT_ID, new MediaBrowserCompat.SubscriptionCallback() {
            @Override
            public void onChildrenLoaded(@NonNull @NotNull String parentId, @NonNull @NotNull List<MediaBrowserCompat.MediaItem> children) {
                MainActivityViewModel.this._episodesMediaItems.postValue(children);
            }
        });

         */
    }

    public void playPodcast() {
        if (podcastServiceConnection.isPrepared()) {
            // Play or pause
        } else {
            String podcastId = _podcastLiveData.getValue().id;
            podcastServiceConnection.getTransportControls().playFromMediaId(podcastId, null);
        }
    }

    public void playEpisodes(List<PodcastEpisode> episodes) {
        this.exoPlayer.stop();

        List<MediaItem> mediaItems = new ArrayList<>();
        for (PodcastEpisode episode : episodes) {
            MediaItem mediaItem = new MediaItem.Builder()
                    .setMediaId(episode.id)
                    .setUri(episode.audioUrl)
                    .build();
            mediaItems.add(mediaItem);
        }
        this.exoPlayer.addMediaItems(mediaItems);
        this.exoPlayer.prepare();
        this.exoPlayer.play();
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

    @Override
    public void onIsPlayingChanged(boolean isPlaying) {
        _isMediaPlayingLiveData.postValue(isPlaying);
    }

    @Override
    public void onMediaItemTransition(@Nullable @org.jetbrains.annotations.Nullable MediaItem mediaItem, int reason) {
        PodcastEpisode episode = null;
        Podcast podcast = podcastLiveData.getValue();

        if (podcast == null || mediaItem == null) {
            return;
        }

        for (PodcastEpisode e : podcast.episodes) {
            if (e.id.contentEquals(mediaItem.mediaId)) {
                episode = e;
                break;
            }
        }
        _playingEpisodeLiveData.postValue(episode);
    }

    @Override
    protected void onCleared() {
        super.onCleared();

        /*
        podcastServiceConnection.unsubscribe(PodcastForegroundService.EMPTY_MEDIA_ROOT_ID, new MediaBrowserCompat.SubscriptionCallback() {
            @Override
            public void onError(@NonNull @NotNull String parentId) {
                super.onError(parentId);
            }
        });

         */
    }

    public void stopPlayer() {
        this.exoPlayer.stop();

        if (this.exoPlayer.getMediaItemCount() > 0) {
            this.exoPlayer.removeMediaItems(0, this.exoPlayer.getMediaItemCount() - 1);
        }
    }

    public ExoPlayer getExoPlayer() {
        return exoPlayer;
    }
}
