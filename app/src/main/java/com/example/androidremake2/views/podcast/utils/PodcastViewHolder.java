package com.example.androidremake2.views.podcast.utils;

import android.view.View;

import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.androidremake2.R;
import com.example.androidremake2.core.podcast.Podcast;
import com.example.androidremake2.databinding.PodcastListItemBinding;

import org.jsoup.Jsoup;

public class PodcastViewHolder extends RecyclerView.ViewHolder {

    protected PodcastListItemBinding binding;

    public PodcastViewHolder(PodcastListItemBinding binding) {
        super(binding.getRoot());
        this.binding = binding;
    }

    public void bind(final Podcast podcast, PodcastAdapter.OnPodcastItemClickListener podcastItemClickListener) {

        binding.txtCardTitle.setText(podcast.title);
        binding.txtCardDescription.setText(Jsoup.parse(podcast.description).text());
        binding.btnPlay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                podcastItemClickListener.onPodcastItemClick(view, podcast);
            }
        });

        Glide
                .with(binding.getRoot())
                .load(podcast.imageUrl)
                .placeholder(R.drawable.ic_launcher_background)
                .into(binding.imgCardDestination);
    }
}
