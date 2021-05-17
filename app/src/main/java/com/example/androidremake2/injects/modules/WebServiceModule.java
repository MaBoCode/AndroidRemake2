package com.example.androidremake2.injects.modules;

import android.content.Context;

import com.example.androidremake2.R;
import com.example.androidremake2.core.podcast.PodcastService;
import com.example.androidremake2.core.user.UserService;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import dagger.hilt.InstallIn;
import dagger.hilt.android.qualifiers.ApplicationContext;
import dagger.hilt.components.SingletonComponent;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava3.RxJava3CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

@Module
@InstallIn(SingletonComponent.class)
public class WebServiceModule {

    @Provides
    @Singleton
    public OkHttpClient provideOkHttpClient(@ApplicationContext Context context) {
        OkHttpClient.Builder builder = new OkHttpClient.Builder();
        builder.addInterceptor(new Interceptor() {
            @NotNull
            @Override
            public Response intercept(@NotNull Chain chain) throws IOException {
                Request original = chain.request();

                String appName = context.getString(R.string.app_name);
                String listenAPIKey = context.getString(R.string.listen_api_key);

                Request request = original.newBuilder()
                        .header("User-Agent", appName)
                        .header("X-ListenAPI-Key", listenAPIKey)
                        .method(original.method(), original.body())
                        .build();
                return chain.proceed(request);
            }
        });
        return builder.build();
    }

    @Provides
    @Singleton
    public UserService provideUserService(OkHttpClient okHttpClient) {
        String serverUrl = "https://jsonplaceholder.typicode.com/";
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(serverUrl)
                .addConverterFactory(GsonConverterFactory.create())
                .addCallAdapterFactory(RxJava3CallAdapterFactory.create())
                .client(okHttpClient)
                .build();
        return retrofit.create(UserService.class);
    }

    @Provides
    @Singleton
    public PodcastService providePodcastService(OkHttpClient okHttpClient) {
        String serverUrl = "https://listen-api.listennotes.com/api/v2/";
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(serverUrl)
                .addConverterFactory(GsonConverterFactory.create())
                .addCallAdapterFactory(RxJava3CallAdapterFactory.create())
                .client(okHttpClient)
                .build();
        return retrofit.create(PodcastService.class);
    }

}
