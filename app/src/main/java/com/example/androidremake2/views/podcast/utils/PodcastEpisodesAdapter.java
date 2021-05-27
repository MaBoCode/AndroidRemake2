package com.example.androidremake2.views.podcast.utils;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;

import com.example.androidremake2.core.podcast.Podcast;
import com.example.androidremake2.core.podcast.PodcastEpisode;
import com.example.androidremake2.databinding.PodcastEpisodeListItemBinding;
import com.example.androidremake2.views.base.BaseRecyclerViewAdapter;
import com.example.androidremake2.views.utils.RecyclerViewUtils;

import org.jetbrains.annotations.NotNull;

public class PodcastEpisodesAdapter extends BaseRecyclerViewAdapter<PodcastEpisode, PodcastEpisodeListItemBinding> {

    protected Podcast podcast;

    public PodcastEpisodesAdapter(Podcast podcast, @NotNull DiffUtil.ItemCallback<PodcastEpisode> diffCallback) {
        super(diffCallback);
        this.podcast = podcast;
    }

    @NonNull
    @NotNull
    @Override
    public PodcastEpisodeViewHolder onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType) {
        PodcastEpisodeListItemBinding binding = PodcastEpisodeListItemBinding.inflate(
                LayoutInflater.from(parent.getContext()),
                parent,
                false
        );
        binding.lyShimmer.setShimmer(RecyclerViewUtils.getShimmer().build());
        return new PodcastEpisodeViewHolder(binding, podcast);
    }
}
