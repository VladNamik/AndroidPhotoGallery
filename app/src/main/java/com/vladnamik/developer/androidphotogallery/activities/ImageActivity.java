package com.vladnamik.developer.androidphotogallery.activities;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;
import com.vladnamik.developer.androidphotogallery.R;

public class ImageActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image);

        imageInit();
    }

    private void imageInit() {

        String imageURL = getIntent().getStringExtra("image_url");
        ImageView imageView = (ImageView) findViewById(R.id.full_image_view);
        Picasso.with(this).load(imageURL).into(imageView);
    }

}
