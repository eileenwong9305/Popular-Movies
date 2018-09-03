package com.example.android.popularmovies.data;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.Ignore;
import android.arch.persistence.room.PrimaryKey;
import android.support.annotation.NonNull;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

@Entity(tableName = "trailer")
public class Trailer {
    @PrimaryKey
    @NonNull
    @SerializedName("id")
    @Expose
    private String id;
    @SerializedName("key")
    @Expose
    private String videoKey;
    @SerializedName("name")
    @Expose
    private String title;
    @SerializedName("type")
    @Expose
    private String type;
    @ColumnInfo(name = "movie_id")
    private int movieId;

    public Trailer(String id, String videoKey, String title, String type, int movieId) {
        this.id = id;
        this.videoKey = videoKey;
        this.title = title;
        this.type = type;
        this.movieId = movieId;
    }

    @Ignore
    public Trailer(String videoKey, String title, String type, int movieId) {
        this.videoKey = videoKey;
        this.title = title;
        this.type = type;
        this.movieId = movieId;
    }

    public String getId() {
        return this.id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getVideoKey() {
        return videoKey;
    }

    public void setVideoKey(String videoKey) {
        this.videoKey = videoKey;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public int getMovieId() {
        return movieId;
    }

    public void setMovieId(int movieId) {
        this.movieId = movieId;
    }
}
