package com.example.android.popularmovies.Data;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.Ignore;
import android.arch.persistence.room.Index;
import android.arch.persistence.room.PrimaryKey;
import android.arch.persistence.room.TypeConverters;
import android.util.Log;

import com.example.android.popularmovies.Database.GenreConverter;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

@Entity(tableName = "favourite_movie", indices = {@Index(value = {"movie_id"}, unique = true)})
public class FavouriteMovie {
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

    @TypeConverters(GenreConverter.class)
    private List<String> genres;
    private String runtime;
    private String language;
    @Ignore
    private ArrayList<Review> reviews;
    @Ignore
    private ArrayList<Trailer> trailers;


    public FavouriteMovie(int id, String title, String poster, String overview, String userRating,
                 String releaseDate, String backdrop, int movieId, List<String> genres, String runtime, String language) {
        this.id = id;
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
                          String releaseDate, String backdrop, int movieId, List<String> genres,
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

    @Ignore
    public FavouriteMovie(String title, String poster, String overview, String userRating,
                 String releaseDate, String backdrop, int movieId, List<String> genres, String runtime,
                 String language, ArrayList<Review> reviews, ArrayList<Trailer> trailers) {
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
        this.reviews = reviews;
        this.trailers = trailers;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setGenres(ArrayList<String> genres) {
        this.genres = genres;
    }

    public void setRuntime(String runtime) {
        this.runtime = runtime;
    }

    public void setReviews(ArrayList<Review> reviews) {
        this.reviews = reviews;
    }

    public void setTrailers(ArrayList<Trailer> trailers) {
        this.trailers = trailers;
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

    public void setMovieId(int movieId) {
        this.movieId = movieId;
    }

    public int getMovieId() {
        return movieId;
    }

    public List<String> getGenres() {
        return genres;
    }

    public String getRuntime() {
        return runtime;
    }

    public String getLanguage() {
        return language;
    }

    public ArrayList<Review> getReviews() {
        return reviews;
    }

    public ArrayList<Trailer> getTrailers() {
        return trailers;
    }

    /**
     * Change the date format
     *
     * @param dateString date in String type
     * @return Converted date string
     */
    public static String convertDateString(String dateString) {
        Log.e("DATE", dateString);
        Date date = null;
        try {
            date = new SimpleDateFormat("yyyy-MM-dd", Locale.US).parse(dateString);

        } catch (ParseException e) {
            e.printStackTrace();
        }
        DateFormat dateFormatter = DateFormat.getDateInstance(DateFormat.MEDIUM);
        return dateFormatter.format(date);
    }
}
