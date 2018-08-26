package com.example.android.popularmovies.ui.main;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.ViewModel;
import android.util.Log;

import com.example.android.popularmovies.Data.Movie;
import com.example.android.popularmovies.Data.MovieList;
import com.example.android.popularmovies.MovieRepository;

import java.util.List;

public class MainViewModel extends ViewModel {

    private MovieRepository repository;
    private LiveData<List<MovieList>> movieList;

    public MainViewModel(MovieRepository repository) {
        this.repository = repository;
    }

    public void setPreference(String sortOrder) {
        movieList = repository.getMovieList(sortOrder);
    }

    public LiveData<List<MovieList>> getMovieList() {
        return movieList;
    }

    public LiveData<List<MovieList>> getMovieList(String sortOrder) {
        return repository.getMovieList(sortOrder);
    }

    public LiveData<List<MovieList>> getOtherMovieData(String sortOrder) {
        return repository.getOtherMovieData(sortOrder);
    }

    public LiveData<List<MovieList>> getFavouriteMovieData() {
        return repository.getFavouriteMovieData();
    }
}
