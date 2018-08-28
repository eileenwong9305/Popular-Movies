package com.example.android.popularmovies;

import com.example.android.popularmovies.Data.FavouriteMovie;
import com.example.android.popularmovies.Data.MovieListResponse;
import com.example.android.popularmovies.Data.ReviewResponse;
import com.example.android.popularmovies.Data.Trailer;
import com.example.android.popularmovies.Data.VideoResponse;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;

public interface ApiInterface {

    @GET("{sort}")
    Call<MovieListResponse> getMovie(@Path("sort") String sortOrder);

    @GET("{movie_id}")
    Call<FavouriteMovie> getMovieDetail(@Path("movie_id") int movieId);

    @GET("{movie_id}/videos")
    Call<VideoResponse> getMovieVideo(@Path("movie_id") int movieId);

    @GET("{movie_id}/reviews")
    Call<ReviewResponse> getMovieReview(@Path("movie_id") int movieId);
}
