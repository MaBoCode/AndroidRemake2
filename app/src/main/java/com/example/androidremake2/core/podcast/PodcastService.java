package com.example.androidremake2.core.podcast;

import java.util.List;

import io.reactivex.rxjava3.core.Observable;
import retrofit2.http.GET;
import retrofit2.http.Headers;
import retrofit2.http.Path;

public interface PodcastService {

    @Headers("Authorization: Bearer eyJhcGlfa2V5IjoiNzVkMzc3N2M3NWFhM2QwOTkxOWEyZTI4ZjhiM2M1YTkifQ==")
    @GET("podcasts")
    Observable<PodcastList> getPodcasts();

    @GET("podcasts/{podcastId}")
    Observable<Podcast> getPodcast(
            @Path("podcastId") String podcastId
    );

}
