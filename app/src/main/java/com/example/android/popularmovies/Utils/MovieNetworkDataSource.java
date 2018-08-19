package com.example.android.popularmovies.Utils;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.util.Log;

import com.example.android.popularmovies.Data.FavouriteMovie;
import com.example.android.popularmovies.Data.Movie;
import com.example.android.popularmovies.Data.MovieList;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class MovieNetworkDataSource {

    private static final Object LOCK = new Object();
    private static MovieNetworkDataSource sInstance;
    private final AppExecutor appExecutor;

    private final MutableLiveData<List<Movie>> downloadedMovieData;
    private final MutableLiveData<FavouriteMovie> movieDetail;

    private MovieNetworkDataSource (AppExecutor appExecutor) {
        this.appExecutor = appExecutor;
        downloadedMovieData = new MutableLiveData<>();
        movieDetail = new MutableLiveData<>();
    }

    public static MovieNetworkDataSource getInstance(AppExecutor appExecutor) {
        if(sInstance == null) {
            synchronized (LOCK) {
                sInstance = new MovieNetworkDataSource(appExecutor);
            }
        }
        return sInstance;
    }

    public void fetchMovie(final String sortOrder) {
        appExecutor.networkIO().execute(new Runnable() {
            @Override
            public void run() {
                if (NetworkUtils.isOnline()) {
                    try {
                        URL queryUrl = NetworkUtils.buildUrl(sortOrder);
                        String json = NetworkUtils.getResponseFromHttp(queryUrl);
                        List<Movie> movies = NetworkUtils.parseMovieJson(json);
                        if (movies != null) {
                            downloadedMovieData.postValue(movies);
                        }
                    } catch (Exception ex) {
                        ex.printStackTrace();
                    }
                } else {
                    downloadedMovieData.postValue(null);
                }
            }
        });
    }

    public void fetchMovieDetail(final int movieId) {
        appExecutor.networkIO().execute(new Runnable() {
            @Override
            public void run() {
                if (NetworkUtils.isOnline()) {
                    try {
                        URL queryUrl = NetworkUtils.buildUrl(movieId);
                        String json = NetworkUtils.getResponseFromHttp(queryUrl);
                        FavouriteMovie movie = NetworkUtils.parseMovieDetailJson(json);
                        if (movie != null) {
                            Log.e("GOTMOVIE", "YEAH");
                            movieDetail.postValue(movie);
                        }
                    } catch (Exception ex) {
                        ex.printStackTrace();
                    }
                } else {
                    movieDetail.postValue(null);
                }
            }
        });
    }

    public LiveData<List<Movie>> getMovieData() {
        return downloadedMovieData;
    }

    public LiveData<FavouriteMovie> getMovieDetail() {
        return movieDetail;
    }
}
