package com.example.android.popularmovies;

import android.arch.lifecycle.LiveData;
import android.util.Log;

import com.example.android.popularmovies.Data.Movie;
import com.example.android.popularmovies.Data.MovieList;
import com.example.android.popularmovies.Database.MovieDao;
import com.example.android.popularmovies.Utils.AppExecutor;
import com.example.android.popularmovies.Utils.MovieNetworkDataSource;

import java.util.List;

public class MovieRepository {

    private static final Object LOCK = new Object();
    private static MovieRepository sInstance;
    private MovieDao movieDao;
    private MovieNetworkDataSource movieNetworkDataSource;
    private AppExecutor appExecutor;
    private LiveData<List<Movie>> selectedMovieList;

    private static final String FAVOURITE_DATABASE_VALUE = "favourites";

    private MovieRepository(MovieDao dao, MovieNetworkDataSource networkDataSource, AppExecutor executor){
        movieDao = dao;
        movieNetworkDataSource = networkDataSource;
        appExecutor = executor;
    }

    public synchronized static MovieRepository getInstance(MovieDao dao,
                                                           MovieNetworkDataSource networkDataSource,
                                                           AppExecutor executor) {
        if (sInstance == null) {
            synchronized (LOCK) {
                sInstance = new MovieRepository(dao, networkDataSource, executor);
            }
        }
        return sInstance;
    }

    public LiveData<List<MovieList>> getMovieData(String sortOrder) {
        if (sortOrder.equals(FAVOURITE_DATABASE_VALUE)) {
            return movieDao.loadAllFavouritesList();
        } else {
            movieNetworkDataSource.fetchMovie(sortOrder);
            return movieNetworkDataSource.getMovieData();
        }
    }

    public LiveData<List<MovieList>> getOtherMovieData(String sortOrder) {
        movieNetworkDataSource.fetchMovie(sortOrder);
        return movieNetworkDataSource.getMovieData();
    }

    public LiveData<List<MovieList>> getFavouriteMovieData() {
        return movieDao.loadAllFavouritesList();
    }

    public boolean containMovieId(int movieId){
        int value = movieDao.getCountByMovieId(movieId);
        Log.e("REPO", String.valueOf(value));
        return movieDao.getCountByMovieId(movieId) > 0;
    }

    public void insertFavourite(final Movie movie) {
        appExecutor.diskIO().execute(new Runnable() {
            @Override
            public void run() {
                movieDao.insertFavourite(movie);
            }
        });
    }

    public void deleteSingleMovie(final int movieId) {
        appExecutor.diskIO().execute(new Runnable() {
            @Override
            public void run() {
                movieDao.deleteSingleMovie(movieId);
            }
        });
    }


}
