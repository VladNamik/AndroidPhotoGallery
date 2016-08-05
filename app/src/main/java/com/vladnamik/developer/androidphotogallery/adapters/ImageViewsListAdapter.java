package com.vladnamik.developer.androidphotogallery.adapters;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;
import com.vladnamik.developer.androidphotogallery.R;
import com.vladnamik.developer.androidphotogallery.activities.ImageActivity;
import com.vladnamik.developer.androidphotogallery.api.entities.Photo;

import java.util.List;


public class ImageViewsListAdapter extends ArrayAdapter<Photo> {
    private static final String ADAPTER_LOG_TAG = "ImageViewsListAdapter";
    private Context context;

    public ImageViewsListAdapter(Context context, List<Photo> photos) {
        super(context, R.layout.image_item, photos);
        this.context = context;
    }

    @NonNull
    @Override
    public View getView(int position, View convertView, @NonNull ViewGroup parent) {

        Log.d(ADAPTER_LOG_TAG, "trying to get view");

        if (convertView == null) {
            convertView = LayoutInflater.from(getContext())
                    .inflate(R.layout.image_item, null);
        }

        ImageView imageView = (ImageView) convertView.findViewById(R.id.image_item_image_view);
        Photo photo = getItem(position);

        if (photo == null) {
            return imageView;
        }

        Picasso.with(context).load(photo.getImageURL()).into(imageView);
        imageView.setOnClickListener(new OnImageClickListener(photo.getImageURL()));

        return convertView;
    }

    /**
     * Обработчик нажатия на картинку
     */
    private class OnImageClickListener implements View.OnClickListener {
        private String imageURL;

        private OnImageClickListener(String imageURL) {
            this.imageURL = imageURL;
        }

        @Override
        public void onClick(View v) {
            Intent intent = new Intent(v.getContext(), ImageActivity.class);
            intent.putExtra("image_url", imageURL);
            context.startActivity(intent);
        }
    }
}
