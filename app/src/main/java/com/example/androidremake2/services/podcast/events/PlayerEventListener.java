package com.example.androidremake2.services.podcast.events;

import com.example.androidremake2.services.podcast.PodcastForegroundService;
import com.example.androidremake2.utils.Logs;
import com.google.android.exoplayer2.ExoPlaybackException;
import com.google.android.exoplayer2.Player;

import org.jetbrains.annotations.NotNull;

public class PlayerEventListener implements Player.EventListener {

    private final PodcastForegroundService service;

    public PlayerEventListener(PodcastForegroundService service) {
        this.service = service;
    }

    @Override
    public void onPlayWhenReadyChanged(boolean playWhenReady, int reason) {
        if (!playWhenReady) {
            service.stopForeground(false);
        }
    }

    @Override
    public void onPlaybackStateChanged(int state) {
        Logs.debug(this, "");
        if (state == Player.STATE_BUFFERING || state == Player.STATE_READY) {
            service.getNotificationManager().showNotificationForPLayer(service.getExoPlayer());
        } else {
            service.getNotificationManager().hideNotification();
        }
    }

    @Override
    public void onPlayerError(@NotNull ExoPlaybackException error) {
        switch (error.type) {
            case ExoPlaybackException.TYPE_SOURCE:
                Logs.error(this, error.getSourceException().getMessage());
                break;
            case ExoPlaybackException.TYPE_RENDERER:
                Logs.error(this, error.getRendererException().getMessage());
                break;
            case ExoPlaybackException.TYPE_UNEXPECTED:
                Logs.error(this, error.getUnexpectedException().getMessage());
                break;
            default:
                Logs.error(this, error.getMessage());
        }
    }
}
