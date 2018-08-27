package com.example.android.popularmovies;

import com.example.android.popularmovies.Data.Movie;
import com.example.android.popularmovies.Data.MovieList;
import com.example.android.popularmovies.Data.MovieListResponse;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface MovieDBService {
    @GET("{sort}")
    Call<MovieListResponse> getMovie(@Path("sort") String sortOrder, @Query("api_key") String key);
}
