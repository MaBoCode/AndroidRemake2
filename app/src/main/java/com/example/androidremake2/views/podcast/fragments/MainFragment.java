package com.example.androidremake2.views.podcast.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.LinearSnapHelper;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.SnapHelper;

import com.bumptech.glide.Glide;
import com.example.androidremake2.R;
import com.example.androidremake2.core.podcast.Podcast;
import com.example.androidremake2.core.podcast.PodcastEpisode;
import com.example.androidremake2.databinding.FrgMainBinding;
import com.example.androidremake2.injects.base.BaseFragment;
import com.example.androidremake2.injects.base.BaseViewModel.LoadingStatus;
import com.example.androidremake2.utils.Logs;
import com.example.androidremake2.utils.UserUtils;
import com.example.androidremake2.views.MainActivityViewModel;
import com.example.androidremake2.views.podcast.utils.PodcastAdapter;
import com.example.androidremake2.views.podcast.viewmodels.MainFragmentViewModel;
import com.example.androidremake2.views.search.events.EndlessRecyclerViewScrollListener;
import com.example.androidremake2.views.views.MediaPlayingView;

import org.androidannotations.annotations.EFragment;
import org.jetbrains.annotations.NotNull;

import java.util.List;

import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
@EFragment
public class MainFragment extends BaseFragment implements PodcastAdapter.OnPodcastItemClickListener {

    protected FrgMainBinding binding;

    protected MainActivityViewModel activityViewModel;
    protected MainFragmentViewModel viewModel;

    protected PodcastAdapter podcastAdapter;

    protected EndlessRecyclerViewScrollListener onScrolled = new EndlessRecyclerViewScrollListener() {
        @Override
        public void onLoadMore(int page, int totalItemsCount, RecyclerView view) {
            Integer nextPage = viewModel.nextPage.getValue();

            if (nextPage == null) {
                return;
            }
            viewModel.getBestPodcasts(nextPage, UserUtils.getUserCountryCode(requireContext()));
        }
    };

    @Nullable
    @org.jetbrains.annotations.Nullable
    @Override
    public View onCreateView(@NonNull @NotNull LayoutInflater inflater, @Nullable @org.jetbrains.annotations.Nullable ViewGroup container, @Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {

        super.onCreateView(inflater, container, savedInstanceState);

        binding = FrgMainBinding.inflate(inflater, container, false);

        MediaPlayingView mediaPlayingView = requireActivity().findViewById(R.id.mediaPlayingView);
        mediaPlayingView.getBinding().getRoot().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                NavController navController = Navigation.findNavController(binding.getRoot());

                Podcast podcast = activityViewModel.podcastLiveData.getValue();

                if (podcast == null) {
                    return;
                }

                MainFragmentDirections.PlayPodcastAction action = MainFragmentDirections.playPodcastAction(podcast);

                navController.navigate(action);
            }
        });

        showBottomNavView();

        setupPodcastAdapter();

        return binding.getRoot();
    }

    public void setupPodcastAdapter() {
        RecyclerView podcastRecyclerView = binding.podcastsRecyclerView;
        this.podcastAdapter = new PodcastAdapter(new Podcast.PodcastDiff(), this);

        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext(), RecyclerView.HORIZONTAL, false);
        layoutManager.setSmoothScrollbarEnabled(true);

        onScrolled.setmLayoutManager(layoutManager);

        podcastRecyclerView.addOnScrollListener(onScrolled);
        podcastRecyclerView.setHasFixedSize(true);
        podcastRecyclerView.setLayoutManager(layoutManager);
        podcastRecyclerView.setAdapter(this.podcastAdapter);
        podcastRecyclerView.setOnFlingListener(null);

        SnapHelper snapHelper = new LinearSnapHelper();
        snapHelper.attachToRecyclerView(podcastRecyclerView);
    }

    @Override
    public void playPodcast(View view, Podcast podcast) {

        showMediaPlayingView();

        NavController navController = Navigation.findNavController(binding.getRoot());

        MainFragmentDirections.PlayPodcastAction action = MainFragmentDirections.playPodcastAction(podcast);

        navController.navigate(action);
    }

    @Override
    public void displayPodcastDetails(View view, Podcast podcast) {

        hideBottomNavView();

        NavController navController = Navigation.findNavController(binding.getRoot());

        MainFragmentDirections.DisplayPodcastDetailsAction action = MainFragmentDirections.displayPodcastDetailsAction(podcast);

        navController.navigate(action);
    }

    public void displayPlayingEpisode(PodcastEpisode episode) {
        MediaPlayingView mediaPlayingView = requireActivity().findViewById(R.id.mediaPlayingView);
        mediaPlayingView.getBinding().podcastTitleTxt.setText(episode.title);

        Glide
                .with(mediaPlayingView)
                .load(episode.imageUrl)
                .into(mediaPlayingView.getBinding().podcastEpisodeImg);
    }

    @Override
    public void initViewModels() {
        activityViewModel = new ViewModelProvider(requireActivity()).get(MainActivityViewModel.class);
        viewModel = new ViewModelProvider(requireActivity()).get(MainFragmentViewModel.class);
    }

    @Override
    public void subscribeObservers() {
        viewModel.bestPodcastsLiveData.observe(getViewLifecycleOwner(), new Observer<List<Podcast>>() {
            @Override
            public void onChanged(List<Podcast> podcasts) {

                showHideLoader(LoadingStatus.NOT_LOADING);

                if (podcasts == null || podcasts.isEmpty()) {
                    return;
                }

                List<Podcast> currentList = podcastAdapter.getCurrentList();

                if (currentList.isEmpty()) {
                    podcastAdapter.submitList(podcasts);
                } else {
                    Integer count = viewModel.podcastCountInPage.getValue();

                    if (count != null) {
                        podcastAdapter.notifyItemRangeInserted(currentList.size() - count, count);
                    }
                }
            }
        });

        viewModel.loadingLiveData.observe(getViewLifecycleOwner(), new Observer<LoadingStatus>() {
            @Override
            public void onChanged(LoadingStatus loadingStatus) {
                showHideLoader(loadingStatus);
            }
        });

        activityViewModel.podcastLiveData.observe(getViewLifecycleOwner(), new Observer<Podcast>() {
            @Override
            public void onChanged(Podcast podcast) {
                Logs.debug(this, podcast.id);
            }
        });
    }

    @Override
    public void unsubscribeObservers() {
        viewModel.bestPodcastsLiveData.removeObservers(getViewLifecycleOwner());
    }

    @Override
    public void onStart() {
        super.onStart();

        viewModel.getBestPodcasts(viewModel.nextPage.getValue(), UserUtils.getUserCountryCode(requireContext()));
    }

    @Override
    public void onResume() {
        super.onResume();

        showBottomNavView();
    }

    @Override
    public void onDestroy() {

        binding = null;

        super.onDestroy();
    }
}
