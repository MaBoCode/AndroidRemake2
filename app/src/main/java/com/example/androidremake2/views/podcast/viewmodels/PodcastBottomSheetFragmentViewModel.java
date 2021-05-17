package com.example.androidremake2.views.podcast.viewmodels;

import android.os.Build;
import android.support.v4.media.MediaMetadataCompat;
import android.support.v4.media.session.MediaControllerCompat;
import android.support.v4.media.session.PlaybackStateCompat;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.hilt.Assisted;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.SavedStateHandle;
import androidx.lifecycle.Transformations;

import com.example.androidremake2.core.podcast.Podcast;
import com.example.androidremake2.core.podcast.PodcastEpisode;
import com.example.androidremake2.injects.base.BaseViewModel;
import com.example.androidremake2.services.podcast.PodcastServiceConnection;
import com.google.android.exoplayer2.MediaItem;
import com.google.android.exoplayer2.Player;

import javax.inject.Inject;

import dagger.hilt.android.lifecycle.HiltViewModel;

@HiltViewModel
public class PodcastBottomSheetFragmentViewModel extends BaseViewModel {

    protected PodcastServiceConnection podcastServiceConnection;

    protected LiveData<String> rootMediaId;

    public Podcast playingPodcast;

    protected MutableLiveData<PodcastEpisode> _playingPodcastEpisode = new MutableLiveData<>();
    public LiveData<PodcastEpisode> playingPodcastEpisode = _playingPodcastEpisode;

    protected Player.EventListener mediaTransitionListener = new Player.EventListener() {
        @RequiresApi(api = Build.VERSION_CODES.N)
        @Override
        public void onMediaItemTransition(@Nullable @org.jetbrains.annotations.Nullable MediaItem mediaItem, int reason) {

            if (mediaItem == null) {
                return;
            }

            PodcastEpisode playingEpisode = playingPodcast.episodes
                    .stream()
                    .filter(episode -> episode.id.contentEquals(mediaItem.mediaId))
                    .findAny()
                    .orElse(null);
            if (playingEpisode == null) {
                return;
            }

            _playingPodcastEpisode.postValue(playingEpisode);
        }
    };

    @Inject
    public PodcastBottomSheetFragmentViewModel(PodcastServiceConnection podcastServiceConnection, @Assisted SavedStateHandle savedStateHandle) {
        this.podcastServiceConnection = podcastServiceConnection;
        this.savedStateHandle = savedStateHandle;

        this.rootMediaId = Transformations.map(podcastServiceConnection.isConnected, isConnected -> {
            return isConnected ? podcastServiceConnection.rootMediaId : null;
        });
    }

    public void playPodcastEpisode(PodcastEpisode episode) {
        MediaMetadataCompat nowPlaying = podcastServiceConnection.nowPlaying.getValue();
        MediaControllerCompat.TransportControls transportControls = podcastServiceConnection.transportControls;

        PlaybackStateCompat playbackState = podcastServiceConnection.playbackState.getValue();

        if (playbackState == null) {
            return;
        }

        boolean isPrepared = playbackState.getState() == PlaybackStateCompat.STATE_BUFFERING
                || playbackState.getState() == PlaybackStateCompat.STATE_PLAYING
                || playbackState.getState() == PlaybackStateCompat.STATE_PAUSED;

        if (nowPlaying != null && isPrepared && episode.id.contentEquals(nowPlaying.getString(MediaMetadataCompat.METADATA_KEY_MEDIA_ID))) {
            // If isPlaying
            if (playbackState.getState() == PlaybackStateCompat.STATE_BUFFERING
                    || playbackState.getState() == PlaybackStateCompat.STATE_PLAYING) {
                transportControls.pause();
            } else {
                transportControls.play();
            }
        } else {
            transportControls.playFromMediaId(episode.id, null);
        }
    }

    public void getNextEpisode() {
        this._playingPodcastEpisode.postValue(playingPodcast.getNextEpisode());
    }

    public void setPlayingPodcast(Podcast playingPodcast) {
        this.playingPodcast = playingPodcast;
    }
}
