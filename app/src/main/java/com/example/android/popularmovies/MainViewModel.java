package com.example.android.popularmovies;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.ViewModel;

import com.example.android.popularmovies.Data.Movie;

import java.util.List;

public class MainViewModel extends ViewModel {

    private MovieRepository repository;

    public MainViewModel(MovieRepository repository) {
        this.repository = repository;
    }

//    public LiveData<List<MovieList>> getMovies(String sortOrder) {
////        return repository.getMovieData(sortOrder);
////    }

    public LiveData<List<Movie>> getOtherMovieData(String sortOrder) {
        return repository.getOtherMovieData(sortOrder);
    }

    public LiveData<List<Movie>> getFavouriteMovieData() {
        return repository.getFavouriteMovieData();
    }
}
