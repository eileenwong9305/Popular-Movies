package com.example.android.popularmovies.ui.detail;

import android.arch.core.util.Function;
import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.Transformations;
import android.arch.lifecycle.ViewModel;

import com.example.android.popularmovies.data.FavouriteMovie;
import com.example.android.popularmovies.data.Resource;
import com.example.android.popularmovies.data.Review;
import com.example.android.popularmovies.data.Trailer;
import com.example.android.popularmovies.repository.MovieRepository;
import com.example.android.popularmovies.utils.AbsentLiveData;
import com.example.android.popularmovies.utils.Objects;

import java.util.List;

import javax.inject.Inject;

public class DetailViewModel extends ViewModel {

    final MutableLiveData<Integer> movieId = new MutableLiveData<>();
    private final MovieRepository repository;
    private final LiveData<Resource<FavouriteMovie>> movieDetail;
    private final LiveData<Resource<List<Review>>> movieReview;
    private final LiveData<Resource<List<Trailer>>> movieVideo;

    @Inject
    public DetailViewModel(final MovieRepository repository) {
        this.repository = repository;

        movieDetail = Transformations.switchMap(movieId, new Function<Integer, LiveData<Resource<FavouriteMovie>>>() {
            @Override
            public LiveData<Resource<FavouriteMovie>> apply(Integer input) {
                if (input == null) {
                    return AbsentLiveData.create();
                } else {
                    return repository.loadMovieDetail(input);
                }
            }
        });
        movieReview = Transformations.switchMap(movieId, new Function<Integer, LiveData<Resource<List<Review>>>>() {
            @Override
            public LiveData<Resource<List<Review>>> apply(Integer input) {
                if (input == null) {
                    return AbsentLiveData.create();
                } else {
                    return repository.loadMovieReview(input);
                }
            }
        });

        movieVideo = Transformations.switchMap(movieId, new Function<Integer, LiveData<Resource<List<Trailer>>>>() {
            @Override
            public LiveData<Resource<List<Trailer>>> apply(Integer input) {
                if (input == null) {
                    return AbsentLiveData.create();
                } else {
                    return repository.loadMovieVideo(input);
                }
            }
        });
    }

    public void setMovieId(int movieId) {
        if (Objects.equals(this.movieId.getValue(), movieId)) {
            return;
        }
        this.movieId.setValue(movieId);
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

    public LiveData<Resource<FavouriteMovie>> getMovieDetail() {
        return movieDetail;
    }

    public LiveData<Resource<List<Trailer>>> getMovieVideo() {
        return movieVideo;
    }

    public LiveData<Resource<List<Review>>> getMovieReview() {
        return movieReview;
    }
}
