package com.example.androidremake2.views.podcast;

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

import com.example.androidremake2.core.MainFragmentViewModel;
import com.example.androidremake2.core.podcast.Podcast;
import com.example.androidremake2.databinding.FrgMainBinding;
import com.example.androidremake2.injects.base.BaseFragment;
import com.example.androidremake2.injects.base.BaseViewModel.LoadingStatus;

import org.androidannotations.annotations.EFragment;

import java.util.List;

import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
@EFragment
public class MainFragment extends BaseFragment implements PodcastAdapter.OnPodcastItemClickListener {

    protected FrgMainBinding binding;

    protected MainFragmentViewModel viewModel;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FrgMainBinding.inflate(inflater, container, false);

        viewModel = new ViewModelProvider(requireActivity()).get(MainFragmentViewModel.class);

        initObservers();

        viewModel.getBestPodcasts();

        return binding.getRoot();
    }

    public void displayPodcasts(List<Podcast> podcasts) {
        RecyclerView podcastRecyclerView = binding.podcastsRecyclerView;
        PodcastAdapter podcastAdapter = new PodcastAdapter(new Podcast.PodcastDiff(), this);
        podcastAdapter.submitList(podcasts);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext(), RecyclerView.HORIZONTAL, false);
        layoutManager.setSmoothScrollbarEnabled(true);

        podcastRecyclerView.setHasFixedSize(true);
        podcastRecyclerView.setLayoutManager(layoutManager);
        podcastRecyclerView.setAdapter(podcastAdapter);

        SnapHelper snapHelper = new LinearSnapHelper();
        snapHelper.attachToRecyclerView(podcastRecyclerView);
    }

    @Override
    public void initObservers() {

        viewModel.podcastsLiveData.observe(getViewLifecycleOwner(), new Observer<List<Podcast>>() {
            @Override
            public void onChanged(List<Podcast> podcasts) {
                displayPodcasts(podcasts);
            }
        });

        viewModel.podcastLiveData.observe(getViewLifecycleOwner(), new Observer<Podcast>() {
            @Override
            public void onChanged(Podcast podcast) {
                NavController navController = Navigation.findNavController(binding.getRoot());

                MainFragmentDirections.PlayPodcastAction action = MainFragmentDirections.playPodcastAction(podcast);

                if (navController != null) {
                    navController.navigate(action);
                }

                viewModel.podcastLiveData.removeObserver(this);
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

    @Override
    public void onPodcastItemClick(View view, Podcast podcast) {
        viewModel.getPodcast(podcast.id);
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

        binding = null;

        super.onDestroy();
    }
}
