package com.example.androidremake2.services.podcast;

import android.content.ComponentName;
import android.content.Context;
import android.support.v4.media.MediaBrowserCompat;
import android.support.v4.media.MediaMetadataCompat;
import android.support.v4.media.session.MediaControllerCompat;
import android.support.v4.media.session.PlaybackStateCompat;

import androidx.lifecycle.MutableLiveData;

import com.example.androidremake2.services.podcast.callbacks.PodcastBrowserConnectionCallback;

import javax.inject.Inject;

public class PodcastServiceConnection {

    public static PlaybackStateCompat EMPTY_PLAYBACK_STATE = new PlaybackStateCompat.Builder()
            .setState(PlaybackStateCompat.STATE_NONE, 0, 0f)
            .build();

    public static MediaMetadataCompat NOTHING_PLAYING = new MediaMetadataCompat.Builder()
            .putString(MediaMetadataCompat.METADATA_KEY_MEDIA_ID, "")
            .putLong(MediaMetadataCompat.METADATA_KEY_DURATION, 0)
            .build();

    protected Context context;

    protected ComponentName serviceComponent;

    protected String rootMediaId;

    protected MediaBrowserCompat mediaBrowser;
    protected MediaControllerCompat mediaController;
    protected MediaControllerCompat.TransportControls transportControls;

    public MutableLiveData<Boolean> isConnected = new MutableLiveData<>(false);
    protected MutableLiveData<PlaybackStateCompat> playbackState = new MutableLiveData<>(EMPTY_PLAYBACK_STATE);
    public MutableLiveData<MediaMetadataCompat> nowPlaying = new MutableLiveData<>(NOTHING_PLAYING);

    protected PodcastBrowserConnectionCallback podcastBrowserConnectionCallback;

    @Inject
    public PodcastServiceConnection(Context context, ComponentName serviceComponent) {
        this.context = context;
        this.serviceComponent = serviceComponent;
        this.podcastBrowserConnectionCallback = new PodcastBrowserConnectionCallback(context, this);

        this.mediaBrowser = new MediaBrowserCompat(
                context,
                serviceComponent,
                podcastBrowserConnectionCallback,
                null
        );
    }

    public boolean isPlaying() {
        int playbackState = this.playbackState.getValue().getState();
        return playbackState == PlaybackStateCompat.STATE_BUFFERING
                || playbackState == PlaybackStateCompat.STATE_PLAYING;
    }

    public boolean isPrepared() {
        int playbackState = this.playbackState.getValue().getState();
        return playbackState == PlaybackStateCompat.STATE_BUFFERING
                || playbackState == PlaybackStateCompat.STATE_PLAYING
                || playbackState == PlaybackStateCompat.STATE_PAUSED;
    }

    public MediaControllerCompat getMediaController() {
        return mediaController;
    }

    public MediaControllerCompat.TransportControls getTransportControls() {
        return transportControls;
    }

    public MediaBrowserCompat getMediaBrowser() {
        return mediaBrowser;
    }

    public PodcastBrowserConnectionCallback getPodcastBrowserConnectionCallback() {
        return podcastBrowserConnectionCallback;
    }

    public void setNowPlaying(MediaMetadataCompat nowPlaying) {
        this.nowPlaying.postValue(nowPlaying);
    }

    public void setPlaybackState(PlaybackStateCompat playbackState) {
        this.playbackState.postValue(playbackState);
    }

    public void setRootMediaId(String rootMediaId) {
        this.rootMediaId = rootMediaId;
    }

    public void setIsConnected(Boolean isConnected) {
        this.isConnected.postValue(isConnected);
    }

    public void setMediaController(MediaControllerCompat mediaController) {
        this.mediaController = mediaController;
    }

    public void setTransportControls(MediaControllerCompat.TransportControls transportControls) {
        this.transportControls = transportControls;
    }
}
