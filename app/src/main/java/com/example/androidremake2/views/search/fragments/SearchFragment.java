package com.example.androidremake2.views.search.fragments;

import android.animation.AnimatorSet;
import android.animation.ValueAnimator;
import android.graphics.drawable.GradientDrawable;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.widget.SearchView;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.androidremake2.R;
import com.example.androidremake2.core.podcast.Podcast;
import com.example.androidremake2.databinding.FrgSearchBinding;
import com.example.androidremake2.injects.base.BaseFragment;
import com.example.androidremake2.injects.base.BaseViewModel;
import com.example.androidremake2.utils.DimUtils;
import com.example.androidremake2.utils.Logs;
import com.example.androidremake2.views.podcast.utils.PodcastAdapter;
import com.example.androidremake2.views.search.events.EndlessRecyclerViewScrollListener;
import com.example.androidremake2.views.search.utils.SearchAdapter;
import com.example.androidremake2.views.search.viewmodels.SearchFragmentViewModel;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.util.List;

public class SearchFragment extends BaseFragment implements View.OnFocusChangeListener, SearchView.OnQueryTextListener, View.OnClickListener, PodcastAdapter.OnPodcastItemClickListener {

    protected FrgSearchBinding binding;

    protected SearchFragmentViewModel viewModel;

    protected SearchAdapter searchAdapter;

    protected EndlessRecyclerViewScrollListener onScrolled = new EndlessRecyclerViewScrollListener() {
        @Override
        public void onLoadMore(int page, int totalItemsCount, RecyclerView view) {
            String query = viewModel.getCurrentQuery();
            viewModel.searchPodcasts(query, "title", viewModel.nextSearchOffset, "fr");
        }
    };

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        super.onCreateView(inflater, container, savedInstanceState);

        binding = FrgSearchBinding.inflate(inflater, container, false);

        binding.searchView.setOnQueryTextFocusChangeListener(this);
        binding.searchView.setOnQueryTextListener(this);
        binding.searchView.setOnFocusChangeListener(this);
        binding.searchView.setOnSearchClickListener(this);

        setupSearchResultAdapter();

        return binding.getRoot();
    }

    public void setupSearchResultAdapter() {
        RecyclerView searchResultRecyclerView = binding.searchResultRecyclerView;
        this.searchAdapter = new SearchAdapter(new Podcast.PodcastDiff(), this);

        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext(), RecyclerView.VERTICAL, false);
        layoutManager.setSmoothScrollbarEnabled(true);

        onScrolled.setmLayoutManager(layoutManager);

        searchResultRecyclerView.addOnScrollListener(onScrolled);
        searchResultRecyclerView.setHasFixedSize(true);
        searchResultRecyclerView.setLayoutManager(layoutManager);
        searchResultRecyclerView.setAdapter(this.searchAdapter);
    }

    @Override
    public void displayPodcastDetails(View view, Podcast podcast) {
        NavController navController = Navigation.findNavController(binding.getRoot());

        SearchFragmentDirections.DisplayPodcastDetailsAction action = SearchFragmentDirections.displayPodcastDetailsAction(podcast);

        navController.navigate(action);
    }

    @Override
    public void playPodcast(View view, Podcast podcast) {

    }

    public void performSearch(String query) {
        viewModel.setCurrentQuery(query);
        viewModel.searchPodcasts(query, "title", null, "fr");
    }

    @Override
    public boolean onQueryTextSubmit(String query) {

        if (viewModel.getCurrentQuery() == null || !query.contentEquals(viewModel.getCurrentQuery())) {
            performSearch(query);
        }
        return false;
    }

    @Override
    public boolean onQueryTextChange(String query) {
        if (query.isEmpty()) {

            clearAdapterData();

            onScrolled.resetState();
        } else {
            //performSearch(query);
        }
        return false;
    }

    public void clearAdapterData() {
        viewModel.clearSearchResults();

        searchAdapter.notifyItemRangeRemoved(0, searchAdapter.getItemCount());
        searchAdapter.notifyDataSetChanged();
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    public void animateSearchView(boolean focus) {
        GradientDrawable gradientDrawable = (GradientDrawable) binding.searchView.getBackground();
        Float fromRadius = gradientDrawable.getCornerRadius();
        Float toRadius = focus ? 0f : DimUtils.dp2px(requireContext(), 8f);

        ValueAnimator searchViewCornerAnimation = ValueAnimator.ofFloat(fromRadius, toRadius);
        searchViewCornerAnimation.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator valueAnimator) {
                Float value = (Float) valueAnimator.getAnimatedValue();
                gradientDrawable.setCornerRadius(value);
            }
        });

        int fromMargin = ((ViewGroup.MarginLayoutParams) binding.searchView.getLayoutParams()).bottomMargin;
        int toMargin = focus ? 0 : (int) DimUtils.dp2px(requireContext(), 12f);

        ValueAnimator searchViewMarginAnimation = ValueAnimator.ofInt(fromMargin, toMargin);
        searchViewMarginAnimation.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator valueAnimator) {
                int value = (int) valueAnimator.getAnimatedValue();
                ((ViewGroup.MarginLayoutParams) binding.searchView.getLayoutParams()).setMargins(value, value, value, value);
                binding.searchView.requestLayout();
            }
        });

        ImageView searchIcon = binding.searchView.findViewById(androidx.appcompat.R.id.search_mag_icon);
        if (focus) {
            searchIcon.setImageResource(R.drawable.ic_back);
        } else {
            searchIcon.setImageResource(R.drawable.ic_search_bar_outlined);
        }

        AnimatorSet animatorSet = new AnimatorSet();
        animatorSet.setDuration(300);
        animatorSet.playTogether(searchViewCornerAnimation, searchViewMarginAnimation);
        animatorSet.start();
    }

    @Override
    public void onClick(View view) {
        // On searchView search icon click
        Logs.debug(this, "[DBG] CLICK");
        requireActivity().getOnBackPressedDispatcher().onBackPressed();
    }

    @Override
    public void onFocusChange(View view, boolean hasFocus) {
        BottomNavigationView bottomNav = requireActivity().findViewById(R.id.bottomNavView);
        if (hasFocus) {
            bottomNav.setVisibility(View.GONE);
        } else {
            bottomNav.setVisibility(View.VISIBLE);
        }
        animateSearchView(hasFocus);
    }

    @Override
    public void initViewModels() {
        viewModel = new ViewModelProvider(requireActivity()).get(SearchFragmentViewModel.class);
    }

    @Override
    public void subscribeObservers() {

        viewModel.loadingLiveData.observe(getViewLifecycleOwner(), new Observer<BaseViewModel.LoadingStatus>() {
            @Override
            public void onChanged(BaseViewModel.LoadingStatus status) {
                showHideLoader(status);
            }
        });

        viewModel.podcastsResultLiveData.observe(getViewLifecycleOwner(), new Observer<List<Podcast>>() {
            @Override
            public void onChanged(List<Podcast> podcasts) {

                showHideLoader(BaseViewModel.LoadingStatus.NOT_LOADING);

                List<Podcast> currentList = searchAdapter.getCurrentList();

                if (podcasts == null || podcasts.isEmpty()) {
                    return;
                }

                if (currentList.isEmpty()) {
                    searchAdapter.submitList(podcasts);
                } else {
                    //searchAdapter.notifyItemRangeInserted(viewModel.previousSearchOffset, podcasts.size() - viewModel.previousSearchOffset);
                }
            }
        });
    }

    @Override
    public void onPause() {

        if (viewModel != null && searchAdapter != null) {
            clearAdapterData();
        }

        if (viewModel != null) {
            viewModel.setCurrentQuery(null);
        }

        super.onPause();
    }

    @Override
    public void onDestroy() {

        if (viewModel != null && searchAdapter != null) {
            clearAdapterData();
        }

        if (binding != null) {
            binding.searchResultRecyclerView.removeOnScrollListener(onScrolled);
        }

        if (binding != null && binding.searchView != null) {
            binding.searchView.setOnQueryTextFocusChangeListener(null);
        }

        binding = null;

        super.onDestroy();
    }
}
