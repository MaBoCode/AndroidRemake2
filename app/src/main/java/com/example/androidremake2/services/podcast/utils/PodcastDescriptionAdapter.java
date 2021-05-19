package com.example.androidremake2.services.podcast.utils;

import android.app.PendingIntent;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.support.v4.media.session.MediaControllerCompat;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.CustomTarget;
import com.bumptech.glide.request.transition.Transition;
import com.example.androidremake2.utils.Logs;
import com.google.android.exoplayer2.Player;
import com.google.android.exoplayer2.ui.PlayerNotificationManager;

import org.jetbrains.annotations.NotNull;

public class PodcastDescriptionAdapter implements PlayerNotificationManager.MediaDescriptionAdapter {

    protected Context context;

    protected MediaControllerCompat controller;

    protected Uri currentUri = null;
    protected Bitmap currentBitmap = null;

    public PodcastDescriptionAdapter(Context context, MediaControllerCompat controller) {
        this.context = context;
        this.controller = controller;
    }

    @Override
    public CharSequence getCurrentContentTitle(Player player) {
        return controller.getMetadata().getDescription().getTitle();
    }

    @Nullable
    @org.jetbrains.annotations.Nullable
    @Override
    public PendingIntent createCurrentContentIntent(Player player) {
        return controller.getSessionActivity();
    }

    @Nullable
    @org.jetbrains.annotations.Nullable
    @Override
    public CharSequence getCurrentContentText(Player player) {
        return controller.getMetadata().getDescription().getSubtitle();
    }

    @Nullable
    @org.jetbrains.annotations.Nullable
    @Override
    public Bitmap getCurrentLargeIcon(Player player, PlayerNotificationManager.BitmapCallback callback) {
        Uri imgUri = controller.getMetadata().getDescription().getIconUri();

        Logs.debug(this, "largeIcon: " + imgUri);

        if (currentUri != imgUri || currentBitmap == null) {
            Glide
                    .with(context)
                    .asBitmap()
                    .load(imgUri)
                    .into(new CustomTarget<Bitmap>() {
                        @Override
                        public void onResourceReady(@NonNull @NotNull Bitmap resource, @Nullable @org.jetbrains.annotations.Nullable Transition<? super Bitmap> transition) {
                            callback.onBitmap(resource);
                        }

                        @Override
                        public void onLoadCleared(@Nullable @org.jetbrains.annotations.Nullable Drawable placeholder) {

                        }
                    });
            return null;
        } else {
            return currentBitmap;
        }
    }
}
