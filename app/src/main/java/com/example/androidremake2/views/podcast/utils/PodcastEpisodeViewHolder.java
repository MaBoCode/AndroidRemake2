package com.example.androidremake2.views.podcast.utils;

import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.androidremake2.R;
import com.example.androidremake2.core.podcast.Podcast;
import com.example.androidremake2.core.podcast.PodcastEpisode;
import com.example.androidremake2.databinding.PodcastEpisodeListItemBinding;

import org.jsoup.Jsoup;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class PodcastEpisodeViewHolder extends RecyclerView.ViewHolder {

    protected PodcastEpisodeListItemBinding binding;

    public PodcastEpisodeViewHolder(PodcastEpisodeListItemBinding binding) {
        super(binding.getRoot());
        this.binding = binding;
    }

    public void bind(final Podcast podcast, final PodcastEpisode episode) {
        binding.podcastEpisodeTitleTxt.setText(episode.title);
        binding.podcastPublisherTxt.setText(podcast.publisher);
        binding.podcastEpisodeDescriptionTxt.setText(Jsoup.parse(episode.description).text());

        SimpleDateFormat formatter = new SimpleDateFormat("HH:mm:ss", Locale.FRANCE);
        String durationTxt = String.format("%s mn", formatter.format(new Date(episode.duration)));
        binding.podcastEpisodeDurationTxt.setText(durationTxt);

        Glide
                .with(binding.getRoot())
                .load(episode.imageUrl)
                .placeholder(R.drawable.ic_launcher_background)
                .into(binding.podcastEpisodeImg);
    }
}
