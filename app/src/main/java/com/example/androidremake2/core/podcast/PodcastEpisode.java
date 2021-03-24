package com.example.androidremake2.core.podcast;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class PodcastEpisode implements Serializable {

    @SerializedName("audio_length_sec")
    public Integer duration;

    @SerializedName("image")
    public String imageUrl;

    @SerializedName("audio")
    public String audioUrl;

    public String id;
    public String title;
    public String description;

}
