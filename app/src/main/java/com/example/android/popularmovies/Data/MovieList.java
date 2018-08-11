package com.example.android.popularmovies.Data;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Ignore;

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

    @Ignore
    public MovieList(String title, String poster, int movieId) {
        this.title = title;
        this.poster = poster;
        this.movieId = movieId;
    }

    public String getTitle() {
        return title;
    }

    public String getPoster() {
        return poster;
    }

    public int getId() {
        return id;
    }

    public int getMovieId() {
        return movieId;
    }
}
