package com.example.androidremake2.core.podcast;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.List;

public class SearchPodcastList implements Serializable {

    @SerializedName("next_offset")
    public Integer offset;

    public List<Podcast> results;

}
