package com.vladnamik.developer.androidphotogallery.activities;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.content.res.ResourcesCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.GridView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.vladnamik.developer.androidphotogallery.R;
import com.vladnamik.developer.androidphotogallery.adapters.ImageViewsListAdapter;
import com.vladnamik.developer.androidphotogallery.api.mock.PageLoaderMock;
import com.vladnamik.developer.androidphotogallery.api.PageLoader;
import com.vladnamik.developer.androidphotogallery.api.entities.Page;
import com.vladnamik.developer.androidphotogallery.api.entities.Photo;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class PageFragment extends Fragment {
    public static final String ARGUMENT_PAGE_NUMBER = "currentPageValue";
    private static final String PAGE_FRAGMENT_LOG_TAG = "PageFragment";
    private static final String INTERNET_CONNECTION_EXCEPTION_MESSAGE = "That seems like you have no internet connection";
    private static final int LOADER_HEIGHT = 80;

    private final List<Photo> photos = new ArrayList<>();
    private Dialog progressDialog;

    private int currentPageValue;
    private ImageViewsListAdapter adapterForImages;

    private Lock progressDialogLock = new ReentrantLock();
    private boolean needToStartProgressDialog = true;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        currentPageValue = getArguments().getInt(ARGUMENT_PAGE_NUMBER);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.photos_fragment, null);
        adapterForPhotosInit(view);

        PageLoader pageLoader = new PageLoaderMock(new PageCallback(getActivity(), photos, adapterForImages));

        pageLoader.loadPage(currentPageValue);
        return view;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    public void startProgressDialogBeforeRequest() {
        progressDialogLock.lock();
        try {
            if (needToStartProgressDialog) {
                getProgressDialog().show();
            }
        } finally {
            progressDialogLock.unlock();
        }
    }

    public void stopProgressDialogAfterRequest() {
        progressDialogLock.lock();
        try {
            needToStartProgressDialog = false;
        } finally {
            progressDialogLock.unlock();
        }

        if (getProgressDialog().isShowing()) {
            getProgressDialog().dismiss();
        }
    }

    private void adapterForPhotosInit(View mainView) {

        adapterForImages = new ImageViewsListAdapter(getActivity(), photos);
        GridView imageViewParentView = mainView.findViewById(R.id.main_images_grid_view);
        imageViewParentView.setAdapter(adapterForImages);
    }

    private Dialog getProgressDialog() {
        if (progressDialog == null) {
            progressDialog = new Dialog(getContext());
            ProgressBar progressBar = new ProgressBar(getContext());

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


    public class PageCallback implements Callback<Page> {
        private final Context context;
        private final List<Photo> photos;
        private final ImageViewsListAdapter adapterForImages;

        PageCallback(Context context, List<Photo> photos, ImageViewsListAdapter adapterForImages) {
            this.context = context;
            this.photos = photos;
            this.adapterForImages = adapterForImages;
        }

        @Override
        public void onResponse(Call<Page> call, Response<Page> response) {
            //getting data from response
            List<Photo> newPhotos = response.body().getPhotos();
            onResponse(newPhotos);
        }

        public void onResponse(List<Photo> newPhotos)
        {
            photos.clear();

            photos.addAll(newPhotos);

            //No more sense to show ProgressDialog
            stopProgressDialogAfterRequest();

            //update adapter
            adapterForImages.notifyDataSetChanged();

            Log.d(PAGE_FRAGMENT_LOG_TAG, "end getting response");
        }

        @Override
        public void onFailure(Call<Page> call, Throwable t) {
            Log.d(PAGE_FRAGMENT_LOG_TAG, "request ended with error " + t.getMessage());
            Toast.makeText(context, INTERNET_CONNECTION_EXCEPTION_MESSAGE, Toast.LENGTH_LONG).show();
            stopProgressDialogAfterRequest();
        }
    }
}