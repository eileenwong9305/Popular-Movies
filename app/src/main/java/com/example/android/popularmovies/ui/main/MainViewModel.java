package com.example.android.popularmovies.ui.main;

import android.arch.core.util.Function;
import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.Transformations;
import android.arch.lifecycle.ViewModel;

import com.example.android.popularmovies.data.Movie;
import com.example.android.popularmovies.data.Resource;
import com.example.android.popularmovies.repository.MovieRepository;
import com.example.android.popularmovies.utils.AbsentLiveData;
import com.example.android.popularmovies.utils.Objects;

import java.util.List;

import javax.inject.Inject;

public class MainViewModel extends ViewModel {

    final MutableLiveData<String> sortOrder = new MutableLiveData<>();
    private final LiveData<Resource<List<Movie>>> movieList;
    private MovieRepository repository;
    private LiveData<List<Movie>> favMovieList;

    @Inject
    public MainViewModel(final MovieRepository repository) {
        this.repository = repository;
        favMovieList = repository.loadFavMovie();
        movieList = Transformations.switchMap(sortOrder, new Function<String, LiveData<Resource<List<Movie>>>>() {
            @Override
            public LiveData<Resource<List<Movie>>> apply(String input) {
                if (input == null) {
                    return AbsentLiveData.create();
                } else {
                    return repository.loadMovie(input);
                }
            }
        });
    }

    public void setSortOrder(String sortOrder) {
        if (Objects.equals(this.sortOrder.getValue(), sortOrder)) {
            return;
        }
        this.sortOrder.setValue(sortOrder);
    }

    public LiveData<Resource<List<Movie>>> getMovieList() {
        return movieList;
    }

    public LiveData<List<Movie>> getFavMovieList() {
        return favMovieList;
    }

}
