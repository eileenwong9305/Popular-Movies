package com.example.android.popularmovies;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.Observer;
import android.support.annotation.Nullable;
import android.util.Log;

import com.example.android.popularmovies.Data.FavouriteMovie;
import com.example.android.popularmovies.Data.Movie;
import com.example.android.popularmovies.Data.MovieList;
import com.example.android.popularmovies.Data.Review;
import com.example.android.popularmovies.Data.Trailer;
import com.example.android.popularmovies.Database.MovieDao;
import com.example.android.popularmovies.Utils.AppExecutor;
import com.example.android.popularmovies.Utils.MovieNetworkDataSource;
import com.example.android.popularmovies.Utils.NetworkUtils;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class MovieRepository {

    private static final Object LOCK = new Object();
    private static MovieRepository sInstance;
    private MovieDao movieDao;
    private MovieNetworkDataSource movieNetworkDataSource;
    private AppExecutor appExecutor;
    private MutableLiveData<FavouriteMovie> movieDetails;
    private MutableLiveData<List<Trailer>> movieTrailers;
    private MutableLiveData<List<Review>> movieReviews;
    private MutableLiveData<List<MovieList>> mMovieList;

    private MovieRepository(MovieDao dao, MovieNetworkDataSource networkDataSource, AppExecutor executor) {
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
                        if(movieLists == null){
                            Log.e("MovieRepo", "null");
                        }
                        if (movieLists != null) {
                            Log.e("MovieRepo", "insert movielist");
                            movieDao.bulkInsert(movieLists);
                        }
                    }
                });
            }
        });
        mMovieList = new MutableLiveData<>();
        movieDetails = new MutableLiveData<>();
        movieTrailers = new MutableLiveData<>();
        movieReviews = new MutableLiveData<>();
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
    public void loadMovieList(final String sortOrder) {
        appExecutor.diskIO().execute(new Runnable() {
            @Override
            public void run() {
                if (sortOrder.equals("favourites")) {
                    mMovieList.postValue(movieDao.loadAllFavouriteMoviesN());
                } else {
                    final long timeNow = System.currentTimeMillis();
                    long maxLastUpdatedAt = timeNow - 60000;
                    if (movieDao.hasMovie(sortOrder, maxLastUpdatedAt) < 1) {
                        appExecutor.networkIO().execute(new Runnable() {
                            @Override
                            public void run() {
                                if (NetworkUtils.isOnline()) {
                                    try {
                                        Log.e("NetworkDataSource", "fetch Movie");
                                        URL queryUrl = NetworkUtils.buildUrl(sortOrder);
                                        String json = NetworkUtils.getResponseFromHttp(queryUrl);
                                        List<Movie> movies = NetworkUtils.parseMovieJson(json);
                                        List<MovieList> movieList = new ArrayList<MovieList>();
                                        for (Movie movie: movies) {
                                            movie.setSort(sortOrder);
                                            movie.setUpdatedAt(timeNow);
                                            movieList.add(new MovieList(movie.getId(), movie.getTitle(), movie.getPoster(), movie.getMovieId()));
                                        }
                                        mMovieList.postValue(movieList);
                                        movieDao.deleteOldData();
                                        if(movies == null){
                                            Log.e("MovieRepo", "null");
                                        } else {
                                            Log.e("MovieRepo", "insert movielist");
                                            movieDao.bulkInsert(movies);
                                        }
                                    } catch (Exception ex) {
                                        ex.printStackTrace();
                                    }
                                } else {
                                    Log.e("NetworkDataSource", "null");
                                }
                            }
                        });
                    } else {
                        mMovieList.postValue(movieDao.loadAllCurrentMoviesN());
                    }
                }
            }
        });
    }

    public LiveData<List<MovieList>> getMovieList(String sortOrder) {
        loadMovieList(sortOrder);
        return mMovieList;
    }

    public LiveData<List<MovieList>> getOtherMovieData(String sortOrder) {
        movieNetworkDataSource.fetchMovie(sortOrder);
        return movieDao.loadAllCurrentMovies();
    }

    public LiveData<List<MovieList>> getFavouriteMovieData() {
        return movieDao.loadAllFavouriteMovies();
    }

    public boolean containMovieId(int movieId) {
        return movieDao.getCountByMovieId(movieId) > 0;
    }

    public void insertFavourite(final FavouriteMovie movie, final List<Review> reviews, final List<Trailer> trailers) {
        appExecutor.diskIO().execute(new Runnable() {
            @Override
            public void run() {
                movieDao.insertFavourite(movie);
                if (reviews != null) {
                    movieDao.insertReview(reviews);
                }
                if (trailers != null) {
                    movieDao.insertTrailer(trailers);
                }
            }
        });
    }

    public void loadMovieDetails(final int movieId) {
        appExecutor.diskIO().execute(new Runnable() {
            @Override
            public void run() {
                if (containMovieId(movieId)) {
                    movieDetails.postValue(movieDao.loadFavouriteByMovieId(movieId));
                    movieTrailers.postValue(movieDao.loadTrailerByMovieId(movieId));
                    movieReviews.postValue(movieDao.loadReviewByMovieId(movieId));
                } else {
                    appExecutor.networkIO().execute(new Runnable() {
                        @Override
                        public void run() {
                            if (NetworkUtils.isOnline()) {
                                try {
                                    URL detailUrl = NetworkUtils.buildUrl(movieId, NetworkUtils.DETAIL_PATH);
                                    String detailJson = NetworkUtils.getResponseFromHttp(detailUrl);
                                    FavouriteMovie detail = NetworkUtils.parseMovieDetailJson(detailJson);
                                    if (detail != null) {
                                        movieDetails.postValue(detail);
                                    }

                                    URL videoUrl = NetworkUtils.buildUrl(movieId, NetworkUtils.VIDEOS_PATH);
                                    String videoJson = NetworkUtils.getResponseFromHttp(videoUrl);
                                    List<Trailer> trailer = NetworkUtils.parseMovieVideosJson(videoJson);
                                    if (trailer != null && trailer.size() != 0) {
                                        movieTrailers.postValue(trailer);
                                    } else {
                                        movieTrailers.postValue(null);
                                    }

                                    URL reviewUrl = NetworkUtils.buildUrl(movieId, NetworkUtils.REVIEWS_PATH);
                                    String reviewJson = NetworkUtils.getResponseFromHttp(reviewUrl);
                                    List<Review> review = NetworkUtils.parseMovieReviewsJson(reviewJson);
                                    if (review != null && review.size() != 0) {
                                        movieReviews.postValue(review);
                                    } else {
                                        movieReviews.postValue(null);
                                    }
                                } catch (Exception ex) {
                                    ex.printStackTrace();
                                }
                            } else {
                                movieDetails.postValue(null);
                                movieTrailers.postValue(null);
                                movieReviews.postValue(null);
                            }
                        }
                    });
                }
            }
        });
    }

    public LiveData<FavouriteMovie> getMovieDetail(int movieId) {
        loadMovieDetails(movieId);
        return movieDetails;
    }

    public LiveData<List<Trailer>> getMovieVideo() {
        return movieTrailers;
    }

    public LiveData<List<Review>> getMovieReview() {
        return movieReviews;
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
