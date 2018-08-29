package com.example.android.popularmovies;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.Observer;
import android.support.annotation.Nullable;
import android.util.Log;

import com.example.android.popularmovies.Data.Example;
import com.example.android.popularmovies.Data.FavouriteMovie;
import com.example.android.popularmovies.Data.Movie;
import com.example.android.popularmovies.Data.MovieList;
import com.example.android.popularmovies.Data.MovieListResponse;
import com.example.android.popularmovies.Data.Review;
import com.example.android.popularmovies.Data.ReviewResponse;
import com.example.android.popularmovies.Data.Trailer;
import com.example.android.popularmovies.Data.VideoResponse;
import com.example.android.popularmovies.Database.MovieDao;
import com.example.android.popularmovies.Utils.AppExecutor;
import com.example.android.popularmovies.Utils.NetworkUtils;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;
import javax.inject.Singleton;

import retrofit2.Response;

@Singleton
public class MovieRepository {

    private static final Object LOCK = new Object();
    private static MovieRepository sInstance;
    private final ApiInterface mApiInterface;
    private final MovieDao movieDao;
    private final AppExecutor appExecutor;
    private MutableLiveData<FavouriteMovie> movieDetails;
    private MutableLiveData<List<Trailer>> movieTrailers;
    private MutableLiveData<List<Review>> movieReviews;
    private MutableLiveData<List<MovieList>> mMovieList;
    private LiveData<List<MovieList>> favMovie;

    @Inject
    public MovieRepository(ApiInterface apiInterface, MovieDao dao, AppExecutor executor) {
        mApiInterface = apiInterface;
        movieDao = dao;
        appExecutor = executor;

        mMovieList = new MutableLiveData<>();
        movieDetails = new MutableLiveData<>();
        movieTrailers = new MutableLiveData<>();
        movieReviews = new MutableLiveData<>();
    }

    public void loadMovieListRetro(final String sortOrder) {
        appExecutor.diskIO().execute(new Runnable() {
            @Override
            public void run() {
                final long timeNow = System.currentTimeMillis();
                long maxLastUpdatedAt = timeNow - 60000;
                Log.e("Count", String.valueOf(movieDao.hasMovie(sortOrder, maxLastUpdatedAt)));
                if (movieDao.hasMovie(sortOrder, maxLastUpdatedAt) < 1) {
                    movieDao.deleteOldData();
                    appExecutor.networkIO().execute(new Runnable() {
                        @Override
                        public void run() {
                            if (NetworkUtils.isOnline()) {
                                try {
                                    Response<MovieListResponse> response = mApiInterface.getMovie(sortOrder).execute();
                                    MovieListResponse moviesResponse = response.body();
                                    List<MovieList> movieList = moviesResponse.getResults();

//                                    mMovieList.postValue(movieList);
                                    List<Movie> movies = new ArrayList<Movie>();
                                    for (MovieList movie: movieList) {
                                        movies.add(new Movie(movie.getTitle(), movie.getPoster(), movie.getMovieId(), sortOrder, timeNow));
                                    }

                                    if(movies != null){
                                        movieDao.bulkInsert(movies);
                                    }
                                } catch (Exception ex) {
                                    ex.printStackTrace();
                                }
                            } else {
                                Log.e("NetworkDataSource", "null");
                            }
                            mMovieList.postValue(movieDao.loadAllCurrentMoviesN());
                        }
                    });
                } else {
                    mMovieList.postValue(movieDao.loadAllCurrentMoviesN());
                }
            }
        });
    }

    public LiveData<List<MovieList>> getMovieList(String sortOrder) {
        loadMovieListRetro(sortOrder);
//        return mMovieList;
        return movieDao.loadAllCurrentMovies();
    }

    public LiveData<List<MovieList>> getFavMovieList() {
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
                                    Response<FavouriteMovie> detailResponse = mApiInterface.getMovieDetail(movieId).execute();
                                    FavouriteMovie detail = detailResponse.body();
                                    if (detail != null) {
                                        movieDetails.postValue(detail);
                                    }

                                    Response<VideoResponse> videoResponse = mApiInterface.getMovieVideo(movieId).execute();
                                    List<Trailer> trailers = videoResponse.body().getVideos();

                                    if (trailers != null && trailers.size() != 0) {
                                        for (Trailer trailer : trailers) {
                                            trailer.setMovieId(movieId);
                                        }
                                        movieTrailers.postValue(trailers);
                                    } else {
                                        movieTrailers.postValue(null);
                                    }

                                    Response<ReviewResponse> reviewResponse = mApiInterface.getMovieReview(movieId).execute();
                                    List<Review> reviews = reviewResponse.body().getResults();
                                    if (reviews != null && reviews.size() != 0) {
                                        for (Review review : reviews) {
                                            review.setMovieId(movieId);
                                        }
                                        movieReviews.postValue(reviews);
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
