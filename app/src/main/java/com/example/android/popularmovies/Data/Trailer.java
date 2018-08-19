package com.example.android.popularmovies.Data;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.Ignore;
import android.arch.persistence.room.PrimaryKey;

@Entity(tableName = "trailer")
public class Trailer {

    @PrimaryKey(autoGenerate = true)
    private int id;
    private String videoKey;
    private String title;
    private String type;
    @ColumnInfo(name = "movie_id")
    private int movieId;

    public Trailer(int id, String videoKey, String title, String type, int movieId) {
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

    public int getId() {
        return id;
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

    public int getMovieId() {
        return movieId;
    }
}
