package com.example.android.popularmovies.ui.detail;

import android.arch.lifecycle.ViewModel;
import android.arch.lifecycle.ViewModelProvider;
import android.support.annotation.NonNull;

import com.example.android.popularmovies.MovieRepository;

public class DetailViewModelFactory extends ViewModelProvider.NewInstanceFactory {

    private MovieRepository repository;

    public DetailViewModelFactory(MovieRepository repository) {
        this.repository = repository;
    }

    @NonNull
    @Override
    public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
        return (T) new DetailViewModel(repository);
    }
}
