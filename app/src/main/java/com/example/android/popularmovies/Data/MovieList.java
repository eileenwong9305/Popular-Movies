package com.example.android.popularmovies.Data;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Ignore;
import android.arch.persistence.room.PrimaryKey;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class MovieList {

    @SerializedName("title")
    @Expose
    private String title;
    @SerializedName("poster_path")
    @Expose
    private String poster;
    @SerializedName("id")
    @Expose
    @ColumnInfo(name = "movie_id")
    private int movieId;

    public MovieList(String title, String poster, int movieId) {
        this.title = title;
        this.poster = poster;
        this.movieId = movieId;
    }

    public String getTitle() {
        return title;
    }

    public int getMovieId() {
        return movieId;
    }

    public String getPoster() {
        return poster;
    }
}
