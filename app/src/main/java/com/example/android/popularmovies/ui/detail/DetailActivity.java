package com.example.android.popularmovies.ui.detail;

import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProvider;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.constraint.ConstraintLayout;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.NavUtils;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RatingBar;
import android.widget.TextView;

import com.beloo.widget.chipslayoutmanager.ChipsLayoutManager;
import com.beloo.widget.chipslayoutmanager.SpacingItemDecoration;
import com.example.android.popularmovies.R;
import com.example.android.popularmovies.adapter.GenreAdapter;
import com.example.android.popularmovies.adapter.ReviewsAdapter;
import com.example.android.popularmovies.adapter.TrailersAdapter;
import com.example.android.popularmovies.data.FavouriteMovie;
import com.example.android.popularmovies.data.Genre;
import com.example.android.popularmovies.data.Resource;
import com.example.android.popularmovies.data.Review;
import com.example.android.popularmovies.data.Trailer;
import com.example.android.popularmovies.ui.main.MainActivity;
import com.example.android.popularmovies.utils.AppExecutor;
import com.example.android.popularmovies.utils.SharedPreferenceHelper;
import com.squareup.picasso.Picasso;

import net.opacapp.multilinecollapsingtoolbar.CollapsingToolbarLayout;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import dagger.android.AndroidInjection;

public class DetailActivity extends AppCompatActivity implements TrailersAdapter.TrailerClickListener {

    public static final String PREF_IS_FAVOURITE_KEY = "fav";
    public static final String YOUTUBE_BASE_URL = "http://www.youtube.com/watch?v=";
    private static final String BACKDROP_BASE_PATH = "http://image.tmdb.org/t/p/w500";
    private static final String POSTER_BASE_PATH = "http://image.tmdb.org/t/p/w342";
    @BindView(R.id.iv_detail_backdrop)
    public ImageView backdropImageView;
    @BindView(R.id.iv_detail_poster)
    public ImageView posterImageView;
    @BindView(R.id.tv_detail_overview)
    public TextView overviewTextView;
    @BindView(R.id.tv_detail_user_rating)
    public TextView userRatingTextView;
    @BindView(R.id.tv_detail_release_date)
    public TextView releaseDateTextView;
    @BindView(R.id.tv_detail_language)
    public TextView languageTextView;
    @BindView(R.id.tv_detail_runtime)
    public TextView runtimeTextView;
    @BindView(R.id.rv_review)
    public RecyclerView reviewRecyclerView;
    @BindView(R.id.rv_trailer)
    public RecyclerView trailerRecyclerView;
    @BindView(R.id.fab)
    public FloatingActionButton fab;
    @BindView(R.id.rv_genre)
    public RecyclerView genreRecyclerView;
    @BindView(R.id.cardview_trailer)
    public CardView trailerCardView;
    @BindView(R.id.cardview_reviews)
    public CardView reviewCardView;
    @BindView(R.id.collapsing_toolbar_layout)
    public CollapsingToolbarLayout collapsingToolbarLayout;
    @BindView(R.id.collapsing_toolbar_appbarlayout)
    public AppBarLayout collapsingAppBarLayout;
    @BindView(R.id.pb_loading_indicator_detail)
    public ProgressBar loadingIndicator;
    @BindView(R.id.tv_error_message_detail)
    public TextView errorMessage;
    @BindView(R.id.layout_movie_content)
    public ConstraintLayout movieContent;
    @BindView(R.id.collapsing_toolbar)
    public Toolbar toolbar;
    @BindView(R.id.coordinator)
    public CoordinatorLayout coordinatorLayout;
    @BindView(R.id.backdrop_frame)
    public FrameLayout backdropFrame;
    @BindView(R.id.rating_bar)
    RatingBar ratingBar;
    @Inject
    ViewModelProvider.Factory viewModelFactory;
    @Inject
    AppExecutor appExecutor;
    private FavouriteMovie movieDetails;
    private List<Review> movieReviews;
    private List<Trailer> movieTrailers;
    private int movieId;
    private ReviewsAdapter reviewsAdapter;
    private TrailersAdapter trailersAdapter;
    private GenreAdapter genreAdapter;
    private boolean addToFavourite = false;
    private DetailViewModel viewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        AndroidInjection.inject(this);
        Intent intent = getIntent();
        if (intent != null) {
            if (intent.hasExtra(MainActivity.KEY_SELECTED_MOVIE_ID_INTENT)) {
                movieId = intent.getIntExtra(MainActivity.KEY_SELECTED_MOVIE_ID_INTENT, 0);
            }
        }

        ButterKnife.bind(this);
        setupToolbar();
        setupRecyclerView();

        viewModel = ViewModelProviders.of(this, viewModelFactory).get(DetailViewModel.class);

        displayMovieInfo();
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (addToFavourite) {
                    fab.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.colorPrimary)));
                    viewModel.deleteSingleMovie(movieId);
                    addToFavourite = false;

                    Snackbar.make(view, R.string.remove_favourite, Snackbar.LENGTH_LONG)
                            .setAction(R.string.action, null).show();
                } else {
                    fab.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.colorAccent)));
                    viewModel.insertFavourite(movieDetails, movieReviews, movieTrailers);
                    addToFavourite = true;
                    Snackbar.make(view, getString(R.string.add_favourite), Snackbar.LENGTH_LONG)
                            .setAction(R.string.action, null).show();
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

    /**
     * Set up action bar and up button
     */
    private void setupToolbar() {
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
    }

    /**
     * Set up recycler views
     */
    private void setupRecyclerView() {
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
    }

    /**
     * Load movie information
     */
    private void displayMovieInfo() {
        viewModel.setMovieId(movieId);
        appExecutor.diskIO().execute(new Runnable() {
            @Override
            public void run() {
                if (viewModel.containMovieId(movieId)) {
                    addToFavourite = true;
                    SharedPreferenceHelper.setSharedPreference(PREF_IS_FAVOURITE_KEY, true);
                    fab.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.colorAccent)));
                } else {
                    SharedPreferenceHelper.setSharedPreference(PREF_IS_FAVOURITE_KEY, false);
                }
            }
        });

        viewModel.getMovieDetail().observe(this, new Observer<Resource<FavouriteMovie>>() {
            @Override
            public void onChanged(@Nullable Resource<FavouriteMovie> favouriteMovieResource) {
                if (favouriteMovieResource != null) {
                    switch (favouriteMovieResource.status) {
                        case SUCCESS:
                            if (favouriteMovieResource.data != null) {
                                movieDetails = favouriteMovieResource.data;
                                showMovieDetails(movieDetails);
                                showMovieData();
                            }
                            break;
                        case ERROR:
                            showErrorMessage();
                            break;
                        case LOADING:
                            showOnlyLoading();
                            break;
                    }
                }
            }
        });

        viewModel.getMovieReview().observe(this, new Observer<Resource<List<Review>>>() {
            @Override
            public void onChanged(@Nullable Resource<List<Review>> listResource) {
                if (listResource != null) {
                    switch (listResource.status) {
                        case SUCCESS:
                            movieReviews = listResource.data;
                            showMovieReview(movieReviews);
                            break;
                        case ERROR:
                            reviewCardView.setVisibility(View.GONE);
                            break;
                        case LOADING:
                            break;
                    }
                }
            }
        });

        viewModel.getMovieVideo().observe(this, new Observer<Resource<List<Trailer>>>() {
            @Override
            public void onChanged(@Nullable Resource<List<Trailer>> listResource) {
                if (listResource != null) {
                    switch (listResource.status) {
                        case SUCCESS:
                            movieTrailers = listResource.data;
                            showMovieVideo(movieTrailers);
                            break;
                        case ERROR:
                            trailerCardView.setVisibility(View.GONE);
                            break;
                        case LOADING:
                            break;
                    }
                }
            }
        });
    }


    /**
     * Load details of movie
     *
     * @param movieDetails details of movie
     */
    private void showMovieDetails(FavouriteMovie movieDetails) {
        collapsingToolbarLayout.setTitle(movieDetails.getTitle());
        String backdropUrl = BACKDROP_BASE_PATH + movieDetails.getBackdrop();
        Picasso.get().load(backdropUrl).fit().centerCrop().into(backdropImageView);
        if (!movieDetails.getPoster().equals("null")) {
            String posterUrl = POSTER_BASE_PATH + movieDetails.getPoster();
            Picasso.get()
                    .load(posterUrl)
                    .placeholder(R.drawable.poster_placeholder)
                    .error(R.drawable.no_pic)
                    .fit()
                    .centerCrop()
                    .into(posterImageView);
        }
        overviewTextView.setText(movieDetails.getOverview());
        userRatingTextView.setText(movieDetails.getUserRating());
        float userRating = Float.valueOf(movieDetails.getUserRating());
        ratingBar.setRating(userRating / 2);
        releaseDateTextView.setText(convertDateString(movieDetails.getReleaseDate()));
        runtimeTextView.setText(getString(R.string.runtime_value, movieDetails.getRuntime()));
        languageTextView.setText(movieDetails.getLanguage());
        List<Genre> genres = movieDetails.getGenres();
        genreAdapter.setGenres(genres);
    }

    /**
     * Load list of reviews
     *
     * @param movieReviews list of reviews
     */
    private void showMovieReview(List<Review> movieReviews) {
        if (movieReviews == null || movieReviews.size() == 0) {
            reviewCardView.setVisibility(View.GONE);
        } else {
            reviewCardView.setVisibility(View.VISIBLE);
            reviewsAdapter.setReviews(movieReviews);
        }
    }

    /**
     * Load list of videos
     *
     * @param movieVideos list of videos
     */
    private void showMovieVideo(List<Trailer> movieVideos) {
        if (movieVideos == null || movieVideos.size() == 0) {
            trailerCardView.setVisibility(View.GONE);
        } else {
            trailerCardView.setVisibility(View.VISIBLE);
            trailersAdapter.setTrailers(movieVideos);
        }
    }

    /**
     * Show data and hide error message
     */
    private void showMovieData() {
        loadingIndicator.setVisibility(View.INVISIBLE);
        errorMessage.setVisibility(View.INVISIBLE);
        movieContent.setVisibility(View.VISIBLE);
        backdropFrame.setVisibility(View.VISIBLE);
    }

    /**
     * Show error message and hide data
     */
    private void showErrorMessage() {
        loadingIndicator.setVisibility(View.INVISIBLE);
        errorMessage.setVisibility(View.VISIBLE);
        movieContent.setVisibility(View.INVISIBLE);
        backdropFrame.setVisibility(View.INVISIBLE);
    }

    /**
     * Hide data and error message and display loading indicator
     */
    private void showOnlyLoading() {
        errorMessage.setVisibility(View.INVISIBLE);
        movieContent.setVisibility(View.INVISIBLE);
        backdropFrame.setVisibility(View.INVISIBLE);
        loadingIndicator.setVisibility(View.VISIBLE);
    }

    /**
     * Change the date format
     *
     * @param dateString date in String type
     * @return Converted date string
     */
    private String convertDateString(String dateString) {
        Date date = null;
        try {
            date = new SimpleDateFormat("yyyy-MM-dd", Locale.US).parse(dateString);

        } catch (ParseException e) {
            e.printStackTrace();
        }
        DateFormat dateFormatter = DateFormat.getDateInstance(DateFormat.MEDIUM);
        return dateFormatter.format(date);
    }
}
