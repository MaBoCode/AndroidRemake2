package com.example.androidremake2.core.podcast;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

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
}
