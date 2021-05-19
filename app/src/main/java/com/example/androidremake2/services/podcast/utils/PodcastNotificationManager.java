package com.example.androidremake2.services.podcast.utils;

import android.content.Context;
import android.support.v4.media.session.MediaControllerCompat;
import android.support.v4.media.session.MediaSessionCompat;

import com.example.androidremake2.R;
import com.google.android.exoplayer2.DefaultControlDispatcher;
import com.google.android.exoplayer2.ExoPlayer;
import com.google.android.exoplayer2.ui.PlayerNotificationManager;

public class PodcastNotificationManager {

    protected final static String PLAYBACK_CHANNEL_ID = "playback_channel";
    protected final static int PLAYBACK_NOTIFICATION_ID = 1;

    protected Context context;

    protected MediaSessionCompat.Token sessionToken;

    protected PlayerNotificationManager notificationManager;
    protected PlayerNotificationManager.NotificationListener notificationListener;

    protected ExoPlayer player;

    public PodcastNotificationManager(Context context, MediaSessionCompat.Token sessionToken, PlayerNotificationManager.NotificationListener notificationListener) {
        this.context = context;
        this.sessionToken = sessionToken;
        this.notificationListener = notificationListener;

        MediaControllerCompat mediaController = new MediaControllerCompat(context, sessionToken);
        this.notificationManager = PlayerNotificationManager.createWithNotificationChannel(
                context,
                PLAYBACK_CHANNEL_ID,
                R.string.notification_channel,
                R.string.notification_channel_description,
                PLAYBACK_NOTIFICATION_ID,
                new PodcastDescriptionAdapter(context, mediaController),
                notificationListener
        );
        this.notificationManager.setMediaSessionToken(sessionToken);
        this.notificationManager.setSmallIcon(R.drawable.exo_notification_small_icon);
        this.notificationManager.setControlDispatcher(new DefaultControlDispatcher(0, 0));
    }

    public void showNotificationForPLayer(ExoPlayer player) {
        notificationManager.setPlayer(player);
    }

    public void hideNotification() {
        this.notificationManager.setPlayer(null);
    }
}
