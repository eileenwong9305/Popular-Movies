package com.example.android.popularmovies.Utils;

import android.app.Application;
import android.content.Context;

import com.example.android.popularmovies.Database.FavouriteDatabase;
import com.example.android.popularmovies.DetailActivity;
import com.example.android.popularmovies.MainViewModel;
import com.example.android.popularmovies.MainViewModelFactory;
import com.example.android.popularmovies.MovieRepository;

public class InjectorUtils {

    public static MovieRepository provideRepository(Context context) {
        FavouriteDatabase database = FavouriteDatabase.getInstance(context);
        AppExecutor appExecutor = AppExecutor.getInstance();
        MovieNetworkDataSource networkDataSource = MovieNetworkDataSource.getInstance(appExecutor);
        return MovieRepository.getInstance(database.favouriteDao(), networkDataSource, appExecutor);
    }

    public static MovieNetworkDataSource provideNetworkDataSource(){
        AppExecutor appExecutor = AppExecutor.getInstance();
        return  MovieNetworkDataSource.getInstance(appExecutor);
    }

    public static MainViewModelFactory provideMainViewModelFactory(Context context) {
        MovieRepository repository = provideRepository(context.getApplicationContext());
        return new MainViewModelFactory(repository);

    }
}
