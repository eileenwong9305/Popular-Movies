package com.example.android.popularmovies.Data;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.Ignore;
import android.arch.persistence.room.PrimaryKey;

@Entity(tableName = "review")
public class Review {

    @PrimaryKey(autoGenerate = true)
    private int id;
    private String author;
    private String content;
    @ColumnInfo(name = "movie_id")
    private int movieId;

    public Review(int id, String author, String content, int movieId) {
        this.id = id;
        this.author = author;
        this.content = content;
        this.movieId = movieId;
    }

    @Ignore
    public Review(String author, String content, int movieId) {
        this.author = author;
        this.content = content;
        this.movieId = movieId;
    }

    public int getId() {
        return id;
    }

    public String getAuthor() {
        return author;
    }

    public String getContent() {
        return content;
    }

    public int getMovieId() {
        return movieId;
    }
}
