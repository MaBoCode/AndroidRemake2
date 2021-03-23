package com.example.androidremake2.views.podcast;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;

import com.example.androidremake2.core.podcast.Podcast;
import com.example.androidremake2.databinding.PodcastListItemBinding;

public class PodcastAdapter extends ListAdapter<Podcast, PodcastViewHolder> {

    public PodcastAdapter(@NonNull DiffUtil.ItemCallback<Podcast> diffCallback) {
        super(diffCallback);
    }

    @NonNull
    @Override
    public PodcastViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        PodcastListItemBinding binding = PodcastListItemBinding.inflate(
                LayoutInflater.from(parent.getContext()),
                parent,
                false
        );
        return new PodcastViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull PodcastViewHolder holder, int position) {
        Podcast podcast = getItem(position);
        holder.bind(podcast);
    }
}
