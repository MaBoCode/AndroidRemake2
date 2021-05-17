package com.example.androidremake2.core.podcast;

import io.reactivex.rxjava3.core.Observable;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface PodcastService {

    @GET("best_podcasts")
    Observable<GetBestPodcastsResponse> getBestPodcasts(
            @Query("page") Integer page,
            @Query("region") String region
    );

    @GET("podcasts/{podcastId}")
    Observable<Podcast> getPodcast(
            @Path("podcastId") String podcastId
    );

    @GET("search")
    Observable<SearchPodcastList> searchPodcasts(
            @Query("q") String query,
            @Query("type") String type,
            @Query("only_in") String onlyIn,
            @Query("offset") Integer offset,
            @Query("region") String region
    );

}
