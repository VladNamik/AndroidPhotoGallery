package com.vladnamik.developer.androidphotogallery.activities;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.res.ResourcesCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.NumberPicker;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.vladnamik.developer.androidphotogallery.R;
import com.vladnamik.developer.androidphotogallery.adapters.ImageViewsListAdapter;
import com.vladnamik.developer.androidphotogallery.api.ImageAPI;
import com.vladnamik.developer.androidphotogallery.api.entities.Page;
import com.vladnamik.developer.androidphotogallery.api.entities.Photo;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class MainActivity extends AppCompatActivity {
    private static final String MAIN_ACTIVITY_LOG_TAG = "MainActivity";
    private static final String INTERNET_CONNECTION_EXCEPTION_MESSAGE = "That seems like you have no internet connection";
    private static final int LOADER_HEIGHT = 80;

    private final int pageMinValue = 1;
    private final int pageMaxValue = 1000;
    private final List<Photo> photos = new ArrayList<>();

    private NumberPicker pageNumberPicker;
    private int currentPageValue;
    private ImageAPI service;
    private PageCallBack pageCallBack;
    private ImageViewsListAdapter adapterForImages;
    private Dialog progressDialog;

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
        setNumberPickerTextColor(pageNumberPicker, ContextCompat.getColor(this, R.color.mainTextColor));

        pageNumberPicker.setOnScrollListener(new NumberPicker.OnScrollListener() {
            @Override
            public void onScrollStateChange(NumberPicker view, int scrollState) {
                if (scrollState == NumberPicker.OnScrollListener.SCROLL_STATE_IDLE){
                    currentPageValue = view.getValue();
                    loadPage(currentPageValue);
                }
            }
        });
    }

    //Set text color in NumberPicker object
    private void setNumberPickerTextColor(NumberPicker numberPicker, int color) {
        final int count = numberPicker.getChildCount();
        for (int i = 0; i < count; i++) {
            View child = numberPicker.getChildAt(i);
            if (child instanceof EditText) {
                try {
                    Field selectorWheelPaintField = numberPicker.getClass()
                            .getDeclaredField("mSelectorWheelPaint");
                    selectorWheelPaintField.setAccessible(true);
                    ((Paint) selectorWheelPaintField.get(numberPicker)).setColor(color);
                    ((EditText) child).setTextColor(color);
                    numberPicker.invalidate();
                    return;
                } catch (NoSuchFieldException | IllegalAccessException | IllegalArgumentException e) {
                    Log.w(MAIN_ACTIVITY_LOG_TAG, e);
                }
            }
        }
    }

    private void adapterForPhotosInit() {

        adapterForImages = new ImageViewsListAdapter(this, photos);
        GridView imageViewParentView = (GridView) findViewById(R.id.main_images_grid_view);
        imageViewParentView.setAdapter(adapterForImages);
    }

    private Dialog getProgressDialog() {
        if (progressDialog == null) {
            progressDialog = new Dialog(this);
            ProgressBar progressBar = new ProgressBar(this);

            progressBar.setIndeterminateDrawable(ResourcesCompat.getDrawable(getResources(), R.drawable.progress_dialog_loader, null));
            progressDialog.addContentView(progressBar, new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, LOADER_HEIGHT));

            //set divider in dialog transparent
            int dividerId = progressDialog.getContext().getResources()
                    .getIdentifier("android:id/titleDivider", null, null);
            View divider = progressDialog.findViewById(dividerId);
            if (divider != null) {
                divider.setBackgroundColor(Color.TRANSPARENT);
            }

            //set background color to dialog transparent
            Window window = progressDialog.getWindow();
            if (window != null) {
                window.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            }
        }
        return progressDialog;
    }

    private void loadPage(int pageNumber) {
        getProgressDialog().show();
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

            //delete after
            Thread t = new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        Thread.sleep(2000);
                    } catch (InterruptedException e) {
                        Log.d(MAIN_ACTIVITY_LOG_TAG, "interrupted with message: " + e.getLocalizedMessage());
                    }
                }
            });
            t.start();
            try {
                t.join();
            } catch (InterruptedException e) {
                Log.d(MAIN_ACTIVITY_LOG_TAG, "interrupted with message: " + e.getLocalizedMessage());
            }

            //update adapter
            adapterForImages.notifyDataSetChanged();

            Log.d(MAIN_ACTIVITY_LOG_TAG, "end getting response");
            progressDialog.dismiss();
        }

        @Override
        public void onFailure(Call<Page> call, Throwable t) {
            Log.d(MAIN_ACTIVITY_LOG_TAG, "request ended with error " + t.getMessage());
            progressDialog.dismiss();
            Toast.makeText(context, INTERNET_CONNECTION_EXCEPTION_MESSAGE, Toast.LENGTH_LONG).show();
        }
    }

}
