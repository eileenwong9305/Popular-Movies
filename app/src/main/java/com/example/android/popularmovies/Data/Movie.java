package com.example.android.popularmovies.Data;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.Ignore;
import android.arch.persistence.room.Index;
import android.arch.persistence.room.PrimaryKey;

@Entity(tableName = "current_movie", indices = {@Index(value = {"movie_id"}, unique = true)})
public class Movie {

    @PrimaryKey(autoGenerate = true)
    private int id;
    private String title;
    private String poster;
    @ColumnInfo(name = "movie_id")
    private int movieId;
    private String sort;
    @ColumnInfo(name = "updated_at")
    private long updatedAt;

    public Movie(int id, String title, String poster, int movieId, String sort, long updatedAt) {
        this.id = id;
        this.title = title;
        this.poster = poster;
        this.movieId = movieId;
        this.sort = sort;
        this.updatedAt = updatedAt;
    }

    @Ignore
    public Movie(String title, String poster, int movieId) {
        this.title = title;
        this.poster = poster;
        this.movieId = movieId;
    }

    @Ignore
    public Movie(String title, String poster, int movieId, String sort, long updatedAt) {
        this.title = title;
        this.poster = poster;
        this.movieId = movieId;
        this.sort = sort;
        this.updatedAt = updatedAt;
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

    public long getUpdatedAt() {
        return updatedAt;
    }

    public String getSort() {
        return sort;
    }

    public void setSort(String sort) {
        this.sort = sort;
    }

    public void setUpdatedAt(long updatedAt) {
        this.updatedAt = updatedAt;
    }
}
