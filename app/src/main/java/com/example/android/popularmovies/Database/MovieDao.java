package com.example.android.popularmovies.Database;

import android.arch.lifecycle.LiveData;
import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;

import com.example.android.popularmovies.Data.FavouriteMovie;
import com.example.android.popularmovies.Data.Movie;
import com.example.android.popularmovies.Data.MovieList;
import com.example.android.popularmovies.Data.Review;
import com.example.android.popularmovies.Data.Trailer;

import java.util.List;

@Dao
public interface MovieDao {

    @Query("SELECT title, poster, movie_id FROM favourite_movie ORDER BY id")
    LiveData<List<MovieList>> loadAllFavouriteMovies();

    @Query("SELECT title, poster, movie_id FROM current_movie ORDER BY id")
    LiveData<List<MovieList>> loadAllCurrentMovies();

    @Query("SELECT title, poster, movie_id FROM favourite_movie ORDER BY id")
    List<MovieList> loadAllFavouriteMoviesN();

    @Query("SELECT title, poster, movie_id FROM current_movie ORDER BY id")
    List<MovieList> loadAllCurrentMoviesN();

    @Query("SELECT COUNT(*) FROM current_movie WHERE sort = :sort AND updated_at < :updatedAt")
    int hasMovie(String sort, long updatedAt);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void bulkInsert(List<Movie> movies);

    @Query("DELETE from current_movie")
    void deleteOldData();

    @Insert
    void insertFavourite(FavouriteMovie movie);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertReview(List<Review> reviews);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertTrailer(List<Trailer> trailers);

    @Query("DELETE FROM favourite_movie WHERE movie_id = :movieId")
    void deleteSingleMovie(int movieId);

    @Query("SELECT * FROM favourite_movie WHERE movie_id = :movieId")
    FavouriteMovie loadFavouriteByMovieId(int movieId);

    @Query("SELECT * FROM trailer WHERE movie_id = :movieId")
    List<Trailer> loadTrailerByMovieId(int movieId);

    @Query("SELECT * FROM review WHERE movie_id = :movieId")
    List<Review> loadReviewByMovieId(int movieId);

    @Query("SELECT COUNT(id) FROM favourite_movie WHERE movie_id = :movieId")
    int getCountByMovieId(int movieId);
}
