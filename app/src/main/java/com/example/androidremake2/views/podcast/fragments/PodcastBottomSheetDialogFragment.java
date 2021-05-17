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
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.bumptech.glide.Glide;
import com.example.androidremake2.core.podcast.Podcast;
import com.example.androidremake2.core.podcast.PodcastEpisode;
import com.example.androidremake2.databinding.FrgDlgModalBottomSheetBinding;
import com.example.androidremake2.injects.base.BaseBottomSheetDialogFragment;
import com.example.androidremake2.injects.base.BaseComponent;
import com.example.androidremake2.utils.DimUtils;
import com.example.androidremake2.utils.Logs;
import com.example.androidremake2.views.podcast.viewmodels.PodcastBottomSheetFragmentViewModel;
import com.google.android.exoplayer2.MediaItem;
import com.google.android.exoplayer2.Player;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialog;

import org.androidannotations.annotations.EFragment;

import java.util.List;

import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
@EFragment
public class PodcastBottomSheetDialogFragment extends BaseBottomSheetDialogFragment implements BaseComponent, Player.EventListener {

    protected FrgDlgModalBottomSheetBinding binding;

    protected PodcastBottomSheetFragmentViewModel viewModel;

    protected SimpleExoPlayer simplePlayer;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        super.onCreateView(inflater, container, savedInstanceState);

        binding = FrgDlgModalBottomSheetBinding.inflate(inflater, container, false);

        Podcast playingPodcast = PodcastBottomSheetDialogFragmentArgs.fromBundle(getArguments()).getPodcast();
        viewModel.setPlayingPodcast(playingPodcast);

        viewModel.getNextEpisode();

        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        Dialog dialog = getDialog();

        if (dialog == null) {
            return;
        }

        dialog.setOnShowListener(new DialogInterface.OnShowListener() {
            @Override
            public void onShow(DialogInterface dialogInterface) {
                BottomSheetDialog dialog = (BottomSheetDialog) dialogInterface;
                FrameLayout bottomSheet = dialog.findViewById(com.google.android.material.R.id.design_bottom_sheet);

                if (bottomSheet == null) {
                    return;
                }
                BottomSheetBehavior.from(bottomSheet).setPeekHeight((int) DimUtils.dp2px(requireContext(), 1000), true);
            }
        });
    }

    public void displayPlayingEpisode(PodcastEpisode episode) {

        Podcast playingPodcast = viewModel.playingPodcast;

        if (playingPodcast == null || episode == null) {
            Logs.debug(this, "[DBG] here");
            return;
        }

        binding.txtPodcastTitle.setText(playingPodcast.title);
        binding.txtPodcastEpisodeTitle.setText(episode.title);

        Glide.with(this).load(episode.imageUrl).into(binding.podcastImg);
    }

    public void initPlayer() {
        simplePlayer = new SimpleExoPlayer.Builder(requireContext())
                .build();
        simplePlayer.addListener(this);

        binding.playerView.setPlayer(simplePlayer);

        List<PodcastEpisode> episodes = viewModel.playingPodcast.episodes;

        for (PodcastEpisode episode : episodes) {
            MediaItem mediaItem = new MediaItem.Builder()
                    .setMediaId(episode.id)
                    .setUri(episode.audioUrl)
                    .build();
            simplePlayer.addMediaItem(mediaItem);
        }

        //simplePlayer.prepare();
        //simplePlayer.play();
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    public void onMediaItemTransition(@Nullable MediaItem mediaItem, int reason) {
        // Add this listener in viewmodel and post new playing Episode value
    }

    @Override
    public void initViewModels() {
        viewModel = new ViewModelProvider(this).get(PodcastBottomSheetFragmentViewModel.class);
    }

    @Override
    public void subscribeObservers() {
        viewModel.playingPodcastEpisode.observe(getViewLifecycleOwner(), new Observer<PodcastEpisode>() {
            @Override
            public void onChanged(PodcastEpisode episode) {
                displayPlayingEpisode(episode);
            }
        });
    }

    @Override
    public void unsubscribeObservers() {
        viewModel.playingPodcastEpisode.removeObservers(getViewLifecycleOwner());
    }

    @Override
    public void onDestroyView() {
        Dialog dialog = getDialog();
        if (dialog != null) {
            dialog.setOnShowListener(null);
        }

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
