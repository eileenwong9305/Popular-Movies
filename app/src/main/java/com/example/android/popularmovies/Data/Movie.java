package com.example.android.popularmovies.Data;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.Ignore;
import android.arch.persistence.room.Index;
import android.arch.persistence.room.PrimaryKey;
import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

@Entity(tableName = "movie", indices = {@Index(value = {"movie_id"}, unique = true)})
public class Movie implements Parcelable {

    public static final Parcelable.Creator<Movie> CREATOR = new Parcelable.Creator<Movie>() {
        @Override
        public Movie createFromParcel(Parcel source) {
            return new Movie(source);
        }

        @Override
        public Movie[] newArray(int size) {
            return new Movie[size];
        }
    };

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

    @Ignore
    private ArrayList<String> genres;
    @Ignore
    private String runtime;
    @Ignore
    private ArrayList<Review> reviews;
    @Ignore
    private ArrayList<String> videoKeys;


    public Movie(int id, String title, String poster, String overview, String userRating,
                 String releaseDate, String backdrop, int movieId) {
        this.id = id;
        this.title = title;
        this.poster = poster;
        this.overview = overview;
        this.userRating = userRating;
        this.releaseDate = releaseDate;
        this.backdrop = backdrop;
        this.movieId = movieId;
    }

    @Ignore
    public Movie(String title, String poster, String overview, String userRating,
                 String releaseDate, String backdrop, int movieId) {
        this.title = title;
        this.poster = poster;
        this.overview = overview;
        this.userRating = userRating;
        this.releaseDate = releaseDate;
        this.backdrop = backdrop;
        this.movieId = movieId;
    }

    @Ignore
    public Movie(ArrayList<String> genres, String runtime, ArrayList<Review> reviews, ArrayList<String> videoKeys) {
        this.genres = genres;
        this.runtime = runtime;
        this.reviews = reviews;
        this.videoKeys = videoKeys;
    }

    @Ignore
    public Movie(Parcel source) {
        this.title = source.readString();
        this.poster = source.readString();
        this.overview = source.readString();
        this.userRating = source.readString();
        this.releaseDate = source.readString();
        this.backdrop = source.readString();
        this.movieId = source.readInt();
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

    public void setVideoKeys(ArrayList<String> videoKeys) {
        this.videoKeys = videoKeys;
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

    public ArrayList<String> getGenres() {
        return genres;
    }

    public String getRuntime() {
        return runtime;
    }

    public ArrayList<Review> getReviews() {
        return reviews;
    }

    public ArrayList<String> getVideoKeys() {
        return videoKeys;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(title);
        dest.writeString(poster);
        dest.writeString(overview);
        dest.writeString(userRating);
        dest.writeString(releaseDate);
        dest.writeString(backdrop);
        dest.writeInt(movieId);
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
