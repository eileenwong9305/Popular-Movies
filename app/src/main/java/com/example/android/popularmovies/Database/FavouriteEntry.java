package com.example.android.popularmovies.Database;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.Ignore;
import android.arch.persistence.room.PrimaryKey;

import com.example.android.popularmovies.Data.Review;

import java.util.ArrayList;

@Entity(tableName = "favourite")
public class FavouriteEntry {

    @PrimaryKey(autoGenerate = true)
    private int id;
    private String title;
    private String poster;
    private String overview;
    @ColumnInfo(name = "user_rating")
    private String userRating;
    @ColumnInfo(name = "release_date")
    private String releaseDate;
    private String backdrop;
    @ColumnInfo(name = "movie_id")
    private int movieId;
    private String runtime;

    @Ignore
    public FavouriteEntry(String title, String poster, String overview, String userRating,
                          String releaseDate, String backdrop, int movieId, String runtime) {
        this.title = title;
        this.poster = poster;
        this.overview = overview;
        this.userRating = userRating;
        this.releaseDate = releaseDate;
        this.backdrop = backdrop;
        this.movieId = movieId;
        this.runtime = runtime;
    }

    public FavouriteEntry(int id, String title, String poster, String overview, String userRating,
                          String releaseDate, String backdrop, int movieId, String runtime) {
        this.id = id;
        this.title = title;
        this.poster = poster;
        this.overview = overview;
        this.userRating = userRating;
        this.releaseDate = releaseDate;
        this.backdrop = backdrop;
        this.movieId = movieId;
        this.runtime = runtime;
    }

    public int getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public String getPoster() {
        return poster;
    }

    public String getOverview() {
        return overview;
    }

    public String getUserRating() {
        return userRating;
    }

    public String getReleaseDate() {
        return releaseDate;
    }

    public String getBackdrop() {
        return backdrop;
    }

    public int getMovieId() {
        return movieId;
    }

    public String getRuntime() {
        return runtime;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setPoster(String poster) {
        this.poster = poster;
    }

    public void setOverview(String overview) {
        this.overview = overview;
    }

    public void setUserRating(String userRating) {
        this.userRating = userRating;
    }

    public void setReleaseDate(String releaseDate) {
        this.releaseDate = releaseDate;
    }

    public void setBackdrop(String backdrop) {
        this.backdrop = backdrop;
    }

    public void setMovieId(int movieId) {
        this.movieId = movieId;
    }

    public void setRuntime(String runtime) {
        this.runtime = runtime;
    }
}
