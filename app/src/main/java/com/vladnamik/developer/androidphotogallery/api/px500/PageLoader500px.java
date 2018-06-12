package com.vladnamik.developer.androidphotogallery.api.px500;

import android.util.Log;

import com.vladnamik.developer.androidphotogallery.api.PageLoader;
import com.vladnamik.developer.androidphotogallery.api.entities.Page;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class PageLoader500px implements PageLoader{
    private static final String PAGE_LOADER_LOG_TAG = "PageLoader";

    private final Callback<Page> pageCallback;
    private ImageAPI service;

    public PageLoader500px(Callback<Page> pageCallback) {
        this.pageCallback = pageCallback;
    }

    public void loadPage(int pageNumber) {
        Log.d(PAGE_LOADER_LOG_TAG, "trying to loadPage #" + pageNumber);
        if (service == null) {
            Retrofit retrofit = new Retrofit.Builder()
                    .baseUrl(ImageAPI.BASE_URL)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
            service = retrofit.create(ImageAPI.class);
        }

        Call<Page> call = service.loadData(pageNumber);
        //async call
        call.enqueue(pageCallback);
    }
}
