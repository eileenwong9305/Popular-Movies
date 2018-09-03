package com.example.android.popularmovies.data;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.Ignore;
import android.arch.persistence.room.Index;
import android.arch.persistence.room.PrimaryKey;
import android.arch.persistence.room.TypeConverters;

import com.example.android.popularmovies.database.GenreConverter;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

@Entity(tableName = "favourite_movie", indices = {@Index(value = {"movie_id"}, unique = true)})
public class FavouriteMovie {
    @PrimaryKey(autoGenerate = true)
    private int _id;
    @SerializedName("title")
    @Expose
    private String title;
    @SerializedName("poster_path")
    @Expose
    private String poster;
    @SerializedName("overview")
    @Expose
    private String overview;
    @SerializedName("vote_average")
    @Expose
    @ColumnInfo(name = "user_rating")
    private String userRating;
    @SerializedName("release_date")
    @Expose
    @ColumnInfo(name = "release_date")
    private String releaseDate;
    @SerializedName("backdrop_path")
    @Expose
    private String backdrop;
    @SerializedName("id")
    @Expose
    @ColumnInfo(name = "movie_id")
    private int movieId;
    @SerializedName("genres")
    @Expose
    @TypeConverters(GenreConverter.class)
    private List<Genre> genres;
    @SerializedName("runtime")
    @Expose
    private String runtime;
    @SerializedName("original_language")
    @Expose
    private String language;


    public FavouriteMovie(int id, String title, String poster, String overview, String userRating,
                          String releaseDate, String backdrop, int movieId, List<Genre> genres, String runtime, String language) {
        this._id = id;
        this.title = title;
        this.poster = poster;
        this.overview = overview;
        this.userRating = userRating;
        this.releaseDate = releaseDate;
        this.backdrop = backdrop;
        this.movieId = movieId;
        this.genres = genres;
        this.runtime = runtime;
        this.language = language;
    }

    @Ignore
    public FavouriteMovie(String title, String poster, String overview, String userRating,
                          String releaseDate, String backdrop, int movieId, List<Genre> genres,
                          String runtime, String language) {
        this.title = title;
        this.poster = poster;
        this.overview = overview;
        this.userRating = userRating;
        this.releaseDate = releaseDate;
        this.backdrop = backdrop;
        this.movieId = movieId;
        this.genres = genres;
        this.runtime = runtime;
        this.language = language;
    }

    public int getId() {
        return _id;
    }

    public void setId(int id) {
        this._id = id;
    }

    public String getOverview() {
        return overview;
    }

    public void setOverview(String overview) {
        this.overview = overview;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getPoster() {
        return poster;
    }

    public void setPoster(String poster) {
        this.poster = poster;
    }

    public String getUserRating() {
        return userRating;
    }

    public void setUserRating(String userRating) {
        this.userRating = userRating;
    }

    public String getReleaseDate() {
        return releaseDate;
    }

    public void setReleaseDate(String releaseDate) {
        this.releaseDate = releaseDate;
    }

    public String getBackdrop() {
        return backdrop;
    }

    public void setBackdrop(String backdrop) {
        this.backdrop = backdrop;
    }

    public int getMovieId() {
        return movieId;
    }

    public void setMovieId(int movieId) {
        this.movieId = movieId;
    }

    public List<Genre> getGenres() {
        return genres;
    }

    public void setGenres(ArrayList<Genre> genres) {
        this.genres = genres;
    }

    public void setGenres(List<Genre> genres) {
        this.genres = genres;
    }

    public String getRuntime() {
        return runtime;
    }

    public void setRuntime(String runtime) {
        this.runtime = runtime;
    }

    public String getLanguage() {
        return language;
    }

    public void setLanguage(String language) {
        this.language = language;
    }
}
