package com.example.android.popularmovies.Utils;

import android.content.Context;

import com.example.android.popularmovies.ApiClient;
import com.example.android.popularmovies.Database.FavouriteDatabase;
import com.example.android.popularmovies.MovieDBService;
import com.example.android.popularmovies.MovieRepository;
import com.example.android.popularmovies.ui.main.MainViewModelFactory;

public class InjectorUtils {

    public static MovieRepository provideRepository(Context context) {
        FavouriteDatabase database = FavouriteDatabase.getInstance(context);
        AppExecutor appExecutor = AppExecutor.getInstance();
        MovieNetworkDataSource networkDataSource = MovieNetworkDataSource.getInstance(appExecutor);
        MovieDBService movieDBService = provideMovieDbService();
        return MovieRepository.getInstance(movieDBService, database.favouriteDao(), networkDataSource, appExecutor);
    }

    public static MainViewModelFactory provideMainViewModelFactory(Context context) {
        MovieRepository repository = provideRepository(context.getApplicationContext());
        return new MainViewModelFactory(repository);

    }

    public static MovieDBService provideMovieDbService() {
        MovieDBService movieDBService = ApiClient.getMovieDBServiceInstance();
        return movieDBService;
    }
}
