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

public class MainViewModel extends ViewModel {

    private MovieRepository repository;

    public MainViewModel(MovieRepository repository) {
        this.repository = repository;
    }

    public LiveData<List<Movie>> getMovies(String sortOrder) {
        return repository.getMovieData(sortOrder);
    }

}
