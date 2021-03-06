package com.vladnamik.developer.androidphotogallery.api.entities;

import java.util.List;

/**
 * One page from api.500px.com
 */
@SuppressWarnings("unused")
public class Page {
    private List<Photo> photos;
    private int current_page;

    public List<Photo> getPhotos() {
        return photos;
    }

    public int getCurrentPageNumber() {
        return current_page;
    }
}
