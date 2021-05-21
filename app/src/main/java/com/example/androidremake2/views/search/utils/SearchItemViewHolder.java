package com.example.androidremake2.views.search.utils;

import android.view.View;

import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.androidremake2.R;
import com.example.androidremake2.core.podcast.Podcast;
import com.example.androidremake2.databinding.SearchResultListItemBinding;
import com.example.androidremake2.views.podcast.utils.PodcastAdapter;

public class SearchItemViewHolder extends RecyclerView.ViewHolder {

    protected SearchResultListItemBinding binding;

    protected PodcastAdapter.OnPodcastItemClickListener podcastItemClickListener;

    public SearchItemViewHolder(SearchResultListItemBinding binding, PodcastAdapter.OnPodcastItemClickListener podcastItemClickListener) {
        super(binding.getRoot());
        this.binding = binding;
        this.podcastItemClickListener = podcastItemClickListener;
    }

    public void bind(final Podcast podcast) {

        binding.podcastTitleView.setText(podcast.title);
        binding.podcastDescriptionView.setText(podcast.publisher);
        binding.getRoot().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                podcastItemClickListener.displayPodcastDetails(view, podcast);
            }
        });

        Glide
                .with(binding.getRoot())
                .load(podcast.imageUrl)
                .placeholder(R.drawable.ic_launcher_background)
                .into(binding.podcastImgView);
    }
}
