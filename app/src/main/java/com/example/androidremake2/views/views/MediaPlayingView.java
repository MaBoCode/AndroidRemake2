package com.example.androidremake2.views.views;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.example.androidremake2.databinding.MediaPlayingViewBinding;

import org.jetbrains.annotations.NotNull;

public class MediaPlayingView extends ConstraintLayout {

    protected MediaPlayingViewBinding binding;

    public MediaPlayingView(@NonNull @NotNull Context context, @Nullable @org.jetbrains.annotations.Nullable AttributeSet attrs) {
        super(context, attrs);
        binding = MediaPlayingViewBinding.inflate(
                LayoutInflater.from(context),
                this,
                true
        );
    }

    public MediaPlayingViewBinding getBinding() {
        return binding;
    }
}
