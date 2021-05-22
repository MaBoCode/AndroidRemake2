package com.example.androidremake2.views.search.utils;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;

import com.example.androidremake2.core.podcast.Podcast;
import com.example.androidremake2.databinding.SearchResultListItemBinding;
import com.example.androidremake2.views.podcast.utils.PodcastAdapter;

public class SearchAdapter extends ListAdapter<Podcast, SearchItemViewHolder> {

    protected PodcastAdapter.OnPodcastItemClickListener podcastItemClickListener;

    public SearchAdapter(@NonNull DiffUtil.ItemCallback<Podcast> diffCallback, PodcastAdapter.OnPodcastItemClickListener podcastItemClickListener) {
        super(diffCallback);
        this.podcastItemClickListener = podcastItemClickListener;
    }

    @NonNull
    @Override
    public SearchItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        SearchResultListItemBinding binding = SearchResultListItemBinding.inflate(
                LayoutInflater.from(parent.getContext()),
                parent,
                false
        );
        return new SearchItemViewHolder(binding, podcastItemClickListener);
    }

    @Override
    public void onBindViewHolder(@NonNull SearchItemViewHolder holder, int position) {
        Podcast podcast = getItem(position);
        holder.bind(podcast);
    }
}
