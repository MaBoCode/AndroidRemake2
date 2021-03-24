package com.example.androidremake2.core.podcast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.List;

public class Podcast implements Serializable {

    @SerializedName("image")
    public String imageUrl;

    @SerializedName("thumbnail")
    public String thumbnailUrl;

    public String id;
    public String title;
    public String description;
    public String publisher;
    public String publishedAt;
    public List<PodcastEpisode> episodes;

    public Podcast(String imageUrl, String thumbnailUrl, String id, String title, String description,
                   String publisher, String publishedAt, List<PodcastEpisode> episodes) {
        this.imageUrl = imageUrl;
        this.thumbnailUrl = thumbnailUrl;
        this.id = id;
        this.title = title;
        this.description = description;
        this.publisher = publisher;
        this.publishedAt = publishedAt;
        this.episodes = episodes;
    }

    public Podcast(Podcast p) {
        this.imageUrl = p.imageUrl;
        this.thumbnailUrl = p.thumbnailUrl;
        this.id = p.id;
        this.title = p.title;
        this.description = p.description;
        this.publisher = p.publisher;
        this.publishedAt = p.publishedAt;
        this.episodes = p.episodes;
    }

    @Override
    public String toString() {
        return "Podcast{" +
                "imageUrl='" + imageUrl + '\'' +
                ", thumbnailUrl='" + thumbnailUrl + '\'' +
                ", id='" + id + '\'' +
                ", title='" + title + '\'' +
                ", description='" + description + '\'' +
                ", publisher='" + publisher + '\'' +
                ", publishedAt='" + publishedAt + '\'' +
                '}';
    }

    public static class PodcastDiff extends DiffUtil.ItemCallback<Podcast> {

        @Override
        public boolean areItemsTheSame(@NonNull Podcast oldItem, @NonNull Podcast newItem) {
            return oldItem.id.contentEquals(newItem.id);
        }

        @Override
        public boolean areContentsTheSame(@NonNull Podcast oldItem, @NonNull Podcast newItem) {
            return oldItem == newItem;
        }
    }
}
