package com.example.androidremake2.core.podcast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.Objects;

public class PodcastEpisode implements Serializable, Comparable<PodcastEpisode> {

    @SerializedName("audio_length_sec")
    public Integer duration;

    @SerializedName("image")
    public String imageUrl;

    @SerializedName("audio")
    public String audioUrl;

    @SerializedName("pub_date_ms")
    public Long publicationDateMs;

    public String id;
    public String title;
    public String description;

    @Override
    public int compareTo(PodcastEpisode episode) {
        return Long.compare(this.publicationDateMs, episode.publicationDateMs);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        PodcastEpisode episode = (PodcastEpisode) o;
        return Objects.equals(duration, episode.duration) &&
                Objects.equals(imageUrl, episode.imageUrl) &&
                Objects.equals(audioUrl, episode.audioUrl) &&
                Objects.equals(publicationDateMs, episode.publicationDateMs) &&
                Objects.equals(id, episode.id) &&
                Objects.equals(title, episode.title) &&
                Objects.equals(description, episode.description);
    }

    @Override
    public int hashCode() {
        return Objects.hash(duration, imageUrl, audioUrl, publicationDateMs, id, title, description);
    }

    @Override
    public String toString() {
        return "PodcastEpisode{" +
                "duration=" + duration +
                ", imageUrl='" + imageUrl + '\'' +
                ", audioUrl='" + audioUrl + '\'' +
                ", publicationDateMs=" + publicationDateMs +
                ", id='" + id + '\'' +
                ", title='" + title + '\'' +
                ", description='" + description + '\'' +
                '}';
    }

    public static class PodcastEpisodeDiff extends DiffUtil.ItemCallback<PodcastEpisode> {

        @Override
        public boolean areItemsTheSame(@NonNull PodcastEpisode oldItem, @NonNull PodcastEpisode newItem) {
            return oldItem.id.contentEquals(newItem.id);
        }

        @Override
        public boolean areContentsTheSame(@NonNull PodcastEpisode oldItem, @NonNull PodcastEpisode newItem) {
            return oldItem.equals(newItem);
        }
    }
}
