package com.example.android.popularmovies;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.ViewModel;
import android.util.Log;

import com.example.android.popularmovies.Data.FavouriteMovie;
import com.example.android.popularmovies.Data.Movie;
import com.example.android.popularmovies.Data.Review;
import com.example.android.popularmovies.Data.Trailer;

import java.util.List;

public class DetailViewModel extends ViewModel {

    private MovieRepository repository;

    public DetailViewModel(MovieRepository repository) {
        this.repository = repository;
    }

    public boolean containMovieId(int movieId) {
        return repository.containMovieId(movieId);
    }
    public void insertFavourite(FavouriteMovie movie, List<Review> reviews, List<Trailer> trailers) {
        repository.insertFavourite(movie, reviews, trailers);
    }

    public void deleteSingleMovie(final int movieId) {
        repository.deleteSingleMovie(movieId);
    }

    public LiveData<FavouriteMovie> getMovieDetail(int movieId) {
        return repository.getMovieDetail(movieId);
    }

    public LiveData<List<Trailer>> getMovieVideo() {
        return repository.getMovieVideo();
    }

    public LiveData<List<Review>> getMovieReview() {
        return repository.getMovieReview();
    }
}
