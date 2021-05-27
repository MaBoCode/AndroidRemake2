package com.example.androidremake2.views.podcast.fragments;

import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
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
import androidx.palette.graphics.Palette;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.example.androidremake2.R;
import com.example.androidremake2.core.podcast.Podcast;
import com.example.androidremake2.core.podcast.PodcastEpisode;
import com.example.androidremake2.databinding.FrgPodcastDetailsBinding;
import com.example.androidremake2.injects.base.BaseFragment;
import com.example.androidremake2.injects.base.BaseViewModel;
import com.example.androidremake2.utils.ThemeUtils;
import com.example.androidremake2.views.MainActivityViewModel;
import com.example.androidremake2.views.podcast.utils.PodcastEpisodesAdapter;
import com.google.android.material.appbar.AppBarLayout;
import com.google.android.material.appbar.MaterialToolbar;

import org.androidannotations.annotations.EFragment;
import org.jetbrains.annotations.NotNull;
import org.jsoup.Jsoup;

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

        binding.lyAppBar.addOnOffsetChangedListener(new AppBarLayout.OnOffsetChangedListener() {
            @Override
            public void onOffsetChanged(AppBarLayout appBarLayout, int verticalOffset) {
                binding.appBarContainer.setTranslationY((float) -verticalOffset);
                float percent = (float) (Math.abs(verticalOffset)) / binding.lyAppBar.getTotalScrollRange();

                binding.appBarContainer.setAlpha(1f - percent);
                binding.allEpisodesTitle2.setAlpha(percent);
                //binding.appBarShadow.setAlpha(percent);

                float scale = (1f - percent) + percent / 1.199f;
                binding.appBarContainer.setScaleY(scale);
                binding.appBarContainer.setScaleX(scale);
            }
        });

        setupActionBar();

        Podcast podcast = PodcastBottomSheetDialogFragmentArgs.fromBundle(getArguments()).getPodcast();

        setupPodcastEpisodesAdapter(podcast);
        displayPodcastDetails(podcast);

        return binding.getRoot();
    }

    @Override
    public void onStart() {
        super.onStart();

        Podcast podcast = PodcastBottomSheetDialogFragmentArgs.fromBundle(getArguments()).getPodcast();
        activityViewModel.getPodcast(podcast.id);
    }

    public void setGradientBackground(Drawable baseImg) {
        int colorBackground = ThemeUtils.getThemeColor(requireContext(), android.R.attr.colorBackground);
        int gradientColor = ThemeUtils.getThemeColor(requireContext(), R.attr.colorSecondary);

        Bitmap bitmap = ((BitmapDrawable) baseImg).getBitmap();

        Palette palette = Palette.from(bitmap).generate();
        Palette.Swatch swatch = palette.getDarkVibrantSwatch();

        if (swatch != null) {
            gradientColor = swatch.getRgb();
        }

        GradientDrawable gradientBg = new GradientDrawable(
                GradientDrawable.Orientation.TOP_BOTTOM,
                new int[]{gradientColor, colorBackground}
        );
        binding.appBarImg.setImageDrawable(gradientBg);
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

    public void setupPodcastEpisodesAdapter(Podcast podcast) {
        RecyclerView episodesReyclerView = binding.episodesRecyclerView;
        this.episodesAdapter = new PodcastEpisodesAdapter(podcast, new PodcastEpisode.PodcastEpisodeDiff());

        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext(), RecyclerView.VERTICAL, false);
        layoutManager.setSmoothScrollbarEnabled(true);

        episodesReyclerView.setHasFixedSize(true);
        episodesReyclerView.setLayoutManager(layoutManager);
        episodesReyclerView.setAdapter(this.episodesAdapter);
    }

    public void displayPodcastDetails(Podcast podcast) {
        binding.podcastTitleTxt.setText(podcast.title);
        binding.podcastPublisherTxt.setText(podcast.publisher);
        binding.podcastDescriptionTxt.setText(Jsoup.parse(podcast.description).text());

        Glide
                .with(binding.getRoot())
                .load(podcast.imageUrl)
                .addListener(new RequestListener<Drawable>() {
                    @Override
                    public boolean onLoadFailed(@Nullable @org.jetbrains.annotations.Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                        return false;
                    }

                    @Override
                    public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                        setGradientBackground(resource);
                        return false;
                    }
                })
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
    public void onResume() {
        super.onResume();

        hideBottomNavView(null);
    }

    @Override
    public void onDestroy() {

        binding.tlbPodcastDetails.setNavigationOnClickListener(null);

        super.onDestroy();
    }
}
