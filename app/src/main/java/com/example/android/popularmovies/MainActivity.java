package com.example.android.popularmovies;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MediatorLiveData;
import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.preference.PreferenceManager;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.example.android.popularmovies.Data.Movie;
import com.example.android.popularmovies.Data.MovieList;
import com.example.android.popularmovies.Database.FavouriteDatabase;
import com.example.android.popularmovies.Utils.AppExecutor;
import com.example.android.popularmovies.Utils.InjectorUtils;
import com.example.android.popularmovies.Utils.MovieNetworkDataSource;
import com.example.android.popularmovies.Utils.MoviesAdapter;
import com.example.android.popularmovies.Utils.NetworkUtils;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MainActivity extends AppCompatActivity implements MoviesAdapter.GridItemListener,
        SharedPreferences.OnSharedPreferenceChangeListener{

    private static final String LOG_TAG = MainActivity.class.getSimpleName();

    private static final int SPAN_COUNT = 2;
    public static final String KEY_SELECTED_MOVIE_ID_INTENT = "selected_movie";
    private static final String KEY_PARCEL_MOVIE_LIST = "movies_list";

    @BindView(R.id.pb_loading_indicator) ProgressBar loadingIndicator;
    @BindView(R.id.tv_error_message) TextView errorMessageTextView;
    @BindView(R.id.rv_movie) RecyclerView recyclerView;

    private MoviesAdapter adapter;
    private ArrayList<Movie> movieList;
    private SharedPreferences mSharedPreferences;

    private MainViewModel viewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ButterKnife.bind(this);
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

        showOnlyLoading();

        mSharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        mSharedPreferences.registerOnSharedPreferenceChangeListener(this);

        MainViewModelFactory factory = InjectorUtils.provideMainViewModelFactory(this.getApplicationContext());
        viewModel = ViewModelProviders.of(this, factory).get(MainViewModel.class);

//        if (savedInstanceState != null && savedInstanceState.containsKey(KEY_PARCEL_MOVIE_LIST)) {
////            movieList = savedInstanceState.getParcelableArrayList(KEY_PARCEL_MOVIE_LIST);
////            adapter.setMovies(movieList);
////        } else {
////            populateUI();
////        }

        loadMovies(mSharedPreferences);
    }

//    @Override
//    protected void onSaveInstanceState(Bundle outState) {
//        outState.putParcelableArrayList(KEY_PARCEL_MOVIE_LIST, movieList);
//        super.onSaveInstanceState(outState);
//    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        PreferenceManager.getDefaultSharedPreferences(this).unregisterOnSharedPreferenceChangeListener(this);
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
        if (id == R.id.action_refresh) {
            loadMovies(mSharedPreferences);
            return true;
        } else if (id == R.id.action_setting) {
            Intent intent = new Intent(MainActivity.this, SettingActivity.class);
            startActivity(intent);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * Show data and hide error message
     */
    private void showMovieData() {
        loadingIndicator.setVisibility(View.INVISIBLE);
        errorMessageTextView.setVisibility(View.INVISIBLE);
        recyclerView.setVisibility(View.VISIBLE);
    }

    /**
     * Show error message and hide data
     */
    private void showErrorMessage() {
        loadingIndicator.setVisibility(View.INVISIBLE);
        errorMessageTextView.setVisibility(View.VISIBLE);
        recyclerView.setVisibility(View.INVISIBLE);
    }

    /**
     * Hide data and error message and display loading indicator
     */
    private void showOnlyLoading() {
        errorMessageTextView.setVisibility(View.INVISIBLE);
        recyclerView.setVisibility(View.INVISIBLE);
        loadingIndicator.setVisibility(View.VISIBLE);
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String s) {
        loadMovies(sharedPreferences);
    }

    private void loadMovies(SharedPreferences sharedPreferences) {
        String sortOrder = sharedPreferences.getString(
                getString(R.string.pref_sort_key),
                getString(R.string.pref_sort_popular));
        viewModel.getMovies(sortOrder).observe(this, new Observer<List<MovieList>>() {
            @Override
            public void onChanged(@Nullable List<MovieList> movieLists) {
                adapter.setMovies(movieLists);
                if (movieLists.size() != 0) {
                    showMovieData();
                } else {
                    showErrorMessage();
                }
            }
        });
    }
}
