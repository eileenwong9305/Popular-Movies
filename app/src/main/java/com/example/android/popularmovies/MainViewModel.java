package com.example.android.popularmovies;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.ViewModel;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.util.Log;

import com.example.android.popularmovies.Data.Movie;
import com.example.android.popularmovies.Database.FavouriteDatabase;
import com.example.android.popularmovies.Utils.MovieNetworkDataSource;

import java.util.List;

public class MainViewModel extends AndroidViewModel {

    private MovieRepository repository;
    private LiveData<List<Movie>> movies;
    private SharedPreferences sharedPreferences;
    private Application application;

    public MainViewModel(Application application, MovieRepository repository) {
        super(application);
        this.application = application;
        this.repository = repository;
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(application);
        String sortOrder = sharedPreferences.getString(
                application.getString(R.string.pref_sort_key),
                application.getString(R.string.pref_sort_popular));
        movies = repository.getMovieData(sortOrder);
    }

    public LiveData<List<Movie>> getMovies() {
        String sortOrder = sharedPreferences.getString(
                application.getString(R.string.pref_sort_key),
                application.getString(R.string.pref_sort_popular));
        movies = repository.getMovieData(sortOrder);
        Log.e("ViewModel", movies.toString());
        return movies;
    }

    public LiveData<List<Movie>> getMovies(String sortOrder) {
        movies = repository.getMovieData(sortOrder);
        Log.e("ViewModel", movies.toString());
        return movies;
    }
}
