package com.example.androidremake2.core.podcast;

import io.reactivex.rxjava3.core.Observable;
import retrofit2.http.GET;
import retrofit2.http.Headers;
import retrofit2.http.Path;

public interface PodcastService {

    @Headers("X-ListenAPI-Key: 79387f0c059f4290aa717f23b2f1356c")
    @GET("best_podcasts?page=1")
    Observable<PodcastList> getBestPodcasts();

    @Headers("X-ListenAPI-Key: 79387f0c059f4290aa717f23b2f1356c")
    @GET("podcasts/{podcastId}")
    Observable<Podcast> getPodcast(
            @Path("podcastId") String podcastId
    );

}
