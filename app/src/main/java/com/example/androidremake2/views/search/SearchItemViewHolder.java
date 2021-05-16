package com.example.androidremake2.views.search;

import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.androidremake2.R;
import com.example.androidremake2.core.podcast.Podcast;
import com.example.androidremake2.databinding.SearchResultListItemBinding;

public class SearchItemViewHolder extends RecyclerView.ViewHolder {

    protected SearchResultListItemBinding binding;

    public SearchItemViewHolder(SearchResultListItemBinding binding) {
        super(binding.getRoot());
        this.binding = binding;
    }

    public void bind(final Podcast podcast) {

        binding.podcastTitleView.setText(podcast.title);
        binding.podcastDescriptionView.setText(podcast.publisher);

        Glide
                .with(binding.getRoot())
                .load(podcast.imageUrl)
                .placeholder(R.drawable.ic_launcher_background)
                .into(binding.podcastImgView);
    }
}
