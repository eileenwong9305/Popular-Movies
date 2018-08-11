package com.example.android.popularmovies;

import android.app.Application;
import android.arch.lifecycle.ViewModel;
import android.arch.lifecycle.ViewModelProvider;
import android.support.annotation.NonNull;

import com.example.android.popularmovies.Database.FavouriteDatabase;

public class MainViewModelFactory extends ViewModelProvider.NewInstanceFactory {

    private MovieRepository repository;

    public MainViewModelFactory(MovieRepository repository) {
        this.repository = repository;
    }

    @NonNull
    @Override
    public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
        return (T) new  MainViewModel(repository);
    }
}
