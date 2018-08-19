package com.example.android.popularmovies;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.Observer;
import android.support.annotation.Nullable;

import com.example.android.popularmovies.Data.FavouriteMovie;
import com.example.android.popularmovies.Data.Movie;
import com.example.android.popularmovies.Database.MovieDao;
import com.example.android.popularmovies.Utils.AppExecutor;
import com.example.android.popularmovies.Utils.MovieNetworkDataSource;

import java.util.List;

public class MovieRepository {

    private static final Object LOCK = new Object();
    private static MovieRepository sInstance;
    private MovieDao movieDao;
    private MovieNetworkDataSource movieNetworkDataSource;
    private AppExecutor appExecutor;
    private MutableLiveData<FavouriteMovie> selectedMovie;

    private static final String FAVOURITE_DATABASE_VALUE = "favourites";

    private MovieRepository(MovieDao dao, MovieNetworkDataSource networkDataSource, AppExecutor executor){
        movieDao = dao;
        movieNetworkDataSource = networkDataSource;
        appExecutor = executor;
        LiveData<List<Movie>> currentMovies = movieNetworkDataSource.getMovieData();
        currentMovies.observeForever(new Observer<List<Movie>>() {
            @Override
            public void onChanged(@Nullable final List<Movie> movieLists) {
                appExecutor.diskIO().execute(new Runnable() {
                    @Override
                    public void run() {
                        movieDao.deleteOldData();
                        if (movieLists != null) {
                            movieDao.bulkInsert(movieLists);
                        }
                    }
                });
            }
        });
    }

    public synchronized static MovieRepository getInstance(MovieDao dao,
                                                           MovieNetworkDataSource networkDataSource,
                                                           AppExecutor executor) {
        if (sInstance == null) {
            synchronized (LOCK) {
                sInstance = new MovieRepository(dao, networkDataSource, executor);
            }
        }
        return sInstance;
    }

//    public LiveData<List<MovieList>> getMovieData(String sortOrder) {
//        if (sortOrder.equals(FAVOURITE_DATABASE_VALUE)) {
//            return movieDao.loadAllFavouriteMovies();
//        } else {
//            movieNetworkDataSource.fetchMovie(sortOrder);
//            return movieNetworkDataSource.getMovieData();
//        }
//    }

    public LiveData<List<Movie>> getOtherMovieData(String sortOrder) {
        movieNetworkDataSource.fetchMovie(sortOrder);
        return movieDao.loadAllCurrentMovies();
    }

    public LiveData<List<Movie>> getFavouriteMovieData() {
        return movieDao.loadAllFavouriteMovies();
    }

    public boolean containMovieId(int movieId){
        return movieDao.getCountByMovieId(movieId) > 0;
    }

    public void insertFavourite(final FavouriteMovie movie) {
        appExecutor.diskIO().execute(new Runnable() {
            @Override
            public void run() {
                movieDao.insertFavourite(movie);
            }
        });
    }

    public FavouriteMovie getMovieDetails(final int movieId) {
//        movieNetworkDataSource.fetchMovieDetail(movieId);
//        LiveData<FavouriteMovie> movieDetail = movieNetworkDataSource.getMovieDetail();
//        Log.e("GOTMOVIE", movieDetail.getValue().toString());
//        if (movieDetail.getValue() != null) {
//            Log.e("1", "1");
//            return movieDetail;
//        } else {
//            return movieDao.loadFavouriteByMovieId(movieId);
//        }
//        appExecutor.networkIO().execute(new Runnable() {
//            @Override
//            public void run() {
//                if (NetworkUtils.isOnline()) {
//                    movieNetworkDataSource.fetchMovieDetail(movieId);
//                    selectedMovie.postValue(movieNetworkDataSource.getMovieDetail().getValue());
//                } else if (containMovieId(movieId)) {
//                    selectedMovie.postValue(movieDao.loadFavouriteByMovieId(movieId).getValue());
//                } else {
//                    selectedMovie.postValue(null);
//                }
//            }
//        });
        return movieDao.loadFavouriteByMovieId(movieId);
    }
    public void deleteSingleMovie(final int movieId) {
        appExecutor.diskIO().execute(new Runnable() {
            @Override
            public void run() {
                movieDao.deleteSingleMovie(movieId);
            }
        });
    }


}
