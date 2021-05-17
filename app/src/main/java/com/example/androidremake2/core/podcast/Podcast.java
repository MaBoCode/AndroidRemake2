package com.example.androidremake2.core.podcast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.List;
import java.util.Objects;

public class Podcast implements Serializable {

    @SerializedName(value = "title", alternate = "title_original")
    public String title;

    @SerializedName(value = "description", alternate = "description_original")
    public String description;

    @SerializedName("image")
    public String imageUrl;

    @SerializedName("thumbnail")
    public String thumbnailUrl;

    @SerializedName("total_episodes")
    public int totalEpisodes;

    @SerializedName(value = "publisher", alternate = "publisher_original")
    public String publisher;

    public String id;
    public String publishedAt;
    public int nextEpisodeIndex = 0;
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

    public PodcastEpisode getFirstEpisode() {
        return episodes.get(0);
    }

    public PodcastEpisode getLastEpisode() {
        return episodes.get(episodes.size() - 1);
    }

    public PodcastEpisode getNextEpisode() {
        if (nextEpisodeIndex >= episodes.size()) {
            return null;
        }
        return episodes.get(nextEpisodeIndex++);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Podcast podcast = (Podcast) o;
        return totalEpisodes == podcast.totalEpisodes &&
                nextEpisodeIndex == podcast.nextEpisodeIndex &&
                Objects.equals(title, podcast.title) &&
                Objects.equals(description, podcast.description) &&
                Objects.equals(imageUrl, podcast.imageUrl) &&
                Objects.equals(thumbnailUrl, podcast.thumbnailUrl) &&
                Objects.equals(publisher, podcast.publisher) &&
                Objects.equals(id, podcast.id) &&
                Objects.equals(publishedAt, podcast.publishedAt) &&
                Objects.equals(episodes, podcast.episodes);
    }

    @Override
    public int hashCode() {
        return Objects.hash(title, description, imageUrl, thumbnailUrl, totalEpisodes, publisher, id, publishedAt, nextEpisodeIndex, episodes);
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
                ", nextEpisodeIndex=" + nextEpisodeIndex +
                ", episodes=" + episodes +
                '}';
    }

    public static class PodcastDiff extends DiffUtil.ItemCallback<Podcast> {

        @Override
        public boolean areItemsTheSame(@NonNull Podcast oldItem, @NonNull Podcast newItem) {
            return oldItem.id.contentEquals(newItem.id);
        }

        @Override
        public boolean areContentsTheSame(@NonNull Podcast oldItem, @NonNull Podcast newItem) {
            return oldItem.equals(newItem);
        }
    }
}
