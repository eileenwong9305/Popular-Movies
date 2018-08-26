package com.example.android.popularmovies.Data;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Ignore;
import android.arch.persistence.room.PrimaryKey;

public class MovieList {

    private int id;
    private String title;
    private String poster;
    @ColumnInfo(name = "movie_id")
    private int movieId;

    public MovieList(int id, String title, String poster, int movieId) {
        this.id = id;
        this.title = title;
        this.poster = poster;
        this.movieId = movieId;
    }

    public String getTitle() {
        return title;
    }

    public int getId() {
        return id;
    }

    public int getMovieId() {
        return movieId;
    }

    public String getPoster() {
        return poster;
    }
}
