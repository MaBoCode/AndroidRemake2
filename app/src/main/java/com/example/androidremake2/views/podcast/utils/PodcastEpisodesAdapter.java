package com.example.androidremake2.views.podcast.utils;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;

import com.example.androidremake2.core.podcast.PodcastEpisode;
import com.example.androidremake2.databinding.PodcastEpisodeListItemBinding;

import org.jetbrains.annotations.NotNull;

public class PodcastEpisodesAdapter extends ListAdapter<PodcastEpisode, PodcastEpisodeViewHolder> {

    public PodcastEpisodesAdapter(@NotNull DiffUtil.ItemCallback<PodcastEpisode> diffCallback) {
        super(diffCallback);
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
        return new PodcastEpisodeViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull @NotNull PodcastEpisodeViewHolder holder, int position) {
        PodcastEpisode episode = getItem(position);
        holder.bind(episode);
    }
}
