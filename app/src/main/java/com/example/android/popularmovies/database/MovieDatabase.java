package com.example.android.popularmovies.database;

import android.arch.persistence.room.Database;
import android.arch.persistence.room.RoomDatabase;
import android.arch.persistence.room.TypeConverters;

import com.example.android.popularmovies.data.FavouriteMovie;
import com.example.android.popularmovies.data.Movie;
import com.example.android.popularmovies.data.Review;
import com.example.android.popularmovies.data.Trailer;

@Database(entities = {FavouriteMovie.class, Movie.class, Review.class, Trailer.class}, version = 1, exportSchema = false)
@TypeConverters(GenreConverter.class)
public abstract class MovieDatabase extends RoomDatabase {

    public static final String DATABASE_NAME = "movies";

    public abstract MovieDao movieDao();
}
