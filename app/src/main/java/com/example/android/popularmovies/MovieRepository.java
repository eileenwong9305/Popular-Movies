package com.example.android.popularmovies;

import android.app.Application;
import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MediatorLiveData;
import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.content.res.ColorStateList;
import android.support.annotation.Nullable;
import android.util.Log;

import com.example.android.popularmovies.Data.Movie;
import com.example.android.popularmovies.Database.FavouriteDao;
import com.example.android.popularmovies.Utils.AppExecutor;
import com.example.android.popularmovies.Utils.InjectorUtils;
import com.example.android.popularmovies.Utils.MovieNetworkDataSource;

import java.util.List;

public class MovieRepository {

    private static final Object LOCK = new Object();
    private static MovieRepository sInstance;
    private FavouriteDao favouriteDao;
    private MovieNetworkDataSource movieNetworkDataSource;
    private AppExecutor appExecutor;
    private LiveData<List<Movie>> selectedMovieList;

    private static final String FAVOURITE_DATABASE_VALUE = "favourites";

    private MovieRepository(FavouriteDao dao, MovieNetworkDataSource networkDataSource, AppExecutor executor){
        favouriteDao = dao;
        movieNetworkDataSource = networkDataSource;
        appExecutor = executor;
    }

    public synchronized static MovieRepository getInstance(FavouriteDao dao,
                                                           MovieNetworkDataSource networkDataSource,
                                                           AppExecutor executor) {
        if (sInstance == null) {
            synchronized (LOCK) {
                sInstance = new MovieRepository(dao, networkDataSource, executor);
            }
        }
        return sInstance;
    }

    public LiveData<List<Movie>> getMovieData(String sortOrder) {
        if (sortOrder.equals(FAVOURITE_DATABASE_VALUE)) {
            selectedMovieList = favouriteDao.loadAllFavourites();
        } else {
            movieNetworkDataSource.fetchMovie(sortOrder);
            selectedMovieList = movieNetworkDataSource.getMovieData();
        }
        return selectedMovieList;
    }

    public LiveData<List<Movie>> getOtherMovieData(String sortOrder) {
        movieNetworkDataSource.fetchMovie(sortOrder);
        return movieNetworkDataSource.getMovieData();
    }

    public LiveData<List<Movie>> getFavouriteMovieData() {
        return favouriteDao.loadAllFavourites();
    }

    public boolean containMovieId(int movieId){
        return favouriteDao.getCountByMovieId(movieId) > 0;
    }

    public void insertFavourite(final Movie movie) {
        appExecutor.diskIO().execute(new Runnable() {
            @Override
            public void run() {
                favouriteDao.insertFavourite(movie);
            }
        });
    }

    public void deleteSingleMovie(final int movieId) {
        appExecutor.diskIO().execute(new Runnable() {
            @Override
            public void run() {
                favouriteDao.deleteSingleMovie(movieId);
            }
        });
    }


}
