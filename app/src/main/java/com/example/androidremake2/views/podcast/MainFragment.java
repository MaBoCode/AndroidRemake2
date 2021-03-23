package com.example.androidremake2.views.podcast;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.LinearSnapHelper;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.SnapHelper;

import com.example.androidremake2.core.MainFragmentViewModel;
import com.example.androidremake2.core.podcast.Podcast;
import com.example.androidremake2.databinding.FrgMainBinding;
import com.example.androidremake2.injects.base.BaseFragment;
import com.example.androidremake2.injects.base.BaseViewModel.LoadingStatus;
import com.example.androidremake2.utils.Logs;

import org.androidannotations.annotations.EFragment;

import java.util.Arrays;

import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
@EFragment
public class MainFragment extends BaseFragment {

    protected FrgMainBinding binding;

    protected MainFragmentViewModel viewModel;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FrgMainBinding.inflate(inflater, container, false);

        viewModel = new ViewModelProvider(requireActivity()).get(MainFragmentViewModel.class);

        initObservers();

        viewModel.getPodcast("c3161c7d-d5ac-46a9-82c1-b18cbcc93b5c");

        return binding.getRoot();
    }

    @Override
    public void initObservers() {

        viewModel.podcastLiveData.observe(getViewLifecycleOwner(), new Observer<Podcast>() {
            @Override
            public void onChanged(Podcast podcast) {

                if (podcast == null)
                    return;

                Podcast p2 = new Podcast(podcast);
                p2.id = "p2_id2";

                Podcast p3 = new Podcast(podcast);
                p3.id = "p3_id3";

                RecyclerView podcastRecyclerView = binding.podcastsRecyclerView;
                PodcastAdapter podcastAdapter = new PodcastAdapter(new Podcast.PodcastDiff());
                podcastAdapter.submitList(Arrays.asList(podcast, p2, p3));
                LinearLayoutManager layoutManager = new LinearLayoutManager(getContext(), RecyclerView.HORIZONTAL, false);
                layoutManager.setSmoothScrollbarEnabled(true);

                podcastRecyclerView.setHasFixedSize(true);
                podcastRecyclerView.setLayoutManager(layoutManager);
                podcastRecyclerView.setAdapter(podcastAdapter);

                SnapHelper snapHelper = new LinearSnapHelper();
                snapHelper.attachToRecyclerView(podcastRecyclerView);
            }
        });

        viewModel.loadingLiveData.observe(getViewLifecycleOwner(), new Observer<LoadingStatus>() {
            @Override
            public void onChanged(LoadingStatus status) {
                int visiblity = status == LoadingStatus.LOADING ? View.VISIBLE : View.GONE;
                binding.loader.setVisibility(visiblity);
            }
        });
    }

    public void startAnimations() {
        /*
        new AnimationUtils.Builder()
                .setObjects(Arrays.asList(binding.usersCountTxtView, binding.usernameTxtView))
                .setAnimateAlphaIn(true)
                .setTranslationYBegin(-400f)
                .setInterpolator(new FastOutSlowInInterpolator())
                .setDelay(300)
                .start();

         */
    }

    @Override
    public void onDestroy() {

        viewModel.podcastLiveData.removeObservers(getViewLifecycleOwner());
        viewModel.podcastsLiveData.removeObservers(getViewLifecycleOwner());
        viewModel.loadingLiveData.removeObservers(getViewLifecycleOwner());

        super.onDestroy();
    }
}
