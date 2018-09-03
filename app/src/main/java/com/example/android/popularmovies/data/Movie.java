package com.example.android.popularmovies.data;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.Ignore;
import android.arch.persistence.room.Index;
import android.arch.persistence.room.PrimaryKey;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

@Entity(tableName = "current_movie", indices = {@Index(value = {"movie_id"}, unique = true)})
public class Movie {

    @PrimaryKey(autoGenerate = true)
    private int _id;
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

    public Movie(int id, String title, String poster, int movieId) {
        this._id = id;
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
        return _id;
    }

    public int getMovieId() {
        return movieId;
    }
}
