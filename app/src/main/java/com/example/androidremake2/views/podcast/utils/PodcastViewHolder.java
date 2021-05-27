package com.example.androidremake2.views.podcast.utils;

import com.bumptech.glide.Glide;
import com.example.androidremake2.core.podcast.Podcast;
import com.example.androidremake2.databinding.PodcastListItemBinding;
import com.example.androidremake2.views.base.BaseViewHolder;

import org.jsoup.Jsoup;

public class PodcastViewHolder extends BaseViewHolder<Podcast, PodcastListItemBinding> {

    protected PodcastAdapter.OnPodcastItemClickListener listener;

    public PodcastViewHolder(PodcastListItemBinding binding, PodcastAdapter.OnPodcastItemClickListener listener) {
        super(binding);
        this.listener = listener;
    }

    @Override
    public void bind(Podcast podcast) {

        if (podcast == null) {
            binding.txtCardTitle.setText("");
            binding.txtCardDescription.setText("");
            return;
        }

        binding.podcastCardView.setOnClickListener(view -> listener.displayPodcastDetails(view, podcast));
        binding.txtCardTitle.setText(podcast.title);
        binding.txtCardDescription.setText(Jsoup.parse(podcast.description).text());
        binding.btnPlay.setOnClickListener(view -> listener.playPodcast(view, podcast));

        Glide
                .with(binding.getRoot())
                .load(podcast.imageUrl)
                .into(binding.imgCardDestination);
    }
}
