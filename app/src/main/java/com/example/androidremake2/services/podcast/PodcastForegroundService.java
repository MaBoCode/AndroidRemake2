package com.example.androidremake2.services.podcast;

import android.app.PendingIntent;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.media.MediaBrowserCompat;
import android.support.v4.media.MediaDescriptionCompat;
import android.support.v4.media.session.MediaSessionCompat;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.media.MediaBrowserServiceCompat;

import com.example.androidremake2.core.podcast.Podcast;
import com.example.androidremake2.core.podcast.PodcastEpisode;
import com.example.androidremake2.core.podcast.PodcastService;
import com.example.androidremake2.services.podcast.events.PlayerEventListener;
import com.example.androidremake2.services.podcast.events.PlayerNotificationListener;
import com.example.androidremake2.services.podcast.utils.PodcastNotificationManager;
import com.example.androidremake2.services.podcast.utils.PodcastPlaybackPreparer;
import com.example.androidremake2.services.podcast.utils.PodcastQueueNavigator;
import com.example.androidremake2.utils.Logs;
import com.google.android.exoplayer2.ExoPlayer;
import com.google.android.exoplayer2.MediaItem;
import com.google.android.exoplayer2.ext.mediasession.MediaSessionConnector;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import dagger.hilt.android.AndroidEntryPoint;
import io.reactivex.rxjava3.disposables.Disposable;
import io.reactivex.rxjava3.functions.Action;
import io.reactivex.rxjava3.functions.Consumer;
import io.reactivex.rxjava3.schedulers.Schedulers;

@AndroidEntryPoint
public class PodcastForegroundService extends MediaBrowserServiceCompat {

    protected final static String MEDIA_ROOT_ID = "media_root_id";
    public final static String EMPTY_MEDIA_ROOT_ID = "empty_root_id";

    @Inject
    protected PodcastService podcastService;

    @Inject
    protected ExoPlayer exoPlayer;

    protected MediaSessionCompat mediaSession;
    protected MediaSessionConnector mediaSessionConnector;

    protected PlayerEventListener playerEventListener;

    protected PodcastNotificationManager notificationManager;

    public boolean isForeground = false;

    public List<MediaBrowserCompat.MediaItem> episodes = new ArrayList<>();

    @Override
    public void onCreate() {
        super.onCreate();

        Logs.debug(this, "create PodcastService");

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
        this.mediaSessionConnector.setPlayer(this.exoPlayer);

        this.playerEventListener = new PlayerEventListener(this);
        this.exoPlayer.addListener(playerEventListener);

        this.notificationManager.showNotificationForPLayer(exoPlayer);
    }

    public void preparePlaylist() {
        Logs.debug(this, "");
        List<MediaItem> mediaItems = new ArrayList<>();
        for (MediaBrowserCompat.MediaItem mediaItem : episodes) {
            MediaItem item = new MediaItem.Builder()
                    .setMediaId(mediaItem.getMediaId())
                    .setUri(mediaItem.getDescription().getMediaUri())
                    .build();
            mediaItems.add(item);
        }
        this.exoPlayer.setPlayWhenReady(true);
        this.exoPlayer.addMediaItems(mediaItems);
        this.exoPlayer.prepare();
    }

    @Nullable
    @Override
    public BrowserRoot onGetRoot(@NonNull String clientPackageName, int clientUid, @Nullable Bundle rootHints) {
        // Allow service connection and media browsing
        return new BrowserRoot(EMPTY_MEDIA_ROOT_ID, null);
    }

    @Override
    public void onLoadChildren(@NonNull String parentId, @NonNull Result<List<MediaBrowserCompat.MediaItem>> result) {

        Logs.debug(this, "");

        if (parentId.contentEquals(EMPTY_MEDIA_ROOT_ID)) {
            result.detach();
            podcastService.getPodcast(parentId)
                    .subscribeOn(Schedulers.io())
                    .observeOn(Schedulers.io())
                    .doOnSubscribe(new Consumer<Disposable>() {
                        @Override
                        public void accept(Disposable disposable) throws Throwable {
                        }
                    })
                    .doFinally(new Action() {
                        @Override
                        public void run() throws Throwable {
                        }
                    })
                    .subscribe(new Consumer<Podcast>() {
                        @Override
                        public void accept(Podcast podcast) throws Throwable {
                            List<MediaBrowserCompat.MediaItem> mediaItems = new ArrayList<>();
                            for (PodcastEpisode episode : podcast.episodes) {
                                MediaDescriptionCompat mediaDescription = new MediaDescriptionCompat.Builder()
                                        .setMediaId(episode.id)
                                        .setIconUri(Uri.parse(episode.imageUrl))
                                        .setMediaUri(Uri.parse(episode.audioUrl))
                                        .setTitle(episode.title)
                                        .setDescription(episode.description)
                                        .build();
                                MediaBrowserCompat.MediaItem mediaItem = new MediaBrowserCompat.MediaItem(mediaDescription, MediaBrowserCompat.MediaItem.FLAG_PLAYABLE);
                                mediaItems.add(mediaItem);
                            }
                            PodcastForegroundService.this.episodes = mediaItems;
                            result.sendResult(mediaItems);
                            Logs.debug(this, "sent " + mediaItems.size() + " items");
                        }
                    }, new Consumer<Throwable>() {
                        @Override
                        public void accept(Throwable throwable) throws Throwable {
                            Logs.error(this, throwable.getLocalizedMessage());
                        }
                    });
        }
    }

    @Override
    public void onTaskRemoved(Intent rootIntent) {
        super.onTaskRemoved(rootIntent);

        exoPlayer.stop();
    }

    @Override
    public void onDestroy() {

        if (exoPlayer != null) {
            exoPlayer.removeListener(this.playerEventListener);
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
}
