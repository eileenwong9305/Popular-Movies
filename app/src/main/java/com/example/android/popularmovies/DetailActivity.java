package com.example.android.popularmovies;

import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.NavUtils;
import android.support.v4.app.ShareCompat;
import android.support.v4.widget.NestedScrollView;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.beloo.widget.chipslayoutmanager.ChipsLayoutManager;
import com.beloo.widget.chipslayoutmanager.SpacingItemDecoration;
import com.example.android.popularmovies.Data.FavouriteMovie;
import com.example.android.popularmovies.Data.Movie;
import com.example.android.popularmovies.Database.FavouriteDatabase;
import com.example.android.popularmovies.Utils.AppExecutor;
import com.example.android.popularmovies.Utils.GenreAdapter;
import com.example.android.popularmovies.Utils.InjectorUtils;
import com.example.android.popularmovies.Utils.NetworkUtils;
import com.example.android.popularmovies.Utils.ReviewsAdapter;
import com.example.android.popularmovies.Utils.TrailersAdapter;
import com.squareup.picasso.Picasso;

import java.net.URL;
import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

public class DetailActivity extends AppCompatActivity implements TrailersAdapter.TrailerClickListener {

    private static final String KEY_PARCEL = "selected_movie";
    private static final String BACKDROP_BASE_PATH = "http://image.tmdb.org/t/p/w500";
    private static final String POSTER_BASE_PATH = "http://image.tmdb.org/t/p/w342";
    public static final String YOUTUBE_BASE_URL = "http://www.youtube.com/watch?v=";

    private FavouriteMovie mMovie;
    private int movieId;
    private ReviewsAdapter reviewsAdapter;
    private TrailersAdapter trailersAdapter;
    private GenreAdapter genreAdapter;

    private FavouriteDatabase mDb;
    private DetailViewModel viewModel;
    private boolean addToFavourite = false;

    @BindView(R.id.iv_detail_backdrop) ImageView backdropImageView;
    @BindView(R.id.iv_detail_poster) ImageView posterImageView;
    @BindView(R.id.tv_detail_overview) TextView overviewTextView;
    @BindView(R.id.tv_detail_user_rating) TextView userRatingTextView;
    @BindView(R.id.tv_detail_release_date) TextView releaseDateTextView;
    @BindView(R.id.tv_detail_language) TextView languageTextView;
    @BindView(R.id.tv_detail_runtime) TextView runtimeTextView;
    @BindView(R.id.rv_review) RecyclerView reviewRecyclerView;
    @BindView(R.id.rv_trailer) RecyclerView trailerRecyclerView;
    @BindView(R.id.fab) FloatingActionButton fab;
    @BindView(R.id.rv_genre) RecyclerView genreRecyclerView;
    @BindView(R.id.cardview_trailer) CardView trailerCardView;
    @BindView(R.id.cardview_reviews) CardView reviewCardView;
    @BindView(R.id.collapsing_toolbar_layout) CollapsingToolbarLayout collapsingToolbarLayout;
    @BindView(R.id.pb_loading_indicator_detail) ProgressBar loadingIndicator;
    @BindView(R.id.tv_error_message_detail) TextView errorMessage;
    @BindView(R.id.scroll) NestedScrollView movieDetailContent;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);
        Toolbar toolbar = (Toolbar) findViewById(R.id.collapsing_toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mDb = FavouriteDatabase.getInstance(getApplicationContext());

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        Intent intent = getIntent();
        if (intent != null) {
            if (intent.hasExtra(MainActivity.KEY_SELECTED_MOVIE_ID_INTENT)) {
                movieId = intent.getIntExtra(MainActivity.KEY_SELECTED_MOVIE_ID_INTENT, 0);
            }
        }

        ButterKnife.bind(this);

        ChipsLayoutManager genreLayoutManager = ChipsLayoutManager.newBuilder(this)
                .setRowStrategy(ChipsLayoutManager.STRATEGY_DEFAULT).withLastRow(true)
                .setOrientation(ChipsLayoutManager.HORIZONTAL)
                .build();
        genreRecyclerView.setLayoutManager(genreLayoutManager);
        genreRecyclerView.addItemDecoration(new SpacingItemDecoration(getResources().getDimensionPixelOffset(R.dimen.item_spacing),
                getResources().getDimensionPixelOffset(R.dimen.item_spacing)));
        genreAdapter = new GenreAdapter();
        genreRecyclerView.setAdapter(genreAdapter);

        LinearLayoutManager reviewLayoutManager = new LinearLayoutManager(this);
        reviewRecyclerView.setLayoutManager(reviewLayoutManager);
        reviewRecyclerView.setHasFixedSize(true);
        reviewsAdapter = new ReviewsAdapter();
        reviewRecyclerView.setAdapter(reviewsAdapter);

        LinearLayoutManager trailerLayoutManager = new LinearLayoutManager(this);
        trailerRecyclerView.setLayoutManager(trailerLayoutManager);
        trailerRecyclerView.setHasFixedSize(true);
        trailersAdapter = new TrailersAdapter(this, this);
        trailerRecyclerView.setAdapter(trailersAdapter);

        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(
                reviewRecyclerView.getContext(),
                reviewLayoutManager.getOrientation());
        reviewRecyclerView.addItemDecoration(dividerItemDecoration);

        new FetchMovieDetailTask().execute(NetworkUtils.buildUrl(movieId));

        showOnlyLoading();
        DetailViewModelFactory factory = new DetailViewModelFactory(InjectorUtils.provideRepository(this));
        viewModel = ViewModelProviders.of(this, factory).get(DetailViewModel.class);

//        mMovie = viewModel.getMovieDetail(movieId).getValue();
//        if (mMovie != null) {
//            showMovieData();
//            showMovieDetails(mMovie);
//        } else {
//            showErrorMessage();
//        }

        AppExecutor.getInstance().diskIO().execute(new Runnable() {
            @Override
            public void run() {
                if (viewModel.containMovieId(movieId)) {
                    addToFavourite = true;
                    fab.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.colorAccent)));
                }
            }
        });

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (addToFavourite) {
                    fab.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.colorPrimary)));
                    viewModel.deleteSingleMovie(movieId);
                    addToFavourite = false;

                    Snackbar.make(view, "Removed from Favourite", Snackbar.LENGTH_LONG)
                            .setAction("Action", null).show();
                } else {
                    fab.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.colorAccent)));
                    viewModel.insertFavourite(mMovie);
                    addToFavourite = true;
                    Snackbar.make(view, "Added to Favourite", Snackbar.LENGTH_LONG)
                            .setAction("Action", null).show();
                }
            }
        });

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
    public void onClick(String movieKey) {
        Uri youtubeUri = Uri.parse(YOUTUBE_BASE_URL + movieKey);
        Intent startYoutube = new Intent(Intent.ACTION_VIEW, youtubeUri);
        if (startYoutube.resolveActivity(getPackageManager()) != null) {
            startActivity(startYoutube);
        }

    }


    public class FetchMovieDetailTask extends AsyncTask<URL, Void, FavouriteMovie> {

        @Override
        protected FavouriteMovie doInBackground(URL... urls) {
            if (urls.length == 0) return null;
            URL url = urls[0];
            if (NetworkUtils.isOnline()){
                try {
                    String json = NetworkUtils.getResponseFromHttp(url);
                    return NetworkUtils.parseMovieDetailJson(json);
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            } else if (viewModel.containMovieId(movieId)) {
                Log.e("Contain", String.valueOf(movieId));
                return viewModel.getMovieDetail(movieId);
            }
            return null;
        }

        @Override
        protected void onPostExecute(FavouriteMovie movie) {
            mMovie = movie;
            if (movie != null) {
                showMovieData();
                showMovieDetails(movie);
            } else {
                showErrorMessage();
            }
        }
    }

    private void showMovieDetails(FavouriteMovie movieDetails) {
        collapsingToolbarLayout.setTitle(movieDetails.getTitle());
        String backdropUrl = BACKDROP_BASE_PATH + movieDetails.getBackdrop();
        Picasso.get().load(backdropUrl).fit().centerCrop().into(backdropImageView);

        if (movieDetails.getPoster().equals("null")) {
            posterImageView.setImageResource(R.drawable.no_pic);
        } else {
            String posterUrl = POSTER_BASE_PATH + movieDetails.getPoster();
            Picasso.get().load(posterUrl).fit().centerCrop().into(posterImageView);
        }
        overviewTextView.setText(movieDetails.getOverview());
        userRatingTextView.setText(movieDetails.getUserRating());
        releaseDateTextView.setText(movieDetails.getReleaseDate());
        runtimeTextView.setText(getString(R.string.runtime_value, movieDetails.getRuntime()));
        languageTextView.setText(movieDetails.getLanguage());
        ArrayList<String> genres = movieDetails.getGenres();
        genreAdapter.setGenres(genres);
        if (movieDetails.getReviews() == null || movieDetails.getReviews().size() == 0) {
            reviewCardView.setVisibility(View.GONE);
        } else {
            reviewCardView.setVisibility(View.VISIBLE);
            reviewsAdapter.setReviews(movieDetails.getReviews());
        }
        if (movieDetails.getTrailers() == null || movieDetails.getTrailers().size() == 0) {
            trailerCardView.setVisibility(View.GONE);
        } else {
            trailerCardView.setVisibility(View.VISIBLE);
            trailersAdapter.setTrailers(movieDetails.getTrailers());
        }
    }

    /**
     * Show data and hide error message
     */
    private void showMovieData() {
        loadingIndicator.setVisibility(View.INVISIBLE);
        errorMessage.setVisibility(View.INVISIBLE);
        movieDetailContent.setVisibility(View.VISIBLE);
    }

    /**
     * Show error message and hide data
     */
    private void showErrorMessage() {
        loadingIndicator.setVisibility(View.INVISIBLE);
        errorMessage.setVisibility(View.VISIBLE);
        movieDetailContent.setVisibility(View.INVISIBLE);
    }

    /**
     * Hide data and error message and display loading indicator
     */
    private void showOnlyLoading() {
        errorMessage.setVisibility(View.INVISIBLE);
        movieDetailContent.setVisibility(View.INVISIBLE);
        loadingIndicator.setVisibility(View.VISIBLE);
    }
}
