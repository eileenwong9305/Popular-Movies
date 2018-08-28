package com.example.android.popularmovies.Utils;

import android.content.Context;

import com.example.android.popularmovies.ApiClient;
import com.example.android.popularmovies.Database.MovieDatabase;
import com.example.android.popularmovies.ApiInterface;
import com.example.android.popularmovies.MovieRepository;
import com.example.android.popularmovies.ui.main.MainViewModelFactory;

import javax.inject.Inject;

public class InjectorUtils {

//    public static MovieRepository provideRepository(Context context) {
//        MovieDatabase database = MovieDatabase.getInstance(context);
//        AppExecutor appExecutor = AppExecutor.getInstance();
//        MovieNetworkDataSource networkDataSource = MovieNetworkDataSource.getInstance(appExecutor);
//        ApiInterface apiInterface = provideMovieDbService();
//        return MovieRepository.getInstance(apiInterface, database.movieDao(), networkDataSource, appExecutor);
//    }
//
//    @Inject
//    public static MainViewModelFactory provideMainViewModelFactory(MovieRepository movieRepository) {
//        return new MainViewModelFactory(movieRepository);
//
//    }
//
//    public static ApiInterface provideMovieDbService() {
//        ApiInterface apiInterface = ApiClient.getMovieDBServiceInstance();
//        return apiInterface;
////    }
}
