package com.example.android.popularmovies.Utils;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.util.Log;

import com.example.android.popularmovies.Data.Movie;
import com.example.android.popularmovies.Data.MovieList;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class MovieNetworkDataSource {

    private static final Object LOCK = new Object();
    private static MovieNetworkDataSource sInstance;
    private final AppExecutor appExecutor;

    private final MutableLiveData<List<MovieList>> downloadedMovieData;

    private MovieNetworkDataSource (AppExecutor appExecutor) {
        this.appExecutor = appExecutor;
        downloadedMovieData = new MutableLiveData<>();
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
                        List<MovieList> movies = NetworkUtils.parseMovieJson(json);
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

    public LiveData<List<MovieList>> getMovieData() {
        return downloadedMovieData;
    }

    public MutableLiveData<List<MovieList>> getDownloadedMovieData() {
        return downloadedMovieData;
    }
}
