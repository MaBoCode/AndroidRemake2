package com.example.androidremake2.services.podcast.events;

import android.app.Notification;
import android.content.Intent;

import androidx.core.content.ContextCompat;

import com.example.androidremake2.services.podcast.PodcastForegroundService;
import com.example.androidremake2.services.podcast.utils.PodcastNotificationManager;
import com.example.androidremake2.utils.Logs;
import com.google.android.exoplayer2.ui.PlayerNotificationManager;

public class PlayerNotificationListener implements PlayerNotificationManager.NotificationListener {

    protected PodcastForegroundService service;

    public PlayerNotificationListener(PodcastForegroundService service) {
        this.service = service;
    }

    @Override
    public void onNotificationPosted(int notificationId, Notification notification, boolean ongoing) {
        Logs.debug(this, "notification posted");
        if (ongoing && !service.isForeground) {
            ContextCompat.startForegroundService(
                    service,
                    new Intent(service.getApplicationContext(), PodcastForegroundService.class));
            service.startForeground(PodcastNotificationManager.PLAYBACK_NOTIFICATION_ID, notification);
            service.isForeground = true;
        }
    }

    @Override
    public void onNotificationCancelled(int notificationId, boolean dismissedByUser) {
        service.stopForeground(true);
        service.isForeground = false;
        service.stopSelf();
    }

}
