package com.example.androidremake2.core.podcast;

import io.reactivex.rxjava3.core.Observable;
import retrofit2.http.GET;
import retrofit2.http.Headers;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface PodcastService {

    @Headers("X-ListenAPI-Key: 79387f0c059f4290aa717f23b2f1356c")
    @GET("best_podcasts?page=1")
    Observable<PodcastList> getBestPodcasts();

    @Headers("X-ListenAPI-Key: 79387f0c059f4290aa717f23b2f1356c")
    @GET("podcasts/{podcastId}")
    Observable<Podcast> getPodcast(
            @Path("podcastId") String podcastId
    );

    @Headers("X-ListenAPI-Key: 79387f0c059f4290aa717f23b2f1356c")
    @GET("search")
    Observable<SearchPodcastList> searchPodcasts(
            @Query("q") String query,
            @Query("type") String type,
            @Query("only_in") String onlyIn,
            @Query("offset") Integer offset
    );

}
