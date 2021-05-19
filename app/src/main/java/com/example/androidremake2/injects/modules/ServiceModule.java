package com.example.androidremake2.injects.modules;

import android.content.ComponentName;
import android.content.Context;

import com.example.androidremake2.services.podcast.PodcastForegroundService;
import com.example.androidremake2.services.podcast.PodcastServiceConnection;

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

}
