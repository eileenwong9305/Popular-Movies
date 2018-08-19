package com.example.android.popularmovies.Utils;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.util.Log;

import com.example.android.popularmovies.Data.FavouriteMovie;
import com.example.android.popularmovies.Data.Movie;
import com.example.android.popularmovies.Data.Review;
import com.example.android.popularmovies.Data.Trailer;

import java.net.URL;
import java.util.List;

public class MovieNetworkDataSource {

    private static final Object LOCK = new Object();
    private static MovieNetworkDataSource sInstance;
    private final AppExecutor appExecutor;

    private final MutableLiveData<List<Movie>> downloadedMovieData;
    private final MutableLiveData<FavouriteMovie> movieDetail;
    private final MutableLiveData<List<Trailer>> movieVideo;
    private final MutableLiveData<List<Review>> movieReview;

    private FavouriteMovie movieDetails;

    private MovieNetworkDataSource (AppExecutor appExecutor) {
        this.appExecutor = appExecutor;
        downloadedMovieData = new MutableLiveData<>();
        movieDetail = new MutableLiveData<>();
        movieVideo = new MutableLiveData<>();
        movieReview = new MutableLiveData<>();
    }

    public static MovieNetworkDataSource getInstance(AppExecutor appExecutor) {
        if(sInstance == null) {
            synchronized (LOCK) {
                sInstance = new MovieNetworkDataSource(appExecutor);
            }
        }
        return sInstance;
    }

    public void fetchMovie(final String sortOrder) {
        appExecutor.networkIO().execute(new Runnable() {
            @Override
            public void run() {
                if (NetworkUtils.isOnline()) {
                    try {
                        URL queryUrl = NetworkUtils.buildUrl(sortOrder);
                        String json = NetworkUtils.getResponseFromHttp(queryUrl);
                        List<Movie> movies = NetworkUtils.parseMovieJson(json);
                        if (movies != null) {
                            downloadedMovieData.postValue(movies);
                        }
                    } catch (Exception ex) {
                        ex.printStackTrace();
                    }
                } else {
                    downloadedMovieData.postValue(null);
                }
            }
        });
    }

    public void fetchMovieDetail(final int movieId) {
        appExecutor.networkIO().execute(new Runnable() {
            @Override
            public void run() {
                if (NetworkUtils.isOnline()) {
                    try {
                        URL detailUrl = NetworkUtils.buildUrl(movieId, NetworkUtils.DETAIL_PATH);
                        Log.e("DETAIL_PATH URL", detailUrl.toString());
                        String detailJson = NetworkUtils.getResponseFromHttp(detailUrl);
                        FavouriteMovie detail = NetworkUtils.parseMovieDetailJson(detailJson);
                        movieDetails = detail;
                        if (detail != null) {
                            movieDetail.postValue(detail);
                        }

                        URL videoUrl = NetworkUtils.buildUrl(movieId, NetworkUtils.VIDEOS_PATH);
                        Log.e("VIDEOS_PATH URL", detailUrl.toString());
                        String videoJson = NetworkUtils.getResponseFromHttp(videoUrl);
                        List<Trailer> trailer = NetworkUtils.parseMovieVideosJson(videoJson);
                        if (trailer != null && trailer.size() != 0) {
                            movieVideo.postValue(trailer);
                        } else {
                            movieVideo.postValue(null);
                        }

                        URL reviewUrl = NetworkUtils.buildUrl(movieId, NetworkUtils.REVIEWS_PATH);
                        Log.e("REVIEWS_PATH URL", detailUrl.toString());
                        String reviewJson = NetworkUtils.getResponseFromHttp(reviewUrl);
                        List<Review> review = NetworkUtils.parseMovieReviewsJson(reviewJson);
                        if (review != null && review.size() != 0) {
                            movieReview.postValue(review);
                        } else {
                            movieReview.postValue(null);
                        }


                    } catch (Exception ex) {
                        ex.printStackTrace();
                    }
                } else {
                    movieDetail.postValue(null);
                    movieVideo.postValue(null);
                    movieReview.postValue(null);
                }
            }
        });
    }

    public LiveData<List<Movie>> getMovieData() {
        return downloadedMovieData;
    }

    public LiveData<FavouriteMovie> getMovieDetail() {
        return movieDetail;
    }

    public LiveData<List<Review>> getMovieReview() {
        return movieReview;
    }

    public LiveData<List<Trailer>> getMovieVideo() {
        return movieVideo;
    }

    public FavouriteMovie getMovieDetails() {
        return movieDetail.getValue();
    }
}
