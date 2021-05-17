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

import com.example.androidremake2.R;
import com.example.androidremake2.core.podcast.Podcast;
import com.example.androidremake2.databinding.FrgMainBinding;
import com.example.androidremake2.injects.base.BaseFragment;
import com.example.androidremake2.injects.base.BaseViewModel.LoadingStatus;
import com.example.androidremake2.views.podcast.utils.PodcastAdapter;
import com.example.androidremake2.views.podcast.viewmodels.MainFragmentViewModel;
import com.example.androidremake2.views.search.events.EndlessRecyclerViewScrollListener;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import org.androidannotations.annotations.EFragment;
import org.jetbrains.annotations.NotNull;

import java.util.List;

import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
@EFragment
public class MainFragment extends BaseFragment implements PodcastAdapter.OnPodcastItemClickListener {

    protected FrgMainBinding binding;

    protected MainFragmentViewModel viewModel;

    protected PodcastAdapter podcastAdapter;

    protected EndlessRecyclerViewScrollListener onScrolled = new EndlessRecyclerViewScrollListener() {
        @Override
        public void onLoadMore(int page, int totalItemsCount, RecyclerView view) {
            Integer nextPage = viewModel.nextPage.getValue();

            if (nextPage == null) {
                return;
            }
            viewModel.getBestPodcasts(nextPage, "fr");
        }
    };

    @Nullable
    @org.jetbrains.annotations.Nullable
    @Override
    public View onCreateView(@NonNull @NotNull LayoutInflater inflater, @Nullable @org.jetbrains.annotations.Nullable ViewGroup container, @Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {

        super.onCreateView(inflater, container, savedInstanceState);

        binding = FrgMainBinding.inflate(inflater, container, false);

        BottomNavigationView bottomNav = requireActivity().findViewById(R.id.bottomNavView);
        bottomNav.setVisibility(View.VISIBLE);

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
    public void onPodcastItemClick(View view, Podcast podcast) {
        viewModel.getPodcast(podcast.id);
    }

    @Override
    public void initViewModels() {
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

        viewModel.podcastLiveData.observe(getViewLifecycleOwner(), new Observer<Podcast>() {
            @Override
            public void onChanged(Podcast podcast) {
                NavController navController = Navigation.findNavController(binding.getRoot());

                MainFragmentDirections.PlayPodcastAction action = MainFragmentDirections.playPodcastAction(podcast);

                navController.navigate(action);
            }
        });

        viewModel.loadingLiveData.observe(getViewLifecycleOwner(), new Observer<LoadingStatus>() {
            @Override
            public void onChanged(LoadingStatus status) {
                showHideLoader(status);
            }
        });
    }

    @Override
    public void unsubscribeObservers() {
        viewModel.podcastLiveData.removeObservers(getViewLifecycleOwner());
        viewModel.bestPodcastsLiveData.removeObservers(getViewLifecycleOwner());
        viewModel.loadingLiveData.removeObservers(getViewLifecycleOwner());
    }

    @Override
    public void onStart() {
        viewModel.getBestPodcasts(viewModel.nextPage.getValue(), "fr");

        super.onStart();
    }

    @Override
    public void onDestroy() {

        binding = null;

        super.onDestroy();
    }
}
