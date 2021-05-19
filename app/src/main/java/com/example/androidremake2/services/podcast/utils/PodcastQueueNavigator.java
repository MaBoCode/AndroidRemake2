package com.example.androidremake2.services.podcast.utils;

import android.support.v4.media.MediaDescriptionCompat;
import android.support.v4.media.session.MediaSessionCompat;

import com.example.androidremake2.services.podcast.PodcastForegroundService;
import com.google.android.exoplayer2.Player;
import com.google.android.exoplayer2.ext.mediasession.TimelineQueueNavigator;

public class PodcastQueueNavigator extends TimelineQueueNavigator {

    protected PodcastForegroundService service;

    public PodcastQueueNavigator(MediaSessionCompat mediaSession, PodcastForegroundService service) {
        super(mediaSession);
    }

    @Override
    public MediaDescriptionCompat getMediaDescription(Player player, int windowIndex) {
        return service.getCurrentEpisodes().get(windowIndex).getDescription();
    }
}
