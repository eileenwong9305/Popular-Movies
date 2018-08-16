package com.example.android.popularmovies.Data;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.Ignore;
import android.arch.persistence.room.Index;
import android.arch.persistence.room.PrimaryKey;
import android.util.Log;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

@Entity(tableName = "current_movie", indices = {@Index(value = {"movie_id"}, unique = true)})
public class Movie {

    @PrimaryKey(autoGenerate = true)
    private int id;
    private String title;
    private String poster;
    @ColumnInfo(name = "movie_id")
    private int movieId;



    public Movie(int id, String title, String poster, int movieId) {
        this.id = id;
        this.title = title;
        this.poster = poster;
        this.movieId = movieId;
    }

    @Ignore
    public Movie(String title, String poster, int movieId) {
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
