package com.example.android.popularmovies.Utils;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.util.Log;

import com.example.android.popularmovies.Data.FavouriteMovie;
import com.example.android.popularmovies.Data.Movie;

import java.net.URL;
import java.util.List;

public class MovieNetworkDataSource {

    private static final Object LOCK = new Object();
    private static MovieNetworkDataSource sInstance;
    private final AppExecutor appExecutor;

    private final MutableLiveData<List<Movie>> downloadedMovieData;

    private FavouriteMovie movieDetails;

    private MovieNetworkDataSource(AppExecutor appExecutor) {
        this.appExecutor = appExecutor;
        downloadedMovieData = new MutableLiveData<>();
    }

    public static MovieNetworkDataSource getInstance(AppExecutor appExecutor) {
        if (sInstance == null) {
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
                        Log.e("NetworkDataSource", "fetch Movie");
                        URL queryUrl = NetworkUtils.buildUrl(sortOrder);
                        String json = NetworkUtils.getResponseFromHttp(queryUrl);
                        List<Movie> movies = NetworkUtils.parseMovieJson(json);
                        if (movies != null) {
                            Log.e("NetworkDataSource", "post Movie");
                            downloadedMovieData.postValue(movies);
                        }
                    } catch (Exception ex) {
                        ex.printStackTrace();
                    }
                } else {
                    Log.e("NetworkDataSource", "null");
                    downloadedMovieData.postValue(null);
                }
            }
        });
    }

    public LiveData<List<Movie>> getMovieData() {
        return downloadedMovieData;
    }
}
