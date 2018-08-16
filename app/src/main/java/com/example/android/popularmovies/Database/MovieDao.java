package com.example.android.popularmovies.Database;

import android.arch.lifecycle.LiveData;
import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Update;

import com.example.android.popularmovies.Data.FavouriteMovie;
import com.example.android.popularmovies.Data.Movie;
import com.example.android.popularmovies.Data.MovieList;

import java.util.ArrayList;
import java.util.List;

@Dao
public interface MovieDao {

    @Query("SELECT id, title, poster, movie_id FROM favourite_movie ORDER BY id")
    LiveData<List<MovieList>> loadAllFavouriteMovies();

    @Query("SELECT id, title, poster, movie_id FROM current_movie ORDER BY id")
    LiveData<List<MovieList>> loadAllCurrentMovies();

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void bulkInsert(List<Movie> movies);

    @Query("DELETE from current_movie")
    void deleteOldData();

//    @Query("SELECT movie_id FROM favourite_movie ORDER BY id")
//    List<Integer> loadAllFavouritesMovieId();

    @Insert
    void insertFavourite(FavouriteMovie movie);

//    @Delete
//    void deleteFavourite(Movie movie);

    @Query("DELETE FROM favourite_movie WHERE movie_id = :movieId")
    void deleteSingleMovie(int movieId);

    @Query("SELECT * FROM favourite_movie WHERE movie_id = :movieId")
    Movie loadFavouriteByMovieId(int movieId);

    @Query("SELECT COUNT(id) FROM favourite_movie WHERE movie_id = :movieId")
    int getCountByMovieId(int movieId);
}
