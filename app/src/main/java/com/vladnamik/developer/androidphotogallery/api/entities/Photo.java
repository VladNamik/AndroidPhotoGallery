package com.vladnamik.developer.androidphotogallery.api.entities;


import com.vladnamik.developer.androidphotogallery.api.px500.ImageAPI;

/**
 * One photo from api.500px.com
 */
@SuppressWarnings("unused")
public class Photo {
    private String name;
    private String image_url;
    private String image_format;
    private String url;

    public Photo(String name, String image_url, String image_format, String url) {
        this.name = name;
        this.image_url = image_url;
        this.image_format = image_format;
        this.url = url;
    }

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
