package com.example.android.popularmovies.ui.main;

import android.arch.lifecycle.Observer;
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
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.example.android.popularmovies.Data.Movie;
import com.example.android.popularmovies.R;
import com.example.android.popularmovies.Utils.InjectorUtils;
import com.example.android.popularmovies.Utils.SharedPreferenceHelper;
import com.example.android.popularmovies.adapter.MoviesAdapter;
import com.example.android.popularmovies.ui.detail.DetailActivity;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MainActivity extends AppCompatActivity implements MoviesAdapter.GridItemListener {

    public static final String KEY_SELECTED_MOVIE_ID_INTENT = "selected_movie";
    private static final String BUNDLE_RECYCLER_LAYOUT = "recycler_layout";

    @BindView(R.id.pb_loading_indicator)
    public ProgressBar loadingIndicator;
    @BindView(R.id.tv_error_message)
    public TextView errorMessageTextView;
    @BindView(R.id.rv_movie)
    public RecyclerView recyclerView;
    @BindView(R.id.layout_error)
    public LinearLayout errorLayout;

    private MoviesAdapter adapter;
    private MainViewModel viewModel;
    private int mPosition = RecyclerView.NO_POSITION;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ButterKnife.bind(this);
        setupRecyclerView();

        if (savedInstanceState != null) {
            mPosition = savedInstanceState.getInt(BUNDLE_RECYCLER_LAYOUT);
        }

        MainViewModelFactory factory = InjectorUtils.provideMainViewModelFactory(this.getApplicationContext());
        viewModel = ViewModelProviders.of(this, factory).get(MainViewModel.class);
        String sortOrder = SharedPreferenceHelper.getSortPreferenceValue(this, getString(R.string.pref_sort_key));
        loadMovies(sortOrder);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        int lastFirstVisiblePosition = ((GridLayoutManager) recyclerView.getLayoutManager()).findFirstCompletelyVisibleItemPosition();
        outState.putInt(BUNDLE_RECYCLER_LAYOUT, lastFirstVisiblePosition);
    }

    @Override
    public void onClick(Movie movie) {
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
        loadMovies(sortOrder);
    }

    /**
     * Setup recyclerview and layoutManager based on the orientation of device
     */
    private void setupRecyclerView() {
        GridLayoutManager layoutManager;
        if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT) {
            layoutManager = new GridLayoutManager(this, 2);
        } else {
            layoutManager = new GridLayoutManager(this, 4);
        }
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setHasFixedSize(true);
        adapter = new MoviesAdapter(this);
        recyclerView.setAdapter(adapter);
    }

    /**
     * Load movie list based on selected sort order preference.
     *
     * @param sortOrder
     */
    private void loadMovies(final String sortOrder) {
        showOnlyLoading();

        if (sortOrder.equals(getString(R.string.pref_sort_favourites))) {
            viewModel.getFavouriteMovieData().observe(this, new Observer<List<Movie>>() {
                @Override
                public void onChanged(@Nullable List<Movie> movieLists) {
                    String currentSort = SharedPreferenceHelper.getSortPreferenceValue(MainActivity.this,
                            getString(R.string.pref_sort_key));
                    if (currentSort.equals(getString(R.string.pref_sort_favourites))) {
                        adapter.setMovies(movieLists);
                        if (mPosition == RecyclerView.NO_POSITION) mPosition = 0;
                        recyclerView.scrollToPosition(mPosition);
                        displayUI(movieLists);
                    }
                }
            });
        } else {
            viewModel.getOtherMovieData(sortOrder).observe(this, new Observer<List<Movie>>() {
                @Override
                public void onChanged(@Nullable List<Movie> movieLists) {
                    adapter.setMovies(movieLists);
                    if (mPosition == RecyclerView.NO_POSITION) mPosition = 0;
                    recyclerView.scrollToPosition(mPosition);
                    displayUI(movieLists);
                }
            });
        }
    }

    /**
     * Display data based on availability of movie data
     *
     * @param movieLists list of movies
     */
    private void displayUI(List<Movie> movieLists) {
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
        recyclerView.setVisibility(View.VISIBLE);
    }

    /**
     * Show error message and hide data
     */
    private void showErrorMessage() {
        loadingIndicator.setVisibility(View.INVISIBLE);
        errorLayout.setVisibility(View.VISIBLE);
        recyclerView.setVisibility(View.INVISIBLE);
    }

    /**
     * Hide data and error message and display loading indicator
     */
    private void showOnlyLoading() {
        errorLayout.setVisibility(View.INVISIBLE);
        recyclerView.setVisibility(View.INVISIBLE);
        loadingIndicator.setVisibility(View.VISIBLE);
    }
}
