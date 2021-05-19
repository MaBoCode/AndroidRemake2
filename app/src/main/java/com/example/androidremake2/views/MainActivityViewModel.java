package com.example.androidremake2.views;

import android.net.Uri;
import android.support.v4.media.MediaBrowserCompat;
import android.support.v4.media.MediaMetadataCompat;
import android.support.v4.media.session.MediaControllerCompat;

import androidx.hilt.Assisted;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.SavedStateHandle;

import com.example.androidremake2.core.podcast.Podcast;
import com.example.androidremake2.core.podcast.PodcastEpisode;
import com.example.androidremake2.injects.base.BaseViewModel;
import com.example.androidremake2.services.podcast.PodcastServiceConnection;

import javax.inject.Inject;

import dagger.hilt.android.lifecycle.HiltViewModel;

@HiltViewModel
public class MainActivityViewModel extends BaseViewModel {

    @Inject
    protected PodcastServiceConnection podcastServiceConnection;

    public Podcast playingPodcast;

    protected MutableLiveData<PodcastEpisode> _playingPodcastEpisode = new MutableLiveData<>();
    public LiveData<PodcastEpisode> playingPodcastEpisode = _playingPodcastEpisode;

    @Inject
    public MainActivityViewModel(@Assisted SavedStateHandle savedStateHandle) {
        this.savedStateHandle = savedStateHandle;
    }

    public void connectToPodcastService() {
        MediaBrowserCompat mediaBrowser = podcastServiceConnection.getMediaBrowser();
        if (!mediaBrowser.isConnected()) {
            mediaBrowser.connect();
        }
    }

    public void disconnectFromPodcastService() {

    }

    public void playPodcast(Podcast podcast) {
        PodcastEpisode episode = podcast.getNextEpisode();

        this.playingPodcast = podcast;
        _playingPodcastEpisode.postValue(episode);

        MediaMetadataCompat nowPlaying = podcastServiceConnection.nowPlaying.getValue();
        MediaControllerCompat.TransportControls transportControls = podcastServiceConnection.getTransportControls();

        if (podcastServiceConnection.isPrepared()
                && episode.id.contentEquals(nowPlaying.getString(MediaMetadataCompat.METADATA_KEY_MEDIA_ID))) {

            if (podcastServiceConnection.isPlaying()) {
                transportControls.pause();
            } else {
                transportControls.play();
            }
        } else {
            transportControls.playFromUri(Uri.parse(episode.audioUrl), null);
        }
    }

    public PodcastServiceConnection getPodcastServiceConnection() {
        return podcastServiceConnection;
    }
}
