package com.example.android.popularmovies.repository;

import android.arch.lifecycle.LiveData;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.example.android.popularmovies.api.ApiInterface;
import com.example.android.popularmovies.api.ApiResponse;
import com.example.android.popularmovies.api.MovieListResponse;
import com.example.android.popularmovies.api.ReviewResponse;
import com.example.android.popularmovies.api.VideoResponse;
import com.example.android.popularmovies.data.FavouriteMovie;
import com.example.android.popularmovies.data.Movie;
import com.example.android.popularmovies.data.Resource;
import com.example.android.popularmovies.data.Review;
import com.example.android.popularmovies.data.Trailer;
import com.example.android.popularmovies.database.MovieDao;
import com.example.android.popularmovies.ui.detail.DetailActivity;
import com.example.android.popularmovies.utils.AbsentLiveData;
import com.example.android.popularmovies.utils.AppExecutor;
import com.example.android.popularmovies.utils.SharedPreferenceHelper;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import javax.inject.Inject;
import javax.inject.Singleton;

@Singleton
public class MovieRepository {

    private static final String PREF_SAVED_DATE_KEY = "saved_date";
    private static final String PREF_PREVIOUS_SORT_KEY = "previous_sort";
    private static final long REFRESH_TIME = 60;
    private static final long REFRESH_TIME_MILLIS = TimeUnit.MINUTES.toMillis(REFRESH_TIME);

    private final ApiInterface mApiInterface;
    private final MovieDao movieDao;
    private final AppExecutor appExecutor;

    @Inject
    public MovieRepository(ApiInterface apiInterface, MovieDao dao, AppExecutor executor) {
        mApiInterface = apiInterface;
        movieDao = dao;
        appExecutor = executor;
    }

    public LiveData<Resource<List<Movie>>> loadMovie(final String sortOrder) {
        return new NetworkBoundResource<List<Movie>, MovieListResponse>(appExecutor) {
            @Override
            protected void saveCallResult(@NonNull MovieListResponse item) {
                SharedPreferenceHelper.setSharedPreference(PREF_SAVED_DATE_KEY, System.currentTimeMillis());
                SharedPreferenceHelper.setSharedPreference(PREF_PREVIOUS_SORT_KEY, sortOrder);
                movieDao.deleteOldData();
                List<Movie> movies = item.getResults();
                movieDao.bulkInsert(movies);
            }

            @Override
            protected boolean shouldFetch(@Nullable List<Movie> data) {
                long timeNow = System.currentTimeMillis();
                long savedDate = SharedPreferenceHelper.getSortPreferenceValue(PREF_SAVED_DATE_KEY,
                        timeNow);
                String previousSort = SharedPreferenceHelper.getSortPreferenceValue(
                        PREF_PREVIOUS_SORT_KEY, "");
                return timeNow - savedDate > REFRESH_TIME_MILLIS || !previousSort.equals(sortOrder);
            }

            @NonNull
            @Override
            protected LiveData<List<Movie>> loadFromDB() {
                return movieDao.loadAllCurrentMovies();
            }

            @NonNull
            @Override
            protected LiveData<ApiResponse<MovieListResponse>> createCall() {
                return mApiInterface.getMovie(sortOrder);
            }
        }.getAsLiveData();
    }

    public LiveData<List<Movie>> loadFavMovie() {
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

    public LiveData<Resource<FavouriteMovie>> loadMovieDetail(final int movieId) {
        return new NetworkBoundResource<FavouriteMovie, FavouriteMovie>(appExecutor) {
            FavouriteMovie movie;

            @Override
            protected void saveCallResult(@NonNull FavouriteMovie item) {
                movie = item;
            }

            @Override
            protected boolean shouldFetch(@Nullable FavouriteMovie data) {
                return data == null;
            }

            @NonNull
            @Override
            protected LiveData<FavouriteMovie> loadFromDB() {
                boolean isFavourite = SharedPreferenceHelper.getSortPreferenceValue(DetailActivity.PREF_IS_FAVOURITE_KEY, false);
                if (movie == null) {
                    if (isFavourite) {
                        return movieDao.loadDetailByMovieId(movieId);
                    } else {
                        return AbsentLiveData.create();
                    }
                } else {
                    return new LiveData<FavouriteMovie>() {
                        @Override
                        protected void onActive() {
                            super.onActive();
                            setValue(movie);
                        }
                    };
                }
            }

            @NonNull
            @Override
            protected LiveData<ApiResponse<FavouriteMovie>> createCall() {
                return mApiInterface.getMovieDetail(movieId);
            }
        }.getAsLiveData();
    }

    public LiveData<Resource<List<Trailer>>> loadMovieVideo(final int movieId) {
        return new NetworkBoundResource<List<Trailer>, VideoResponse>(appExecutor) {
            List<Trailer> trailers = new ArrayList<>();

            @Override
            protected void saveCallResult(@NonNull VideoResponse item) {
                trailers = item.getVideos();
                for (Trailer trailer : trailers) {
                    trailer.setMovieId(movieId);
                }
                if (containMovieId(movieId)) {
                    movieDao.insertTrailer(trailers);
                }
            }

            @Override
            protected boolean shouldFetch(@Nullable List<Trailer> data) {
                return data == null || data.isEmpty();
            }

            @NonNull
            @Override
            protected LiveData<List<Trailer>> loadFromDB() {
                boolean isFavourite = SharedPreferenceHelper.getSortPreferenceValue(DetailActivity.PREF_IS_FAVOURITE_KEY, false);
                if (isFavourite) {
                    return movieDao.loadTrailerByMovieId(movieId);
                } else {
                    return new LiveData<List<Trailer>>() {
                        @Override
                        protected void onActive() {
                            super.onActive();
                            setValue(trailers);
                        }
                    };
                }
            }

            @NonNull
            @Override
            protected LiveData<ApiResponse<VideoResponse>> createCall() {
                return mApiInterface.getMovieVideo(movieId);
            }
        }.getAsLiveData();
    }

    public LiveData<Resource<List<Review>>> loadMovieReview(final int movieId) {
        return new NetworkBoundResource<List<Review>, ReviewResponse>(appExecutor) {
            List<Review> reviews = new ArrayList<>();

            @Override
            protected void saveCallResult(@NonNull ReviewResponse item) {
                reviews = item.getResults();
                for (Review review : reviews) {
                    review.setMovieId(movieId);
                }
                if (containMovieId(movieId)) {
                    movieDao.insertReview(reviews);
                }
            }

            @Override
            protected boolean shouldFetch(@Nullable List<Review> data) {
                return data == null || data.isEmpty();
            }

            @NonNull
            @Override
            protected LiveData<List<Review>> loadFromDB() {
                boolean isFavourite = SharedPreferenceHelper.getSortPreferenceValue(DetailActivity.PREF_IS_FAVOURITE_KEY, false);
                if (isFavourite) {
                    return movieDao.loadReviewByMovieId(movieId);
                } else {
                    return new LiveData<List<Review>>() {
                        @Override
                        protected void onActive() {
                            super.onActive();
                            setValue(reviews);
                        }
                    };
                }
            }

            @NonNull
            @Override
            protected LiveData<ApiResponse<ReviewResponse>> createCall() {
                return mApiInterface.getMovieReview(movieId);
            }
        }.getAsLiveData();
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
