package com.vladnamik.developer.androidphotogallery.api.entities;


import com.vladnamik.developer.androidphotogallery.api.ImageAPI;

/**
 * One photo from api.500px.com
 */
@SuppressWarnings("unused")
public class Photo {
    private String name;
    private String image_url;
    private String image_format;
    private String url;

    @Override
    public String toString() {
        return image_url;
    }

    public String getName() {
        return name;
    }

    public String getImageURL() {
        return image_url;
    }

    public String getURL() {
        return ImageAPI.SITE_URL + url;
    }

    public String getImageFormat() {
        return image_format;
    }

}
