package com.example.androidremake2.views.podcast.fragments;

import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.lifecycle.ViewModelProvider;

import com.bumptech.glide.Glide;
import com.example.androidremake2.core.podcast.Podcast;
import com.example.androidremake2.core.podcast.PodcastEpisode;
import com.example.androidremake2.databinding.DlgModalBottomSheetBinding;
import com.example.androidremake2.injects.base.BaseComponent;
import com.example.androidremake2.utils.DimUtils;
import com.example.androidremake2.views.podcast.viewmodels.PodcastBottomSheetFragmentViewModel;
import com.google.android.exoplayer2.MediaItem;
import com.google.android.exoplayer2.Player;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

import java.util.Collections;
import java.util.List;

public class PodcastBottomSheetDialogFragment extends BottomSheetDialogFragment implements BaseComponent, Player.EventListener {

    protected DlgModalBottomSheetBinding binding;

    protected PodcastBottomSheetFragmentViewModel viewModel;

    protected SimpleExoPlayer simplePlayer;

    protected Podcast podcast;
    protected PodcastEpisode currentEpisode;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = DlgModalBottomSheetBinding.inflate(inflater, container, false);

        this.podcast = PodcastBottomSheetDialogFragmentArgs.fromBundle(getArguments()).getPodcast();
        Collections.sort(this.podcast.episodes);
        this.currentEpisode = this.podcast.getFirstEpisode();

        initViewModels();

        subscribeObservers();

        initPlayer();

        updateUI();

        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Dialog dialog = getDialog();
        dialog.setOnShowListener(new DialogInterface.OnShowListener() {
            @Override
            public void onShow(DialogInterface dialogInterface) {
                BottomSheetDialog dialog = (BottomSheetDialog) dialogInterface;
                FrameLayout bottomSheet = (FrameLayout) dialog.findViewById(com.google.android.material.R.id.design_bottom_sheet);
                BottomSheetBehavior.from(bottomSheet).setPeekHeight((int) DimUtils.dp2px(requireContext(), 1000), true);
            }
        });
    }

    public void initPlayer() {
        simplePlayer = new SimpleExoPlayer.Builder(requireContext())
                .build();
        simplePlayer.addListener(this);

        binding.playerView.setPlayer(simplePlayer);

        List<PodcastEpisode> episodes = this.podcast.episodes;

        for (PodcastEpisode episode : episodes) {
            MediaItem mediaItem = new MediaItem.Builder()
                    .setMediaId(episode.id)
                    .setUri(episode.audioUrl)
                    .build();
            simplePlayer.addMediaItem(mediaItem);
        }

        simplePlayer.prepare();
        simplePlayer.play();
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    public void onMediaItemTransition(@Nullable MediaItem mediaItem, int reason) {
        PodcastEpisode newEpisode = podcast.episodes
                .stream()
                .filter(episode -> episode.id.contentEquals(mediaItem.mediaId))
                .findAny()
                .orElse(null);
        this.currentEpisode = newEpisode;
        updateUI();
    }

    public void updateUI() {

        if (binding == null || this.podcast == null || this.currentEpisode == null)
            return;

        binding.txtPodcastTitle.setText(this.podcast.title);
        binding.txtPodcastEpisodeTitle.setText(this.currentEpisode.title);

        Glide.with(this).load(this.currentEpisode.imageUrl).into(binding.podcastImg);
    }

    @Override
    public void onStart() {
        super.onStart();
    }

    @Override
    public void initViewModels() {
        viewModel = new ViewModelProvider(this).get(PodcastBottomSheetFragmentViewModel.class);
    }

    @Override
    public void subscribeObservers() {

    }

    @Override
    public void onDestroyView() {
        Dialog dialog = getDialog();
        dialog.setOnShowListener(null);

        super.onDestroyView();
    }

    @Override
    public void onDestroy() {

        if (simplePlayer != null) {
            simplePlayer.removeListener(this);
            simplePlayer.release();
            simplePlayer = null;
        }

        binding = null;
        super.onDestroy();
    }
}
