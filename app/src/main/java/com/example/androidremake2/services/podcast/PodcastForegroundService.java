package com.example.androidremake2.services.podcast;

import android.app.PendingIntent;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.media.MediaBrowserCompat;
import android.support.v4.media.MediaMetadataCompat;
import android.support.v4.media.session.MediaSessionCompat;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.media.MediaBrowserServiceCompat;

import com.example.androidremake2.services.podcast.events.PlayerEventListener;
import com.example.androidremake2.services.podcast.events.PlayerNotificationListener;
import com.example.androidremake2.services.podcast.utils.PodcastNotificationManager;
import com.example.androidremake2.services.podcast.utils.PodcastPlaybackPreparer;
import com.example.androidremake2.services.podcast.utils.PodcastQueueNavigator;
import com.example.androidremake2.utils.Logs;
import com.google.android.exoplayer2.C;
import com.google.android.exoplayer2.ExoPlayer;
import com.google.android.exoplayer2.MediaItem;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.audio.AudioAttributes;
import com.google.android.exoplayer2.ext.mediasession.MediaSessionConnector;

import java.util.ArrayList;
import java.util.List;

public class PodcastForegroundService extends MediaBrowserServiceCompat {

    protected final static String MEDIA_ROOT_ID = "media_root_id";
    protected final static String EMPTY_MEDIA_ROOT_ID = "empty_root_id";

    public final static String MEDIA_DESCRIPTION_EXTRAS_START_PLAYBACK_POSITION_MS = "playback_start_position_ms";


    protected MediaSessionCompat mediaSession;
    protected MediaSessionConnector mediaSessionConnector;

    protected ExoPlayer exoPlayer;
    protected AudioAttributes audioAttributes;

    protected PlayerEventListener playerEventListener = new PlayerEventListener(this);

    protected PodcastNotificationManager notificationManager;

    public boolean isForeground = false;

    protected List<MediaMetadataCompat> currentEpisodes = new ArrayList<>();

    @Override
    public void onCreate() {
        super.onCreate();

        Logs.debug(this, "create PodcastService");

        this.audioAttributes = new AudioAttributes.Builder()
                .setContentType(C.CONTENT_TYPE_MUSIC)
                .setUsage(C.USAGE_MEDIA)
                .build();

        this.exoPlayer = new SimpleExoPlayer.Builder(this).build();
        ((SimpleExoPlayer) this.exoPlayer).setAudioAttributes(this.audioAttributes, true);
        ((SimpleExoPlayer) this.exoPlayer).setHandleAudioBecomingNoisy(true);
        this.exoPlayer.addListener(playerEventListener);

        PendingIntent pendingIntent = PendingIntent.getActivity(
                this,
                0,
                getPackageManager().getLaunchIntentForPackage(getPackageName()),
                0);

        this.mediaSession = new MediaSessionCompat(getApplicationContext(), Logs.tag);
        this.mediaSession.setActive(true);
        this.mediaSession.setSessionActivity(pendingIntent);
        setSessionToken(mediaSession.getSessionToken());

        this.notificationManager = new PodcastNotificationManager(this,
                mediaSession.getSessionToken(),
                new PlayerNotificationListener(this));

        this.mediaSessionConnector = new MediaSessionConnector(mediaSession);
        this.mediaSessionConnector.setPlaybackPreparer(new PodcastPlaybackPreparer(this));
        this.mediaSessionConnector.setQueueNavigator(new PodcastQueueNavigator(mediaSession, this));

        this.notificationManager.showNotificationForPLayer(exoPlayer);
    }

    public void preparePlaylist(MediaMetadataCompat itemToPlay, List<MediaMetadataCompat> metadataList, Long playbackStartPositionMs) {
        int initialWindowIndex = itemToPlay == null ? 0 : metadataList.indexOf(itemToPlay);
        this.currentEpisodes = metadataList;

        this.exoPlayer.stop();

        List<MediaItem> mediaItems = new ArrayList<>();
        for (MediaMetadataCompat mediaMetadataCompat : metadataList) {
            MediaItem mediaItem = new MediaItem.Builder()
                    .setMediaId(mediaMetadataCompat.getDescription().getMediaId())
                    .setUri(mediaMetadataCompat.getDescription().getMediaUri())
                    .build();
            mediaItems.add(mediaItem);
        }

        this.exoPlayer.addMediaItems(mediaItems);
        this.exoPlayer.prepare();
        this.exoPlayer.seekTo(initialWindowIndex, playbackStartPositionMs);
    }

    @Nullable
    @Override
    public BrowserRoot onGetRoot(@NonNull String clientPackageName, int clientUid, @Nullable Bundle rootHints) {
        // Allow service connection and media browsing
        return new BrowserRoot(EMPTY_MEDIA_ROOT_ID, null);
    }

    @Override
    public void onLoadChildren(@NonNull String parentId, @NonNull Result<List<MediaBrowserCompat.MediaItem>> result) {

        Logs.debug(this, "[DBG] loadChildren");

        // Browsing not allowed
        if (parentId.contentEquals(EMPTY_MEDIA_ROOT_ID)) {
            result.sendResult(null);
        }
    }

    @Override
    public void onTaskRemoved(Intent rootIntent) {
        super.onTaskRemoved(rootIntent);

        exoPlayer.stop();
        exoPlayer.removeListener(playerEventListener);
        exoPlayer.release();
    }

    @Override
    public void onDestroy() {

        if (mediaSession != null) {
            mediaSession.setActive(false);
            mediaSession.release();
        }

        if (exoPlayer != null) {
            exoPlayer.release();
        }

        super.onDestroy();
    }

    public ExoPlayer getExoPlayer() {
        return exoPlayer;
    }

    public PodcastNotificationManager getNotificationManager() {
        return notificationManager;
    }

    public List<MediaMetadataCompat> getCurrentEpisodes() {
        return currentEpisodes;
    }
}
