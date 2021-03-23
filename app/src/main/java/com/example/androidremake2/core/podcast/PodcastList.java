package com.example.androidremake2.core.podcast;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class PodcastList {

    @SerializedName("collection")
    public List<Podcast> podcasts;

}
