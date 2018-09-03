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
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.example.android.popularmovies.R;
import com.example.android.popularmovies.adapter.FavMoviesAdapter;
import com.example.android.popularmovies.adapter.MoviesAdapter;
import com.example.android.popularmovies.data.Movie;
import com.example.android.popularmovies.data.Resource;
import com.example.android.popularmovies.repository.MovieRepository;
import com.example.android.popularmovies.ui.detail.DetailActivity;
import com.example.android.popularmovies.utils.GridItemClickListener;
import com.example.android.popularmovies.utils.SharedPreferenceHelper;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import dagger.android.AndroidInjection;

public class MainActivity extends AppCompatActivity implements GridItemClickListener {

    public static final String KEY_SELECTED_MOVIE_ID_INTENT = "selected_movie_id";

    @BindView(R.id.pb_loading_indicator)
    public ProgressBar loadingIndicator;
    @BindView(R.id.layout_network_error)
    public LinearLayout errorLayout;
    @BindView(R.id.tv_error_message)
    public TextView errorMessageTextView;
    @BindView(R.id.rv_movie)
    public RecyclerView movieRecyclerView;
    @BindView(R.id.tv_empty_list_message)
    public TextView emptyListMessageTextView;
    @BindView(R.id.rv_fav_movie)
    public RecyclerView favMovieRecyclerView;
    @Inject
    MovieRepository movieRepository;
    @Inject
    ViewModelProvider.Factory viewModelFactory;
    private MoviesAdapter moviesAdapter;
    private FavMoviesAdapter favMovieAdapter;
    private int mPosition = RecyclerView.NO_POSITION;
    private MainViewModel viewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        AndroidInjection.inject(this);
        ButterKnife.bind(this);
        setupRecyclerView();
        showOnlyLoading();

        viewModel = ViewModelProviders.of(this, viewModelFactory).get(MainViewModel.class);
        String sortOrder = SharedPreferenceHelper.getSortPreferenceValue(
                getString(R.string.pref_sort_key),
                getString(R.string.pref_sort_default));
        loadMovies(sortOrder);
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
            int selectedPosition = itemValue.indexOf(SharedPreferenceHelper.getSortPreferenceValue(
                    sortKey, getString(R.string.pref_sort_default)));
            AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
            builder.setTitle("Sort Order");
            builder.setSingleChoiceItems(getResources().getStringArray(R.array.pref_sort_options),
                    selectedPosition,
                    new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            SharedPreferenceHelper.setSharedPreference(sortKey, itemValue.get(i));
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
        String sortOrder = SharedPreferenceHelper.getSortPreferenceValue(
                getString(R.string.pref_sort_key),
                getString(R.string.pref_sort_default));
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


    private void showMovie() {
        movieRecyclerView.setVisibility(View.VISIBLE);
        favMovieRecyclerView.setVisibility(View.GONE);
        loadingIndicator.setVisibility(View.GONE);
        emptyListMessageTextView.setVisibility(View.GONE);
        errorLayout.setVisibility(View.GONE);
    }

    private void showFavMovie() {
        loadingIndicator.setVisibility(View.GONE);
        emptyListMessageTextView.setVisibility(View.GONE);
        errorLayout.setVisibility(View.GONE);
    }

    private void showNoNetworkMessage() {
        movieRecyclerView.setVisibility(View.GONE);
        favMovieRecyclerView.setVisibility(View.GONE);
        loadingIndicator.setVisibility(View.GONE);
        emptyListMessageTextView.setVisibility(View.GONE);
        errorLayout.setVisibility(View.VISIBLE);
    }

    private void showEmptyListMessage() {
        movieRecyclerView.setVisibility(View.GONE);
        favMovieRecyclerView.setVisibility(View.GONE);
        loadingIndicator.setVisibility(View.GONE);
        emptyListMessageTextView.setVisibility(View.VISIBLE);
        errorLayout.setVisibility(View.GONE);
    }

    private void showOnlyLoading() {
        movieRecyclerView.setVisibility(View.GONE);
        favMovieRecyclerView.setVisibility(View.GONE);
        loadingIndicator.setVisibility(View.VISIBLE);
        emptyListMessageTextView.setVisibility(View.GONE);
        errorLayout.setVisibility(View.GONE);
    }

    private void loadMovies(final String sortOrder) {
        loadingIndicator.setVisibility(View.VISIBLE);
        if (sortOrder.equals(getString(R.string.pref_sort_favourites))) {
            movieRecyclerView.setVisibility(View.GONE);
            favMovieRecyclerView.setVisibility(View.VISIBLE);
            viewModel.getFavMovieList().observe(this, new Observer<List<Movie>>() {
                @Override
                public void onChanged(@Nullable List<Movie> movies) {
                    favMovieAdapter.setMovies(movies);
                    if (movies != null && movies.size() != 0) {
                        showFavMovie();
                    } else {
                        showEmptyListMessage();
                    }
                }
            });

        } else {
            viewModel.setSortOrder(sortOrder);
            viewModel.getMovieList().observe(this, new Observer<Resource<List<Movie>>>() {
                @Override
                public void onChanged(@Nullable Resource<List<Movie>> listResource) {
                    if (listResource != null) {
                        switch (listResource.status) {
                            case SUCCESS:
                                moviesAdapter.setMovies(listResource.data);
                                showMovie();
                                break;
                            case ERROR:
                                showNoNetworkMessage();
                                break;
                            case LOADING:
                                showOnlyLoading();
                                break;
                        }
                    }
                }
            });

        }
    }
}
