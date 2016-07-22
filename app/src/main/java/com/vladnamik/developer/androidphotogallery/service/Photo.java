package com.vladnamik.developer.androidphotogallery.service;


/**
 * One photo from api.500px.com
 */
public class Photo {
    private String name;
    private String image_url;
    private String image_format;

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

    public String getImageFormat() {
        return image_format;
    }

}
