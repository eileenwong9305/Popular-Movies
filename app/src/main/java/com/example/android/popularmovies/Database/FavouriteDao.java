package com.example.android.popularmovies.Database;

import android.arch.lifecycle.LiveData;
import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Update;

import com.example.android.popularmovies.Data.Movie;

import java.util.ArrayList;
import java.util.List;

@Dao
public interface FavouriteDao {

    @Query("SELECT * FROM movie ORDER BY id")
    LiveData<List<Movie>> loadAllFavourites();

    @Query("SELECT movie_id FROM movie ORDER BY id")
    List<Integer> loadAllFavouritesMovieId();

    @Insert
    void insertFavourite(Movie movieEntry);

    @Delete
    void deleteFavourite(Movie movieEntry);

    @Query("DELETE FROM movie WHERE movie_id = :movieId")
    void deleteSingleMovie(int movieId);

    @Query("SELECT * FROM movie WHERE movie_id = :movieId")
    Movie loadFavouriteByMovieId(int movieId);
}
