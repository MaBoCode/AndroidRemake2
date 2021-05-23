package com.example.androidremake2.views.podcast.fragments;

import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.media.MediaBrowserCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.bumptech.glide.Glide;
import com.example.androidremake2.R;
import com.example.androidremake2.core.podcast.Podcast;
import com.example.androidremake2.core.podcast.PodcastEpisode;
import com.example.androidremake2.databinding.FrgDlgModalBottomSheetBinding;
import com.example.androidremake2.injects.base.BaseBottomSheetDialogFragment;
import com.example.androidremake2.injects.base.BaseComponent;
import com.example.androidremake2.utils.DimUtils;
import com.example.androidremake2.views.MainActivityViewModel;
import com.example.androidremake2.views.podcast.viewmodels.PodcastBottomSheetFragmentViewModel;
import com.example.androidremake2.views.views.MediaPlayingView;
import com.google.android.exoplayer2.ExoPlayer;
import com.google.android.exoplayer2.MediaItem;
import com.google.android.exoplayer2.Player;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialog;

import org.androidannotations.annotations.EFragment;

import java.util.ArrayList;
import java.util.List;

import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
@EFragment
public class PodcastBottomSheetDialogFragment extends BaseBottomSheetDialogFragment
        implements BaseComponent, Player.EventListener {

    protected FrgDlgModalBottomSheetBinding binding;

    protected MainActivityViewModel activityViewModel;
    protected PodcastBottomSheetFragmentViewModel fragmentViewModel;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        super.onCreateView(inflater, container, savedInstanceState);

        binding = FrgDlgModalBottomSheetBinding.inflate(inflater, container, false);

        ExoPlayer exoPlayer = activityViewModel.getExoPlayer();
        binding.playerView.setPlayer(exoPlayer);

        Podcast podcast = PodcastBottomSheetDialogFragmentArgs.fromBundle(getArguments()).getPodcast();

        activityViewModel.getPodcast(podcast.id);

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
        Podcast playingPodcast = activityViewModel.podcastLiveData.getValue();

        if (playingPodcast == null || episode == null) {
            return;
        }

        MediaPlayingView mediaPlayingView = requireActivity().findViewById(R.id.mediaPlayingView);
        mediaPlayingView.getBinding().smallPlayerControlView.setPlayer(activityViewModel.getExoPlayer());
        mediaPlayingView.getBinding().podcastTitleTxt.setText(episode.title);
        Glide
                .with(mediaPlayingView)
                .load(episode.imageUrl)
                .into(mediaPlayingView.getBinding().podcastEpisodeImg);

        binding.txtPodcastTitle.setText(playingPodcast.title);
        binding.txtPodcastEpisodeTitle.setText(episode.title);

        Glide.with(this).load(episode.imageUrl).into(binding.podcastImg);
    }

    @Override
    public void initViewModels() {
        ViewModelProvider viewModelProvider = new ViewModelProvider(this);
        this.fragmentViewModel = viewModelProvider.get(PodcastBottomSheetFragmentViewModel.class);
        this.activityViewModel = viewModelProvider.get(MainActivityViewModel.class);
    }

    @Override
    public void subscribeObservers() {
        activityViewModel.podcastLiveData.observe(getViewLifecycleOwner(), new Observer<Podcast>() {
            @Override
            public void onChanged(Podcast podcast) {
                activityViewModel.playEpisodes(podcast.episodes);
            }
        });

        activityViewModel.episodesMediaItems.observe(getViewLifecycleOwner(), new Observer<List<MediaBrowserCompat.MediaItem>>() {
            @Override
            public void onChanged(List<MediaBrowserCompat.MediaItem> mediaItems) {

                List<MediaItem> exoMediaItems = new ArrayList<>();
                for (MediaBrowserCompat.MediaItem mediaItem : mediaItems) {
                    MediaItem item = new MediaItem.Builder()
                            .setMediaId(mediaItem.getMediaId())
                            .setUri(mediaItem.getDescription().getMediaUri())
                            .build();
                    exoMediaItems.add(item);
                }
            }
        });

        activityViewModel.playingEpisodeLiveData.observe(getViewLifecycleOwner(), new Observer<PodcastEpisode>() {
            @Override
            public void onChanged(PodcastEpisode episode) {
                displayPlayingEpisode(episode);
            }
        });
    }

    @Override
    public void unsubscribeObservers() {
        activityViewModel.playingEpisodeLiveData.removeObservers(getViewLifecycleOwner());
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

        binding = null;
        super.onDestroy();
    }
}
