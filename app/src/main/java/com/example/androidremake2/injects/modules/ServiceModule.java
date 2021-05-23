package com.example.androidremake2.injects.modules;

import android.content.ComponentName;
import android.content.Context;

import com.example.androidremake2.services.podcast.PodcastForegroundService;
import com.example.androidremake2.services.podcast.PodcastServiceConnection;
import com.google.android.exoplayer2.C;
import com.google.android.exoplayer2.ExoPlayer;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.audio.AudioAttributes;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import dagger.hilt.InstallIn;
import dagger.hilt.android.qualifiers.ApplicationContext;
import dagger.hilt.components.SingletonComponent;

@Module
@InstallIn(SingletonComponent.class)
public class ServiceModule {

    @Provides
    @Singleton
    public PodcastServiceConnection providePodcastServiceConnection(@ApplicationContext Context context) {
        return new PodcastServiceConnection(context, new ComponentName(context, PodcastForegroundService.class));
    }

    @Provides
    @Singleton
    public ExoPlayer provideExoPlayer(@ApplicationContext Context context) {
        AudioAttributes audioAttributes = new AudioAttributes.Builder()
                .setContentType(C.CONTENT_TYPE_MUSIC)
                .setUsage(C.USAGE_MEDIA)
                .build();

        return new SimpleExoPlayer
                .Builder(context)
                .setAudioAttributes(audioAttributes, true)
                .setHandleAudioBecomingNoisy(true)
                .build();
    }

}
