package com.example.androidremake2.views.podcast.fragments;

import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.bumptech.glide.Glide;
import com.example.androidremake2.core.podcast.Podcast;
import com.example.androidremake2.core.podcast.PodcastEpisode;
import com.example.androidremake2.databinding.FrgDlgModalBottomSheetBinding;
import com.example.androidremake2.injects.base.BaseBottomSheetDialogFragment;
import com.example.androidremake2.injects.base.BaseComponent;
import com.example.androidremake2.utils.DimUtils;
import com.example.androidremake2.views.MainActivityViewModel;
import com.example.androidremake2.views.podcast.viewmodels.PodcastBottomSheetFragmentViewModel;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialog;

import org.androidannotations.annotations.EFragment;

import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
@EFragment
public class PodcastBottomSheetDialogFragment extends BaseBottomSheetDialogFragment implements BaseComponent {

    protected FrgDlgModalBottomSheetBinding binding;

    protected MainActivityViewModel activityViewModel;
    protected PodcastBottomSheetFragmentViewModel fragmentViewModel;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        super.onCreateView(inflater, container, savedInstanceState);

        binding = FrgDlgModalBottomSheetBinding.inflate(inflater, container, false);

        return binding.getRoot();
    }

    @Override
    public void onStart() {
        super.onStart();
        activityViewModel.connectToPodcastService();
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

        Podcast playingPodcast = activityViewModel.playingPodcast;

        if (playingPodcast == null || episode == null) {
            return;
        }

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
        activityViewModel.playingPodcastEpisode.observe(getViewLifecycleOwner(), new Observer<PodcastEpisode>() {
            @Override
            public void onChanged(PodcastEpisode episode) {
                displayPlayingEpisode(episode);
            }
        });
        activityViewModel.getPodcastServiceConnection().isConnected.observe(getViewLifecycleOwner(), new Observer<Boolean>() {
            @Override
            public void onChanged(Boolean isConnected) {

                if (isConnected) {
                    Podcast playingPodcast = PodcastBottomSheetDialogFragmentArgs.fromBundle(getArguments()).getPodcast();
                    activityViewModel.playPodcast(playingPodcast);
                }
            }
        });
    }

    @Override
    public void unsubscribeObservers() {
        activityViewModel.playingPodcastEpisode.removeObservers(getViewLifecycleOwner());
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
