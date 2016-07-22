package com.vladnamik.developer.androidphotogallery.service;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface ImageAPIService {
    String BASE_URL = "https://api.500px.com/v1/";

    @GET("photos?feature=popular&consumer_key=wB4ozJxTijCwNuggJvPGtBGCRqaZVcF6jsrzUadF")
    Call<Page> loadData(@Query("page") int page);
}
