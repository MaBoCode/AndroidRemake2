package com.example.androidremake2.core.podcast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class Podcast implements Serializable {

    public String id;

    @SerializedName("image_url")
    public String imageUrl;
    public String title;
    public String description;
    public String publishedAt;

    public Podcast(String id, String imageUrl, String title, String description, String publishedAt) {
        this.id = id;
        this.imageUrl = imageUrl;
        this.title = title;
        this.description = description;
        this.publishedAt = publishedAt;
    }

    public Podcast(Podcast p) {
        this.id = p.id;
        this.imageUrl = p.imageUrl;
        this.title = p.title;
        this.description = p.description;
        this.publishedAt = p.publishedAt;
    }

    @Override
    public String toString() {
        return "Podcast{" +
                "id='" + id + '\'' +
                ", imageUrl='" + imageUrl + '\'' +
                ", title='" + title + '\'' +
                ", description='" + description + '\'' +
                ", publishedAt='" + publishedAt + '\'' +
                '}';
    }

    public static class PodcastDiff extends DiffUtil.ItemCallback<Podcast> {

        @Override
        public boolean areItemsTheSame(@NonNull Podcast oldItem, @NonNull Podcast newItem) {
            return oldItem.id == newItem.id;
        }

        @Override
        public boolean areContentsTheSame(@NonNull Podcast oldItem, @NonNull Podcast newItem) {
            return oldItem == newItem;
        }
    }
}
