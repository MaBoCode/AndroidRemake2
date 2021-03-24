package com.example.androidremake2.views.podcast;

import android.view.View;

import androidx.recyclerview.widget.RecyclerView;

import com.example.androidremake2.core.podcast.Podcast;
import com.example.androidremake2.databinding.PodcastListItemBinding;
import com.squareup.picasso.Picasso;

public class PodcastViewHolder extends RecyclerView.ViewHolder {

    protected PodcastListItemBinding binding;

    public PodcastViewHolder(PodcastListItemBinding binding) {
        super(binding.getRoot());
        this.binding = binding;
    }

    public void bind(final Podcast podcast, PodcastAdapter.OnPodcastItemClickListener podcastItemClickListener) {

        binding.txtCardTitle.setText(podcast.title);
        binding.txtCardDescription.setText(podcast.description);
        binding.btnPlay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                podcastItemClickListener.onPodcastItemClick(view, podcast);
            }
        });

        Picasso.get().load(podcast.imageUrl).into(binding.imgCardDestination);
    }
}
