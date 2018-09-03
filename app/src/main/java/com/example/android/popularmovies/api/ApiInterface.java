package com.example.android.popularmovies.api;

import android.arch.lifecycle.LiveData;

import com.example.android.popularmovies.data.FavouriteMovie;

import retrofit2.http.GET;
import retrofit2.http.Path;

public interface ApiInterface {

    @GET("{sort}")
    LiveData<ApiResponse<MovieListResponse>> getMovie(@Path("sort") String sortOrder);

    @GET("{movie_id}")
    LiveData<ApiResponse<FavouriteMovie>> getMovieDetail(@Path("movie_id") int movieId);

    @GET("{movie_id}/videos")
    LiveData<ApiResponse<VideoResponse>> getMovieVideo(@Path("movie_id") int movieId);

    @GET("{movie_id}/reviews")
    LiveData<ApiResponse<ReviewResponse>> getMovieReview(@Path("movie_id") int movieId);
}
