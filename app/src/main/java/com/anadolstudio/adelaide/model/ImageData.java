package com.anadolstudio.adelaide.model;

import androidx.annotation.NonNull;

public class ImageData {

    private String path;

    public ImageData(String path) {
        this.path = path;
    }

    public String getPath() {
        return path;
    }

    @NonNull
    @Override
    public String toString() {
        return "ImageData{" +
                "path='" + path + '\'' +
                '}';
    }
}
