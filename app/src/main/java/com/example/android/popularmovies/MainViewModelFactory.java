package com.example.android.popularmovies;

import android.app.Application;
import android.arch.lifecycle.ViewModel;
import android.arch.lifecycle.ViewModelProvider;
import android.support.annotation.NonNull;

import com.example.android.popularmovies.Database.FavouriteDatabase;

public class MainViewModelFactory extends ViewModelProvider.NewInstanceFactory {

    private MovieRepository repository;
    private Application application;

    public MainViewModelFactory(Application application, MovieRepository repository) {
        this.application = application;
        this.repository = repository;
    }

    @NonNull
    @Override
    public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
        return (T) new  MainViewModel(application, repository);
    }
}
