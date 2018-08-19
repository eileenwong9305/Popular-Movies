package com.example.android.popularmovies.ui.main;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.ViewModel;

import com.example.android.popularmovies.Data.Movie;
import com.example.android.popularmovies.MovieRepository;

import java.util.List;

public class MainViewModel extends ViewModel {

    private MovieRepository repository;

    public MainViewModel(MovieRepository repository) {
        this.repository = repository;
    }

    public LiveData<List<Movie>> getOtherMovieData(String sortOrder) {
        return repository.getOtherMovieData(sortOrder);
    }

    public LiveData<List<Movie>> getFavouriteMovieData() {
        return repository.getFavouriteMovieData();
    }
}
