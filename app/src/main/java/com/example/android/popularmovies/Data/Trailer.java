package com.example.android.popularmovies.Data;

public class Trailer {

    private String videoKey;
    private String title;
    private String type;

    public Trailer(String videoKey, String title, String type) {
        this.videoKey = videoKey;
        this.title = title;
        this.type = type;
    }

    public String getVideoKey() {
        return videoKey;
    }

    public String getTitle() {
        return title;
    }

    public String getType() {
        return type;
    }
}
