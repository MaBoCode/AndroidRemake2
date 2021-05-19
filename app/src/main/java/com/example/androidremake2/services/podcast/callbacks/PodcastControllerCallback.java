package com.example.androidremake2.services.podcast.callbacks;

import android.support.v4.media.MediaMetadataCompat;
import android.support.v4.media.session.MediaControllerCompat;
import android.support.v4.media.session.PlaybackStateCompat;

import com.example.androidremake2.services.podcast.PodcastServiceConnection;

public class PodcastControllerCallback extends MediaControllerCompat.Callback {

    protected PodcastServiceConnection serviceConnection;

    public PodcastControllerCallback(PodcastServiceConnection serviceConnection) {
        this.serviceConnection = serviceConnection;
    }

    @Override
    public void onPlaybackStateChanged(PlaybackStateCompat state) {
        if (state == null) {
            serviceConnection.setPlaybackState(PodcastServiceConnection.EMPTY_PLAYBACK_STATE);
        } else {
            serviceConnection.setPlaybackState(state);
        }
    }

    @Override
    public void onMetadataChanged(MediaMetadataCompat metadata) {
        if (metadata.getString(MediaMetadataCompat.METADATA_KEY_MEDIA_ID) == null) {
            serviceConnection.setNowPlaying(PodcastServiceConnection.NOTHING_PLAYING);
        } else {
            serviceConnection.setNowPlaying(metadata);
        }
    }

    @Override
    public void onSessionDestroyed() {
        serviceConnection.getPodcastBrowserConnectionCallback().onConnectionSuspended();
    }

    // TODO: handle network failure

}
