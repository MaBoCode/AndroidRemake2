package com.example.androidremake2.injects.base;

import android.animation.Animator;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.LinearInterpolator;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.interpolator.view.animation.FastOutSlowInInterpolator;

import com.example.androidremake2.R;
import com.example.androidremake2.views.utils.AnimationUtils;
import com.example.androidremake2.views.views.MediaPlayingView;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.List;

public abstract class BaseFragment extends Fragment implements BaseComponent {

    @Nullable
    @org.jetbrains.annotations.Nullable
    @Override
    public View onCreateView(@NonNull @NotNull LayoutInflater inflater, @Nullable @org.jetbrains.annotations.Nullable ViewGroup container, @Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {

        initViewModels();

        subscribeObservers();

        return super.onCreateView(inflater, container, savedInstanceState);
    }

    @Override
    public void initViewModels() {

    }

    @Override
    public void subscribeObservers() {

    }

    @Override
    public void unsubscribeObservers() {

    }

    public void showHideLoader(BaseViewModel.LoadingStatus status) {
        int visibility = status == BaseViewModel.LoadingStatus.LOADING ? View.VISIBLE : View.GONE;
        requireActivity().findViewById(R.id.loader).setVisibility(visibility);
    }

    public void showBottomNavView(@Nullable List<Animator> concurrentAnimations) {
        BottomNavigationView bottomNav = requireActivity().findViewById(R.id.bottomNavView);

        if (bottomNav.getVisibility() == View.VISIBLE) {
            return;
        }

        bottomNav.setVisibility(View.VISIBLE);

        List<Animator> animators = new AnimationUtils.Builder()
                .setObjects(Arrays.asList(bottomNav))
                .setAnimateAlphaIn(true)
                .setTranslationYBegin(bottomNav.getTranslationY())
                .setInterpolator(new FastOutSlowInInterpolator())
                .getAnimators();

        if (concurrentAnimations != null && !concurrentAnimations.isEmpty()) {
            animators.addAll(concurrentAnimations);
        }

        AnimationUtils.animate(animators, AnimationUtils.ANIMATION_DURATION, 0, new LinearInterpolator());

        for (Animator animator : animators) {
            animator.removeAllListeners();
        }
    }

    public void hideBottomNavView(@Nullable List<Animator> concurrentAnimations) {
        BottomNavigationView bottomNav = requireActivity().findViewById(R.id.bottomNavView);

        if (bottomNav.getVisibility() == View.GONE) {
            return;
        }

        List<Animator> animators = new AnimationUtils.Builder()
                .setObjects(Arrays.asList(bottomNav))
                .setAnimateAlphaOut(true)
                .setTranslationYEnd(bottomNav.getHeight())
                .setInterpolator(new LinearInterpolator())
                .getAnimators();

        if (concurrentAnimations != null && !concurrentAnimations.isEmpty()) {
            animators.addAll(concurrentAnimations);
        }

        AnimationUtils.animate(animators, AnimationUtils.ANIMATION_DURATION, 0, new LinearInterpolator());

        for (Animator animator : animators) {
            animator.removeAllListeners();
        }

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                bottomNav.setVisibility(View.GONE);
            }
        }, 300);
    }

    public void showMediaPlayingView() {
        MediaPlayingView mediaPlayingView = requireActivity().findViewById(R.id.mediaPlayingView);

        if (mediaPlayingView.getVisibility() == View.VISIBLE) {
            return;
        }

        mediaPlayingView.setVisibility(View.VISIBLE);

        new AnimationUtils.Builder()
                .setObjects(Arrays.asList(mediaPlayingView))
                .setAnimateAlphaIn(true)
                .setTranslationYBegin(100f)
                .setInterpolator(new FastOutSlowInInterpolator())
                .start();
    }

    public void hideMediaPlayingView() {
        MediaPlayingView mediaPlayingView = requireActivity().findViewById(R.id.mediaPlayingView);

        if (mediaPlayingView.getVisibility() == View.GONE) {
            return;
        }

        new AnimationUtils.Builder()
                .setObjects(Arrays.asList(mediaPlayingView))
                .setAnimateAlphaOut(true)
                .setTranslationYEnd(100f)
                .setInterpolator(new FastOutSlowInInterpolator())
                .start();

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                mediaPlayingView.setVisibility(View.VISIBLE);

            }
        }, AnimationUtils.ANIMATION_DURATION);
    }

    @Override
    public void onDestroyView() {

        super.onDestroyView();
    }
}
