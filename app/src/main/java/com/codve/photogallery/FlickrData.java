package com.codve.photogallery;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class FlickrData {

    @SerializedName("photo")
    private List<GalleryItem> photos;

    public FlickrData(List<GalleryItem> photo) {
        this.photos = photo;
    }

    public List<GalleryItem> getPhoto() {
        return photos;
    }
}
