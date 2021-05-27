package com.example.androidremake2.views.podcast.utils;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;

import com.example.androidremake2.core.podcast.Podcast;
import com.example.androidremake2.databinding.PodcastListItemBinding;
import com.example.androidremake2.views.base.BaseRecyclerViewAdapter;
import com.example.androidremake2.views.base.BaseViewHolder;

import org.jetbrains.annotations.NotNull;

public class PodcastAdapter extends BaseRecyclerViewAdapter<Podcast, PodcastListItemBinding> {

    protected OnPodcastItemClickListener podcastItemClickListener;

    public PodcastAdapter(@NonNull DiffUtil.ItemCallback<Podcast> diffCallback, OnPodcastItemClickListener podcastItemClickListener) {
        super(diffCallback);
        this.podcastItemClickListener = podcastItemClickListener;
    }

    public interface OnPodcastItemClickListener {
        void playPodcast(View view, Podcast podcast);

        void displayPodcastDetails(View view, Podcast podcast);
    }

    @NonNull
    @NotNull
    @Override
    public BaseViewHolder<Podcast, PodcastListItemBinding> onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType) {
        PodcastListItemBinding binding = PodcastListItemBinding.inflate(
                LayoutInflater.from(parent.getContext()),
                parent,
                false
        );
        return new PodcastViewHolder(binding, podcastItemClickListener);
    }

    @Override
    public void onBindViewHolder(@NonNull @NotNull BaseViewHolder<Podcast, PodcastListItemBinding> holder, int position) {
        Podcast podcast = getItem(position);
        holder.bind(podcast);
    }
}
