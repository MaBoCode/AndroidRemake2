package com.example.androidremake2.views.podcast.utils;

import com.bumptech.glide.Glide;
import com.example.androidremake2.R;
import com.example.androidremake2.core.podcast.Podcast;
import com.example.androidremake2.core.podcast.PodcastEpisode;
import com.example.androidremake2.databinding.PodcastEpisodeListItemBinding;
import com.example.androidremake2.views.base.BaseViewHolder;

import org.jsoup.Jsoup;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class PodcastEpisodeViewHolder extends BaseViewHolder<PodcastEpisode, PodcastEpisodeListItemBinding> {

    protected Podcast podcast;

    public PodcastEpisodeViewHolder(PodcastEpisodeListItemBinding binding, Podcast podcast) {
        super(binding);
        this.podcast = podcast;
    }

    @Override
    public void bind(PodcastEpisode episode) {

        if (episode == null) {
            binding.lyItem.setBackgroundResource(R.drawable.bg_podcast_episode_skeleton);
            binding.podcastEpisodeTitleTxt.setText("");
            binding.podcastEpisodeDescriptionTxt.setText("");
            binding.podcastPublisherTxt.setText("");
            binding.podcastEpisodeDurationTxt.setText("");
            binding.podcastEpisodeImg.setImageResource(R.drawable.bg_podcast_episode_skeleton);
            return;
        }

        binding.lyShimmer.hideShimmer();
        binding.podcastEpisodeTitleTxt.setText(episode.title);
        binding.podcastPublisherTxt.setText(podcast.publisher);
        binding.podcastEpisodeDescriptionTxt.setText(Jsoup.parse(episode.description).text());

        SimpleDateFormat formatter = new SimpleDateFormat("HH:mm:ss", Locale.FRANCE);
        String durationTxt = String.format("%s mn", formatter.format(new Date(episode.duration)));
        binding.podcastEpisodeDurationTxt.setText(durationTxt);

        Glide
                .with(binding.getRoot())
                .load(episode.imageUrl)
                .into(binding.podcastEpisodeImg);
    }
}
