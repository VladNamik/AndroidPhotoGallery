package com.vladnamik.developer.androidphotogallery.activities;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.NumberPicker;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.Toast;

import com.squareup.picasso.Picasso;
import com.vladnamik.developer.androidphotogallery.R;
import com.vladnamik.developer.androidphotogallery.service.Page;
import com.vladnamik.developer.androidphotogallery.service.ImageAPIService;
import com.vladnamik.developer.androidphotogallery.service.Photo;

import java.util.List;
import java.util.Random;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class MainActivity extends AppCompatActivity implements Callback<Page>{
    private ImageAPIService service;
    private NumberPicker pageNumberPicker;
    private int pageMinValue = 1;
    private int pageMaxValue = 1000;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
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

        //загружаем первую страницу
        loadPage(pageMinValue);
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

//    public void onPageNumberPickerClick(View view) {
//        loadPage(pageNumberPicker.getValue());
//    }

    @Override
    public void onResponse(Call<Page> call, Response<Page> response) {
        Log.d("MainActivity", "onResponse");

        //получаем данные из response
        final List<Photo> photos = response.body().getPhotos();
        int photosNumber = photos.size();
        TableLayout tableLayout = (TableLayout)findViewById(R.id.main_images_table_layout);
        tableLayout.removeAllViews();
        TableRow tableRow;
        ImageView imageView;

        //обработчик нажатия на картинку
        class OnImageClickListener implements View.OnClickListener {
            private String imageURL;

            public OnImageClickListener(String imageURL) {
                this.imageURL = imageURL;
            }

            @Override
            public void onClick(View v) {
                Intent intent = new Intent(v.getContext(), ImageActivity.class);
                intent.putExtra("image_url", imageURL);
                startActivity(intent);
            }
        }

        //загружаем фото в TableLayout
        for (int i = 0; i < photosNumber; i+= 2) {
            tableRow = (TableRow) LayoutInflater.from(this).inflate(R.layout.main_table_row_item, null);
            imageView = (ImageView)tableRow.findViewById(R.id.main_table_row_left_image);
            Picasso.with(this).load(photos.get(i).getImageURL()).into(imageView);
            imageView.setOnClickListener(new OnImageClickListener(photos.get(i).getImageURL()));


            //выходим при нечетном количестве фото на странице
            if ((i + 1) == photosNumber) {
                break;
            }

            imageView = (ImageView)tableRow.findViewById(R.id.main_table_row_right_image);
            Picasso.with(this).load(photos.get(i + 1).getImageURL()).into(imageView);
            imageView.setOnClickListener(new OnImageClickListener(photos.get(i + 1).getImageURL()));

            tableLayout.addView(tableRow);
        }

        //обновляем tableLayout
        tableLayout.requestLayout();
        tableLayout.invalidate();
    }

    @Override
    public void onFailure(Call<Page> call, Throwable t) {
        Log.d("MainActivity", "onFailure");
        Toast.makeText(MainActivity.this, t.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
    }
}
