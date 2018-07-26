package com.example.android.popularmovies;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.android.popularmovies.Utils.Movie;
import com.squareup.picasso.Picasso;

public class DetailActivity extends AppCompatActivity {

    private static final String KEY_PARCEL = "selected_movie";
    private static final String BACKDROP_BASE_PATH = "http://image.tmdb.org/t/p/w500";
    private static final String POSTER_BASE_PATH = "http://image.tmdb.org/t/p/w342";

    private Movie movie;

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

        ImageView backdropImageView = findViewById(R.id.iv_detail_backdrop);
        ImageView posterImageView = findViewById(R.id.iv_detail_poster);
        TextView titleTextView = findViewById(R.id.tv_detail_title);
        TextView overviewTextView = findViewById(R.id.tv_detail_overview);
        TextView userRatingTextView = findViewById(R.id.tv_detail_user_rating);
        TextView releaseDateTextView = findViewById(R.id.tv_detail_release_date);

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
}
