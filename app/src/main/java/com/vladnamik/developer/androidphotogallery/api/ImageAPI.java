package com.vladnamik.developer.androidphotogallery.api;

import com.vladnamik.developer.androidphotogallery.api.entities.Page;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

/**
 * Interface used via Retrofit to communicate with image api service
 */
public interface ImageAPI {
    String BASE_URL = "https://api.500px.com/v1/";
    String CONSUMER_KEY = "UAmBBDhE8UaesFe6MOq0A1wugIe9MLDDo0kp6yC0";

    @GET("photos?feature=popular&consumer_key=" + CONSUMER_KEY)
    Call<Page> loadData(@Query("page") int page);
}
