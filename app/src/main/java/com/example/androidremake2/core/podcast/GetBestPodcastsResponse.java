package com.example.androidremake2.core.podcast;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.List;

public class GetBestPodcastsResponse implements Serializable {

     public List<Podcast> podcasts;
     public Integer total;

     @SerializedName("has_next")
     public boolean hasNext;

     @SerializedName("previous_page_number")
     public Integer previousPageNumber;

     @SerializedName("next_page_number")
     public Integer nextPageNumber;

}
