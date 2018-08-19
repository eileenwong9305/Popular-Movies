package com.example.android.popularmovies;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.ViewModel;
import android.util.Log;

import com.example.android.popularmovies.Data.FavouriteMovie;
import com.example.android.popularmovies.Data.Movie;

import java.util.List;

public class DetailViewModel extends ViewModel {

    private MovieRepository repository;

    public DetailViewModel(MovieRepository repository) {
        this.repository = repository;
    }

    public boolean containMovieId(int movieId) {
        return repository.containMovieId(movieId);
    }
    public void insertFavourite(FavouriteMovie movie) {
        repository.insertFavourite(movie);
    }

    public void deleteSingleMovie(final int movieId) {
        repository.deleteSingleMovie(movieId);
    }

    public FavouriteMovie getMovieDetail(int movieId) {
        return repository.getMovieDetails(movieId);
    }
}
