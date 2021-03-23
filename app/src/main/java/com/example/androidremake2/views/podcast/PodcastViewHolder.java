package com.example.androidremake2.views.podcast;

import android.net.Uri;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.RecyclerView;

import com.example.androidremake2.R;
import com.example.androidremake2.core.podcast.Podcast;
import com.example.androidremake2.databinding.PodcastListItemBinding;
import com.squareup.picasso.Picasso;

public class PodcastViewHolder extends RecyclerView.ViewHolder {

    protected PodcastListItemBinding binding;

    public PodcastViewHolder(PodcastListItemBinding binding) {
        super(binding.getRoot());
        this.binding = binding;
    }

    public void bind(final Podcast podcast) {
        binding.txtCardTitle.setText(podcast.title);
        binding.txtCardDescription.setText(podcast.description);

        binding.btnPlay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                NavController navController = Navigation.findNavController(binding.getRoot());

                MainFragmentDirections.PlayPodcastAction action = MainFragmentDirections.playPodcastAction(podcast);

                if (navController != null) {
                    navController.navigate(action);
                }
            }
        });

        Picasso.get().load(podcast.imageUrl).into(binding.imgCardDestination);
    }
}
