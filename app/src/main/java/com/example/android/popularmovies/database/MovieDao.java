package com.example.android.popularmovies.database;

import android.arch.lifecycle.LiveData;
import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;

import com.example.android.popularmovies.data.FavouriteMovie;
import com.example.android.popularmovies.data.Movie;
import com.example.android.popularmovies.data.Review;
import com.example.android.popularmovies.data.Trailer;

import java.util.List;

@Dao
public interface MovieDao {

    @Query("SELECT _id, title, poster, movie_id FROM favourite_movie ORDER BY _id")
    LiveData<List<Movie>> loadAllFavouriteMovies();

    @Query("SELECT _id, title, poster, movie_id FROM current_movie ORDER BY _id")
    LiveData<List<Movie>> loadAllCurrentMovies();

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void bulkInsert(List<Movie> movies);

    @Query("DELETE from current_movie")
    void deleteOldData();

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertFavourite(FavouriteMovie movie);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertReview(List<Review> reviews);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertTrailer(List<Trailer> trailers);

    @Query("DELETE FROM favourite_movie WHERE movie_id = :movieId")
    void deleteSingleMovie(int movieId);

    @Query("SELECT * FROM favourite_movie WHERE movie_id = :movieId")
    LiveData<FavouriteMovie> loadDetailByMovieId(int movieId);

    @Query("SELECT * FROM trailer WHERE movie_id = :movieId")
    LiveData<List<Trailer>> loadTrailerByMovieId(int movieId);

    @Query("SELECT * FROM review WHERE movie_id = :movieId")
    LiveData<List<Review>> loadReviewByMovieId(int movieId);

    @Query("SELECT COUNT(_id) FROM favourite_movie WHERE movie_id = :movieId")
    int getCountByMovieId(int movieId);
}
