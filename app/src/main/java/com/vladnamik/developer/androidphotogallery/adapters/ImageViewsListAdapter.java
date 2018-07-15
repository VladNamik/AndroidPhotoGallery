package com.vladnamik.developer.androidphotogallery.adapters;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;
import com.vladnamik.developer.androidphotogallery.R;
import com.vladnamik.developer.androidphotogallery.api.entities.Photo;

import java.util.List;


public class ImageViewsListAdapter extends ArrayAdapter<Photo> {
    private static final String ADAPTER_LOG_TAG = "ImageViewsListAdapter";
    private final Context context;

    public ImageViewsListAdapter(Context context, List<Photo> photos) {
        super(context, R.layout.image_item, photos);
        this.context = context;
    }

    @NonNull
    @Override
    public View getView(int position, View convertView, @NonNull ViewGroup parent) {

        Log.d(ADAPTER_LOG_TAG, "start trying to get view");

        if (convertView == null) {
            convertView = LayoutInflater.from(getContext())
                    .inflate(R.layout.image_item, null);
        }

        ImageView imageView = (ImageView) convertView;
        Photo photo = getItem(position);

        if (photo == null) {
            return imageView;
        }

        Picasso.with(context).load(photo.getImageURL()).into(imageView);
        imageView.setOnClickListener(new OnImageClickListener(photo.getURL()));

        Log.d(ADAPTER_LOG_TAG, "end trying to get view");

        return convertView;
    }

    /**
     * On image click handler
     */
    private class OnImageClickListener implements View.OnClickListener {
        private final String url;

        private OnImageClickListener(String url) {
            this.url = url;
        }

        @Override
        public void onClick(View v) {
            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
            context.startActivity(intent);
        }
    }
}
