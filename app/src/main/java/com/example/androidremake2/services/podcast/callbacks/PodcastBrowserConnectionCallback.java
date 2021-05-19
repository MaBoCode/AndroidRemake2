package com.example.androidremake2.services.podcast.callbacks;

import android.content.Context;
import android.support.v4.media.MediaBrowserCompat;
import android.support.v4.media.session.MediaControllerCompat;

import com.example.androidremake2.services.podcast.PodcastServiceConnection;
import com.example.androidremake2.utils.Logs;

public class PodcastBrowserConnectionCallback extends MediaBrowserCompat.ConnectionCallback {

    protected Context context;

    protected PodcastServiceConnection serviceConnection;

    public PodcastBrowserConnectionCallback(Context context, PodcastServiceConnection serviceConnection) {
        this.context = context;
        this.serviceConnection = serviceConnection;
    }

    @Override
    public void onConnected() {
        Logs.debug(this, "connected to PodcastService");
        MediaBrowserCompat mediaBrowser = serviceConnection.getMediaBrowser();


        MediaControllerCompat mediaController = new MediaControllerCompat(context, mediaBrowser.getSessionToken());
        mediaController.registerCallback(new PodcastControllerCallback(serviceConnection));

        MediaControllerCompat.TransportControls transportControls = mediaController.getTransportControls();

        serviceConnection.setMediaController(mediaController);
        serviceConnection.setTransportControls(transportControls);
        serviceConnection.setRootMediaId(mediaBrowser.getRoot());

        serviceConnection.setIsConnected(true);
    }

    @Override
    public void onConnectionSuspended() {
        Logs.error(this, "PodcastService connection suspended");
        serviceConnection.setIsConnected(false);
    }

    @Override
    public void onConnectionFailed() {
        Logs.error(this, "PodcastService connection failed");
        serviceConnection.setIsConnected(false);
    }

}
