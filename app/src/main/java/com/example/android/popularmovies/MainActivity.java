package com.example.android.popularmovies;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.example.android.popularmovies.Utils.Movie;
import com.example.android.popularmovies.Utils.MoviesAdapter;
import com.example.android.popularmovies.Utils.NetworkUtils;

import java.net.URL;
import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MainActivity extends AppCompatActivity implements MoviesAdapter.GridItemListener {

    private static final String LOG_TAG = MainActivity.class.getSimpleName();

    private static final int SPAN_COUNT = 2;
    private static final String KEY_PARCEL_INTENT = "selected_movie";
    private static final String KEY_PARCEL_MOVIE_LIST = "movies_list";

    @BindView(R.id.pb_loading_indicator) ProgressBar loadingIndicator;
    @BindView(R.id.tv_error_message) TextView errorMessageTextView;
    @BindView(R.id.rv_movie) RecyclerView recyclerView;

    private MoviesAdapter adapter;
    private ArrayList<Movie> movieList;
    private String sortByPath;
    private URL parseUrl;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ButterKnife.bind(this);

        GridLayoutManager layoutManager = new GridLayoutManager(this, SPAN_COUNT);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setHasFixedSize(true);
        adapter = new MoviesAdapter(this);
        recyclerView.setAdapter(adapter);

        if (sortByPath == null) {
            parseUrl = NetworkUtils.getDefaultSortByPathUrl();
        }

        if (savedInstanceState == null || !savedInstanceState.containsKey(KEY_PARCEL_MOVIE_LIST)) {
            new FetchMovieTask().execute(parseUrl);
        } else {
            movieList = savedInstanceState.getParcelableArrayList(KEY_PARCEL_MOVIE_LIST);
            adapter.setMovies(movieList);
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putParcelableArrayList(KEY_PARCEL_MOVIE_LIST, movieList);
        super.onSaveInstanceState(outState);
    }

    @Override
    public void onClick(Movie movie) {
        Intent intent = new Intent(MainActivity.this, DetailActivity.class);
        intent.putExtra(KEY_PARCEL_INTENT, movie);
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
            new FetchMovieTask().execute(parseUrl);
            return true;
        }
        if (!item.isChecked()) {
            item.setChecked(true);
            switch (id) {
                case R.id.sort_by_top_rated:
                    sortByPath = "top_rated";
                    parseUrl = NetworkUtils.buildUrl(sortByPath);
                    new FetchMovieTask().execute(parseUrl);
                    return true;
                case R.id.sort_by_popularity:
                    sortByPath = "popular";
                    parseUrl = NetworkUtils.buildUrl(sortByPath);
                    new FetchMovieTask().execute(parseUrl);
                    return true;
                default:
                    return super.onOptionsItemSelected(item);
            }
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * Show data and hide error message
     */
    private void showMovieData() {
        errorMessageTextView.setVisibility(View.INVISIBLE);
        recyclerView.setVisibility(View.VISIBLE);
    }

    /**
     * Show error message and hide data
     */
    private void showErrorMessage() {
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

    public class FetchMovieTask extends AsyncTask<URL, Void, ArrayList<Movie>> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            showOnlyLoading();
        }

        @Override
        protected ArrayList<Movie> doInBackground(URL... urls) {
            if (urls.length == 0) {
                return null;
            }
            URL parseUrl = urls[0];
            if (NetworkUtils.isOnline()) {
                try {
                    String json = NetworkUtils.getResponseFromHttp(parseUrl);
                    return NetworkUtils.parseMovieJson(json);
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
            return null;
        }

        @Override
        protected void onPostExecute(ArrayList<Movie> movies) {
            loadingIndicator.setVisibility(View.INVISIBLE);
            if (movies != null) {
                movieList = movies;
                adapter.setMovies(movies);
                showMovieData();
            } else {
                showErrorMessage();
            }
        }
    }
}
