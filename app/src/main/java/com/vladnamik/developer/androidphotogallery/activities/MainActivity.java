package com.vladnamik.developer.androidphotogallery.activities;

import android.content.Context;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.GridView;
import android.widget.NumberPicker;
import android.widget.Toast;

import com.vladnamik.developer.androidphotogallery.R;
import com.vladnamik.developer.androidphotogallery.adapters.ImageViewsListAdapter;
import com.vladnamik.developer.androidphotogallery.api.entities.Page;
import com.vladnamik.developer.androidphotogallery.api.ImageAPI;
import com.vladnamik.developer.androidphotogallery.api.entities.Photo;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class MainActivity extends AppCompatActivity {
    private static final String MAIN_ACTIVITY_LOG_TAG = "MainActivity";

    private NumberPicker pageNumberPicker;
    private final int pageMinValue = 1;
    private final int pageMaxValue = 1000;

    private int currentPageValue;

    private ImageAPI service;
    private PageCallBack pageCallBack;
    private ImageViewsListAdapter adapterForImages;
    private final List<Photo> photos = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //current page value retrieving
        if (savedInstanceState !=null) {
            currentPageValue = savedInstanceState.getInt("current_page_value");
        } else {
            currentPageValue = pageMinValue;
        }

        pageNumberPickerInit();

        adapterForPhotosInit();

        loadPage(currentPageValue);
    }

    private void pageNumberPickerInit() {
        pageNumberPicker = (NumberPicker) findViewById(R.id.page_number_picker);
        pageNumberPicker.setMinValue(pageMinValue);
        pageNumberPicker.setMaxValue(pageMaxValue);
        pageNumberPicker.setValue(currentPageValue);
        pageNumberPicker.setOnValueChangedListener(new NumberPicker.OnValueChangeListener() {
            @Override
            public void onValueChange(NumberPicker picker, int oldVal, int newVal) {
                currentPageValue = newVal;
                loadPage(newVal);
            }
        });
    }

    private void adapterForPhotosInit() {

        adapterForImages = new ImageViewsListAdapter(this, photos);
        GridView imageViewParentView = (GridView) findViewById(R.id.main_images_grid_view);
        imageViewParentView.setAdapter(adapterForImages);
    }

    private void loadPage(int pageNumber) {
        Log.d(MAIN_ACTIVITY_LOG_TAG, "trying to loadPage #" + pageNumber);
        if (service == null) {
            Retrofit retrofit = new Retrofit.Builder()
                    .baseUrl(ImageAPI.BASE_URL)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
            service = retrofit.create(ImageAPI.class);
            pageCallBack = new PageCallBack(this, photos, adapterForImages);
        }

        Call<Page> call = service.loadData(pageNumber);
        //async call
        call.enqueue(pageCallBack);
    }

    public void onPageLeftArrowClick(View view) {
        if (pageNumberPicker.getValue() != pageMinValue) {
            pageNumberPicker.setValue(currentPageValue - 1);
            currentPageValue--;
            loadPage(pageNumberPicker.getValue());
        }
    }

    public void onPageRightArrowClick(View view) {
        if (pageNumberPicker.getValue() != pageMaxValue) {
            pageNumberPicker.setValue(currentPageValue + 1);
            currentPageValue++;
            loadPage(pageNumberPicker.getValue());
        }
    }

    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt("current_page_value", currentPageValue);
    }

    private class PageCallBack implements Callback<Page> {
        private final Context context;
        private final List<Photo> photos;
        private final ImageViewsListAdapter adapterForImages;

        PageCallBack(Context context, List<Photo> photos, ImageViewsListAdapter adapterForImages) {
            this.context = context;
            this.photos = photos;
            this.adapterForImages = adapterForImages;
        }

        @Override
        public void onResponse(Call<Page> call, Response<Page> response) {
            photos.clear();

            //getting data from response
            photos.addAll(response.body().getPhotos());

            //update adapter
            adapterForImages.notifyDataSetChanged();

            Log.d(MAIN_ACTIVITY_LOG_TAG, "end getting response");
        }

        @Override
        public void onFailure(Call<Page> call, Throwable t) {
            Log.d(MAIN_ACTIVITY_LOG_TAG, "request ended with error " + t.getMessage());
            Toast.makeText(context, t.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
        }
    }

}
