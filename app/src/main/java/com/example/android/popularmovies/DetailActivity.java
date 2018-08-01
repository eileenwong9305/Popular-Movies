package com.example.android.popularmovies;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.android.popularmovies.Data.Movie;
import com.example.android.popularmovies.Utils.NetworkUtils;
import com.example.android.popularmovies.Utils.ReviewsAdapter;
import com.squareup.picasso.Picasso;

import java.net.URL;
import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

public class DetailActivity extends AppCompatActivity implements ReviewsAdapter.ReviewItemListener{

    private static final String KEY_PARCEL = "selected_movie";
    private static final String BACKDROP_BASE_PATH = "http://image.tmdb.org/t/p/w500";
    private static final String POSTER_BASE_PATH = "http://image.tmdb.org/t/p/w342";

    private Movie movie;
    private ReviewsAdapter reviewsAdapter;

    @BindView(R.id.iv_detail_backdrop) ImageView backdropImageView;
    @BindView(R.id.iv_detail_poster) ImageView posterImageView;
    @BindView(R.id.tv_detail_title) TextView titleTextView;
    @BindView(R.id.tv_detail_overview) TextView overviewTextView;
    @BindView(R.id.tv_detail_user_rating) TextView userRatingTextView;
    @BindView(R.id.tv_detail_release_date) TextView releaseDateTextView;
    @BindView(R.id.tv_detail_genre) TextView genreTextView;
    @BindView(R.id.tv_detail_runtime) TextView runtimeTextView;
    @BindView(R.id.rv_review) RecyclerView recyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        Intent intent = getIntent();
        if (intent != null) {
            if (intent.hasExtra(KEY_PARCEL)) {
                movie = intent.getParcelableExtra(KEY_PARCEL);
            }
        }

        ButterKnife.bind(this);

        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setHasFixedSize(true);
        reviewsAdapter = new ReviewsAdapter();
        recyclerView.setAdapter(reviewsAdapter);

        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(
                recyclerView.getContext(),
                layoutManager.getOrientation());
        recyclerView.addItemDecoration(dividerItemDecoration);

//        String backdropUrl = BACKDROP_BASE_PATH + movie.getBackdrop();
//        Picasso.get().load(backdropUrl).fit().centerCrop().into(backdropImageView);
//
//        if (movie.getPoster().equals("null")) {
//            posterImageView.setImageResource(R.drawable.no_pic);
//        } else {
//            String posterUrl = POSTER_BASE_PATH + movie.getPoster();
//            Picasso.get().load(posterUrl).fit().centerCrop().into(posterImageView);
//        }
//        titleTextView.setText(movie.getTitle());
//        overviewTextView.setText(movie.getOverview());
//        userRatingTextView.setText(getString(R.string.user_rating_value, movie.getUserRating()));
//        releaseDateTextView.setText(movie.getReleaseDate());
        new FetchMovieDetailTask().execute(NetworkUtils.buildUrl(movie.getMovieId()));
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {
            NavUtils.navigateUpFromSameTask(this);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onClick(TextView textView) {
        textView.setMaxLines(Integer.MAX_VALUE);
    }

    public class FetchMovieDetailTask extends AsyncTask<URL, Void, Movie> {

        @Override
        protected Movie doInBackground(URL... urls) {
            if (urls.length == 0) return null;
            URL url = urls[0];
            if (NetworkUtils.isOnline()){
                try {
                    String json = NetworkUtils.getResponseFromHttp(url);
                    return NetworkUtils.parseMovieDetailJson(json);
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
            return null;
        }

        @Override
        protected void onPostExecute(Movie movie) {
            if (movie != null) {
                showMovieDetails(movie);
            }
        }
    }

    private void showMovieDetails(Movie addMovieDetails) {
        String backdropUrl = BACKDROP_BASE_PATH + movie.getBackdrop();
        Picasso.get().load(backdropUrl).fit().centerCrop().into(backdropImageView);

        if (movie.getPoster().equals("null")) {
            posterImageView.setImageResource(R.drawable.no_pic);
        } else {
            String posterUrl = POSTER_BASE_PATH + movie.getPoster();
            Picasso.get().load(posterUrl).fit().centerCrop().into(posterImageView);
        }
        titleTextView.setText(movie.getTitle());
        overviewTextView.setText(movie.getOverview());
        userRatingTextView.setText(getString(R.string.user_rating_value, movie.getUserRating()));
        releaseDateTextView.setText(movie.getReleaseDate());
        runtimeTextView.setText(addMovieDetails.getRuntime());

        ArrayList<String> genres = addMovieDetails.getGenres();
        for (String genre : genres) {
            genreTextView.append(genre + " ");
        }
        reviewsAdapter.setReviews(addMovieDetails.getReviews());
    }
}
