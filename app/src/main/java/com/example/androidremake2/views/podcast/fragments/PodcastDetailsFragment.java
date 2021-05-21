package com.example.androidremake2.views.podcast.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.androidremake2.R;
import com.example.androidremake2.core.podcast.Podcast;
import com.example.androidremake2.core.podcast.PodcastEpisode;
import com.example.androidremake2.databinding.FrgPodcastDetailsBinding;
import com.example.androidremake2.injects.base.BaseFragment;
import com.example.androidremake2.injects.base.BaseViewModel;
import com.example.androidremake2.views.MainActivityViewModel;
import com.example.androidremake2.views.podcast.utils.PodcastEpisodesAdapter;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import org.androidannotations.annotations.EFragment;
import org.jetbrains.annotations.NotNull;

import java.util.List;

import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
@EFragment
public class PodcastDetailsFragment extends BaseFragment {

    protected FrgPodcastDetailsBinding binding;

    protected MainActivityViewModel activityViewModel;

    protected PodcastEpisodesAdapter episodesAdapter;

    @Nullable
    @org.jetbrains.annotations.Nullable
    @Override
    public View onCreateView(@NonNull @NotNull LayoutInflater inflater, @Nullable @org.jetbrains.annotations.Nullable ViewGroup container, @Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);

        binding = FrgPodcastDetailsBinding.inflate(inflater, container, false);

        BottomNavigationView bottomNav = requireActivity().findViewById(R.id.bottomNavView);
        bottomNav.setVisibility(View.GONE);

        setupActionBar();

        setupPodcastEpisodesAdapter();

        Podcast podcast = PodcastBottomSheetDialogFragmentArgs.fromBundle(getArguments()).getPodcast();
        displayPodcastDetails(podcast);

        activityViewModel.getPodcast(podcast.id);

        return binding.getRoot();
    }

    public void setupActionBar() {
        MaterialToolbar toolbar = binding.tlbPodcastDetails;
        AppCompatActivity activity = (AppCompatActivity) requireActivity();
        activity.setSupportActionBar(toolbar);

        activity.getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        activity.getSupportActionBar().setDisplayShowTitleEnabled(false);

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Navigation.findNavController(binding.getRoot()).navigateUp();
            }
        });
    }

    public void setupPodcastEpisodesAdapter() {
        RecyclerView episodesReyclerView = binding.episodesRecyclerView;
        this.episodesAdapter = new PodcastEpisodesAdapter(new PodcastEpisode.PodcastEpisodeDiff());

        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext(), RecyclerView.VERTICAL, false);
        layoutManager.setSmoothScrollbarEnabled(true);

        episodesReyclerView.setHasFixedSize(true);
        episodesReyclerView.setLayoutManager(layoutManager);
        episodesReyclerView.setAdapter(this.episodesAdapter);
    }

    public void displayPodcastDetails(Podcast podcast) {
        binding.podcastTitleTxt.setText(podcast.title);
        binding.podcastPublisherTxt.setText(podcast.publisher);
        //binding.podcastDescriptionTxt.setText(Jsoup.parse(podcast.description).text());

        Glide
                .with(binding.getRoot())
                .load(podcast.imageUrl)
                .placeholder(R.drawable.ic_launcher_background)
                .into(binding.podcastImg);
    }

    public void displayPodcastEpisodes(List<PodcastEpisode> episodes) {

        if (episodes != null && !episodes.isEmpty()) {
            this.episodesAdapter.submitList(episodes);
        }
    }

    @Override
    public void initViewModels() {
        activityViewModel = new ViewModelProvider(this).get(MainActivityViewModel.class);
    }

    @Override
    public void subscribeObservers() {
        activityViewModel.podcastLiveData.observe(getViewLifecycleOwner(), new Observer<Podcast>() {
            @Override
            public void onChanged(Podcast podcast) {
                displayPodcastEpisodes(podcast.episodes);
            }
        });

        activityViewModel.loadingLiveData.observe(getViewLifecycleOwner(), new Observer<BaseViewModel.LoadingStatus>() {
            @Override
            public void onChanged(BaseViewModel.LoadingStatus status) {
                showHideLoader(status);
            }
        });
    }

    @Override
    public void unsubscribeObservers() {
        activityViewModel.podcastLiveData.removeObservers(getViewLifecycleOwner());
        activityViewModel.loadingLiveData.removeObservers(getViewLifecycleOwner());
    }

    @Override
    public void onDestroy() {

        binding.tlbPodcastDetails.setNavigationOnClickListener(null);

        super.onDestroy();
    }
}
