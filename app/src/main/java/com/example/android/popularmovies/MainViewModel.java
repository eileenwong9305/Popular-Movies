package com.example.android.popularmovies;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LiveData;
import android.support.annotation.NonNull;

import com.example.android.popularmovies.Data.Movie;
import com.example.android.popularmovies.Database.FavouriteDatabase;

import java.util.List;

public class MainViewModel extends AndroidViewModel{

    private LiveData<List<Movie>> movies;
    public MainViewModel(@NonNull Application application) {
        super(application);
        FavouriteDatabase mDb = FavouriteDatabase.getInstance(this.getApplication());
        movies = mDb.favouriteDao().loadAllFavourites();
    }

    public LiveData<List<Movie>> getMovies() {
        return movies;
    }
}
