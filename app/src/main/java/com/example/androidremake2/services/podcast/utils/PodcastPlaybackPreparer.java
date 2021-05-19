package com.example.androidremake2.services.podcast.utils;

import android.net.Uri;
import android.os.Bundle;
import android.os.ResultReceiver;
import android.support.v4.media.MediaMetadataCompat;
import android.support.v4.media.session.PlaybackStateCompat;

import androidx.annotation.Nullable;

import com.example.androidremake2.services.podcast.PodcastForegroundService;
import com.example.androidremake2.utils.Logs;
import com.google.android.exoplayer2.C;
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
                PlaybackStateCompat.ACTION_PLAY_FROM_MEDIA_ID ^
                PlaybackStateCompat.ACTION_PREPARE_FROM_SEARCH ^
                PlaybackStateCompat.ACTION_PLAY_FROM_SEARCH;
    }

    @Override
    public void onPrepare(boolean playWhenReady) {

    }

    @Override
    public void onPrepareFromMediaId(String mediaId, boolean playWhenReady, @Nullable @org.jetbrains.annotations.Nullable Bundle extras) {
        MediaMetadataCompat itemToPlay = null;
        for (MediaMetadataCompat metadata : service.getCurrentEpisodes()) {
            if (metadata.getDescription().getMediaId().contentEquals(mediaId)) {
                itemToPlay = metadata;
                break;
            }
        }

        Logs.debug(this, "[DBG] id: " + mediaId);

        Long playbackStartPositionMs = C.TIME_UNSET;
        if (extras != null) {
            playbackStartPositionMs = extras.getLong(
                    PodcastForegroundService.MEDIA_DESCRIPTION_EXTRAS_START_PLAYBACK_POSITION_MS,
                    C.TIME_UNSET);
        }
        service.preparePlaylist(itemToPlay, service.getCurrentEpisodes(), playbackStartPositionMs);
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
