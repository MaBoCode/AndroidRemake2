package com.example.androidremake2.services.podcast.utils;

import android.net.Uri;
import android.os.Bundle;
import android.os.ResultReceiver;
import android.support.v4.media.session.PlaybackStateCompat;

import androidx.annotation.Nullable;

import com.example.androidremake2.services.podcast.PodcastForegroundService;
import com.google.android.exoplayer2.ControlDispatcher;
import com.google.android.exoplayer2.Player;
import com.google.android.exoplayer2.ext.mediasession.MediaSessionConnector;

public class PodcastPlaybackPreparer implements MediaSessionConnector.PlaybackPreparer {

    protected PodcastForegroundService service;

    public PodcastPlaybackPreparer(PodcastForegroundService service) {
        this.service = service;
    }

    @Override
    public long getSupportedPrepareActions() {
        return PlaybackStateCompat.ACTION_PREPARE_FROM_MEDIA_ID ^
                PlaybackStateCompat.ACTION_PLAY_FROM_MEDIA_ID;
    }

    @Override
    public void onPrepare(boolean playWhenReady) {

    }

    @Override
    public void onPrepareFromMediaId(String mediaId, boolean playWhenReady, @Nullable @org.jetbrains.annotations.Nullable Bundle extras) {
        service.preparePlaylist();
    }

    @Override
    public void onPrepareFromSearch(String query, boolean playWhenReady, @Nullable @org.jetbrains.annotations.Nullable Bundle extras) {

    }

    @Override
    public void onPrepareFromUri(Uri uri, boolean playWhenReady, @Nullable @org.jetbrains.annotations.Nullable Bundle extras) {

    }

    @Override
    public boolean onCommand(Player player, ControlDispatcher controlDispatcher, String command, @Nullable @org.jetbrains.annotations.Nullable Bundle extras, @Nullable @org.jetbrains.annotations.Nullable ResultReceiver cb) {
        return false;
    }
}
