package com.example.android.popularmovies.ui.main;

import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProvider;
import android.arch.lifecycle.ViewModelProviders;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.example.android.popularmovies.Data.MovieList;
import com.example.android.popularmovies.GridItemClickListener;
import com.example.android.popularmovies.MovieRepository;
import com.example.android.popularmovies.R;
import com.example.android.popularmovies.Utils.AppExecutor;
import com.example.android.popularmovies.Utils.NetworkUtils;
import com.example.android.popularmovies.Utils.SharedPreferenceHelper;
import com.example.android.popularmovies.adapter.FavMoviesAdapter;
import com.example.android.popularmovies.adapter.MoviesAdapter;
import com.example.android.popularmovies.ui.detail.DetailActivity;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import dagger.android.AndroidInjection;

public class MainActivity extends AppCompatActivity implements GridItemClickListener {

    public static final String KEY_SELECTED_MOVIE_ID_INTENT = "selected_movie";
    private static final String BUNDLE_RECYCLER_LAYOUT = "recycler_layout";

    @BindView(R.id.pb_loading_indicator)
    public ProgressBar loadingIndicator;
    @BindView(R.id.layout_movie)
    public FrameLayout movieLayout;
    @BindView(R.id.layout_network_error)
    public LinearLayout errorLayout;
    @BindView(R.id.tv_error_message)
    public TextView errorMessageTextView;
    @BindView(R.id.rv_movie)
    public RecyclerView movieRecyclerView;
    @BindView(R.id.layout_fav_movie)
    public FrameLayout favMovieLayout;
    @BindView(R.id.tv_empty_list_message)
    public TextView emptyListMessageTextView;
    @BindView(R.id.rv_fav_movie)
    public RecyclerView favMovieRecyclerView;


    private MoviesAdapter moviesAdapter;
    private FavMoviesAdapter favMovieAdapter;

    private int mPosition = RecyclerView.NO_POSITION;

    @Inject
    MovieRepository movieRepository;
    @Inject
    ViewModelProvider.Factory viewModelFactory;
    private MainViewModel viewModel;

    @Inject
    AppExecutor appExecutor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        AndroidInjection.inject(this);
        ButterKnife.bind(this);
        setupRecyclerView();
        loadingIndicator.setVisibility(View.VISIBLE);


//        if (savedInstanceState != null) {
//            mPosition = savedInstanceState.getInt(BUNDLE_RECYCLER_LAYOUT);
//        }

        viewModel = ViewModelProviders.of(this, viewModelFactory).get(MainViewModel.class);
        String sortOrder = SharedPreferenceHelper.getSortPreferenceValue(this, getString(R.string.pref_sort_key));
//        viewModel.setPreference(sortOrder);
//        viewModel.getMovieList(sortOrder).observe(this, new Observer<List<MovieList>>() {
//            @Override
//            public void onChanged(@Nullable List<MovieList> movieLists) {
//                Log.e(MainActivity.class.getSimpleName(), "Change data");
//                showOnlyLoading();
//                moviesAdapter.setMovies(movieLists);
////                if (mPosition == RecyclerView.NO_POSITION) mPosition = 0;
////                movieRecyclerView.scrollToPosition(mPosition);
//                displayUI(movieLists);
//            }
//        });
        loadMovies(sortOrder);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        int lastFirstVisiblePosition = ((GridLayoutManager) movieRecyclerView.getLayoutManager()).findFirstCompletelyVisibleItemPosition();
        outState.putInt(BUNDLE_RECYCLER_LAYOUT, lastFirstVisiblePosition);
    }

    @Override
    public void onClick(MovieList movie) {
        Intent intent = new Intent(MainActivity.this, DetailActivity.class);
        intent.putExtra(KEY_SELECTED_MOVIE_ID_INTENT, movie.getMovieId());
        startActivity(intent);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_sort) {
            final String sortKey = getString(R.string.pref_sort_key);
            final ArrayList<String> itemValue = new ArrayList<>(Arrays.asList(getResources().getStringArray(R.array.pref_sort_values)));
            int selectedPosition = itemValue.indexOf(SharedPreferenceHelper.getSortPreferenceValue(this, sortKey));
            AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
            builder.setTitle("Sort Order");
            builder.setSingleChoiceItems(getResources().getStringArray(R.array.pref_sort_options),
                    selectedPosition,
                    new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            SharedPreferenceHelper.setSharedPreference(MainActivity.this, sortKey, itemValue.get(i));

//                            viewModel.getMovieList(itemValue.get(i)).observe(MainActivity.this, new Observer<List<MovieList>>() {
//                                @Override
//                                public void onChanged(@Nullable List<MovieList> movieLists) {
//                                    Log.e("Main", "show loading" );
//                                    showOnlyLoading();
//                                    moviesAdapter.setMovies(movieLists);
//
//                                    displayUI(movieLists);
//                                }
//                            });
                            loadMovies(itemValue.get(i));
                            dialogInterface.dismiss();
                        }
                    });
            AlertDialog dialog = builder.create();
            dialog.show();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * Refresh and load movie list
     *
     * @param v view
     */
    public void refreshView(View v) {
        String sortOrder = SharedPreferenceHelper.getSortPreferenceValue(this, getString(R.string.pref_sort_key));
//        viewModel.setPreference(sortOrder);
        loadMovies(sortOrder);
    }

    /**
     * Setup recyclerview and layoutManager based on the orientation of device
     */
    private void setupRecyclerView() {
        int spanCount;
        if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT) {
            spanCount = 2;
        } else {
            spanCount = 4;
        }
        GridLayoutManager movieLayoutManager = new GridLayoutManager(this, spanCount);
        movieRecyclerView.setLayoutManager(movieLayoutManager);
        movieRecyclerView.setHasFixedSize(true);
        moviesAdapter = new MoviesAdapter(this);
        movieRecyclerView.setAdapter(moviesAdapter);

        GridLayoutManager favMovieLayoutManager = new GridLayoutManager(this, spanCount);
        favMovieRecyclerView.setLayoutManager(favMovieLayoutManager);
        favMovieRecyclerView.setHasFixedSize(true);
        favMovieAdapter = new FavMoviesAdapter(this);
        favMovieRecyclerView.setAdapter(favMovieAdapter);
    }

    /**
     * Display data based on availability of movie data
     *
     * @param movieLists list of movies
     */
    private void displayUI(List<MovieList> movieLists) {
        if (movieLists != null && movieLists.size() != 0) {
            showMovieData();
        } else {
            showErrorMessage();
        }
    }

    /**
     * Show data and hide error message
     */
    private void showMovieData() {
        loadingIndicator.setVisibility(View.INVISIBLE);
        errorLayout.setVisibility(View.INVISIBLE);
    }

    /**
     * Show error message and hide data
     */
    private void showErrorMessage() {
        loadingIndicator.setVisibility(View.INVISIBLE);
        errorLayout.setVisibility(View.VISIBLE);
    }

    /**
     * Hide data and error message and display loading indicator
     */
    private void showOnlyLoading() {
        errorLayout.setVisibility(View.INVISIBLE);
        loadingIndicator.setVisibility(View.VISIBLE);
    }

    private void loadMovies(final String sortOrder) {
        loadingIndicator.setVisibility(View.VISIBLE);

        if (sortOrder.equals(getString(R.string.pref_sort_favourites))) {
            favMovieLayout.setVisibility(View.VISIBLE);
            movieLayout.setVisibility(View.GONE);
            viewModel.getFavMovieList().observe(this, new Observer<List<MovieList>>() {
                @Override
                public void onChanged(@Nullable List<MovieList> movieLists) {
                    Log.e(MainActivity.class.getSimpleName(), "Fav Change data");
                    favMovieAdapter.setMovies(movieLists);
                    if (movieLists != null && movieLists.size() != 0) {
                        favMovieRecyclerView.setVisibility(View.VISIBLE);
                        loadingIndicator.setVisibility(View.GONE);
                        emptyListMessageTextView.setVisibility(View.GONE);
                    } else {
                        favMovieRecyclerView.setVisibility(View.GONE);
                        loadingIndicator.setVisibility(View.GONE);
                        emptyListMessageTextView.setVisibility(View.VISIBLE);
                    }
                }
            });

        } else {
            favMovieLayout.setVisibility(View.GONE);
            movieLayout.setVisibility(View.VISIBLE);
            appExecutor.networkIO().execute(new Runnable() {
                @Override
                public void run() {
                    if (NetworkUtils.isOnline()){
                        favMovieRecyclerView.setVisibility(View.GONE);
                        loadingIndicator.setVisibility(View.GONE);
                        emptyListMessageTextView.setVisibility(View.VISIBLE);
                    } else {
                        viewModel.getMovieList(sortOrder).observe(MainActivity.this, new Observer<List<MovieList>>() {
                            @Override
                            public void onChanged(@Nullable List<MovieList> movieLists) {
                                Log.e(MainActivity.class.getSimpleName(), "other Change data");
                                moviesAdapter.setMovies(movieLists);
                                movieRecyclerView.setVisibility(View.VISIBLE);
                                loadingIndicator.setVisibility(View.GONE);
                                errorLayout.setVisibility(View.GONE);
                            }
                        });
                    }
                }
            });


        }

    }
}
