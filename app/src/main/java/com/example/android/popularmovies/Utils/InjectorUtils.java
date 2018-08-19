package com.example.android.popularmovies.Utils;

import android.content.Context;

import com.example.android.popularmovies.Database.FavouriteDatabase;
import com.example.android.popularmovies.MovieRepository;
import com.example.android.popularmovies.ui.main.MainViewModelFactory;

public class InjectorUtils {

    public static MovieRepository provideRepository(Context context) {
        FavouriteDatabase database = FavouriteDatabase.getInstance(context);
        AppExecutor appExecutor = AppExecutor.getInstance();
        MovieNetworkDataSource networkDataSource = MovieNetworkDataSource.getInstance(appExecutor);
        return MovieRepository.getInstance(database.favouriteDao(), networkDataSource, appExecutor);
    }

    public static MainViewModelFactory provideMainViewModelFactory(Context context) {
        MovieRepository repository = provideRepository(context.getApplicationContext());
        return new MainViewModelFactory(repository);

    }
}
