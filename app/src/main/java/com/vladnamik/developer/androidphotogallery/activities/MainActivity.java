package com.vladnamik.developer.androidphotogallery.activities;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.GridView;
import android.widget.NumberPicker;
import android.widget.Toast;

import com.vladnamik.developer.androidphotogallery.R;
import com.vladnamik.developer.androidphotogallery.adapters.ImageViewAdapter;
import com.vladnamik.developer.androidphotogallery.service.Page;
import com.vladnamik.developer.androidphotogallery.service.ImageAPIService;
import com.vladnamik.developer.androidphotogallery.service.Photo;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class MainActivity extends AppCompatActivity implements Callback<Page>{
    private static final String MAIN_ACTIVITY_LOG_TAG = "MainActivity";

    private NumberPicker pageNumberPicker;
    private int pageMinValue = 1;
    private int pageMaxValue = 1000;

    private ImageAPIService service;
    private ImageViewAdapter adapterForImages;
    private List<Photo> photos = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        pageNumberPickerInit();

        adapterForPhotosInit();

        //загружаем первую страницу
        loadPage(pageMinValue);
    }

    private void pageNumberPickerInit() {
        pageNumberPicker = (NumberPicker)findViewById(R.id.page_number_picker);
        pageNumberPicker.setValue(pageMinValue);
        pageNumberPicker.setMinValue(pageMinValue);
        pageNumberPicker.setMaxValue(pageMaxValue);
        pageNumberPicker.setOnValueChangedListener(new NumberPicker.OnValueChangeListener() {
            @Override
            public void onValueChange(NumberPicker picker, int oldVal, int newVal) {
                loadPage(newVal);
            }
        });
    }

    private void adapterForPhotosInit() {

        adapterForImages = new ImageViewAdapter(this, photos);
        GridView imageViewParentView = (GridView)findViewById(R.id.main_images_grid_view);
        imageViewParentView.setAdapter(adapterForImages);
    }

    private void loadPage(int pageNumber) {
        if (service == null) {
            Retrofit retrofit = new Retrofit.Builder()
                    .baseUrl(ImageAPIService.BASE_URL)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
            service = retrofit.create(ImageAPIService.class);
        }

        Call<Page> call = service.loadData(pageNumber);
        //асинхронный вызов
        call.enqueue(this);
    }

    public void onPageLeftArrowClick(View view) {
        if (pageNumberPicker.getValue() != pageMinValue) {
            pageNumberPicker.setValue(pageNumberPicker.getValue() - 1);
            loadPage(pageNumberPicker.getValue());
        }
    }

    public void onPageRightArrowClick(View view) {
        if (pageNumberPicker.getValue() != pageMaxValue) {
            pageNumberPicker.setValue(pageNumberPicker.getValue() + 1);
            loadPage(pageNumberPicker.getValue());
        }
    }

    @Override
    public void onResponse(Call<Page> call, Response<Page> response) {
        photos.clear();

        //получаем данные из response
        photos.addAll(response.body().getPhotos());

        //обновляем adapter
        adapterForImages.notifyDataSetChanged();
    }

    @Override
    public void onFailure(Call<Page> call, Throwable t) {
        Log.d(MAIN_ACTIVITY_LOG_TAG, "onFailure");
        Toast.makeText(MainActivity.this, t.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
    }
}
