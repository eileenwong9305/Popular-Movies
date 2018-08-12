package com.example.android.popularmovies.Utils;

import android.net.Uri;
import android.util.Log;

import com.example.android.popularmovies.BuildConfig;
import com.example.android.popularmovies.Data.Movie;
import com.example.android.popularmovies.Data.MovieList;
import com.example.android.popularmovies.Data.Review;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.net.InetSocketAddress;
import java.net.MalformedURLException;
import java.net.Socket;
import java.net.SocketAddress;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import javax.net.ssl.HttpsURLConnection;

/**
 * Utility function to communicate with themoviedb.org API and handle JSON data
 */
public class NetworkUtils {

    private static final String KEY_RESULTS = "results";
    private static final String KEY_TITLE = "title";
    private static final String KEY_POSTER_PATH = "poster_path";
    private static final String KEY_OVERVIEW = "overview";
    private static final String KEY_USER_RATING = "vote_average";
    private static final String KEY_RELEASE_DATE = "release_date";
    private static final String KEY_BACKDROP = "backdrop_path";
    private static final String KEY_MOVIE_ID = "id";
    private static final String KEY_GENRES = "genres";
    private static final String KEY_NAME = "name";
    private static final String KEY_RUNTIME = "runtime";
    private static final String KEY_ORIGINAL_LANGUAGE = "original_language";
    private static final String KEY_CREDITS = "credits";
    private static final String KEY_CASTS = "cast";
    private static final String KEY_CHARACTER = "character";
    private static final String KEY_PROFILE_PATH = "profile_path";

    private static final String KEY_VIDEOS = "videos";
    private static final String KEY_VIDEO_KEY = "key";


    private static final String KEY_REVIEWS = "reviews";
    private static final String KEY_REVIEW_AUTHOR = "author";
    private static final String KEY_REVIEW_CONTENT = "content";


    private static final String BASE_URL = "https://api.themoviedb.org/3/movie";
    private static final String API_KEY_PARAM = "api_key";
    private static final String RESPOND_PARAM = "append_to_response";
    private static final String DEFAULT_SORT_BY_PATH = "popular";
    private static final String RESPOND_VALUE = "videos";
    private static final String RESPOND_ADD_VALUE = ",images,credits,reviews";

    /*
        TODO: Replace [YOUR_API_KEY] with your API key requested from TheMovieDB website
     */
    private static final String API_KEY = BuildConfig.API_KEY;

    /**
     * Build url to communicate with themoviedb.org server using user-selected sort type.
     *
     * @param sortByPath path of the sort by type (most popular, highest rated)
     * @return URL to query themoviedb.org server
     */
    public static URL buildUrl(String sortByPath) {
        Uri buildUri = Uri.parse(BASE_URL).buildUpon()
                .appendPath(sortByPath)
                .appendQueryParameter(API_KEY_PARAM, API_KEY)
                .build();
        URL url = null;
        try {
            url = new URL(buildUri.toString());
        } catch (MalformedURLException ex) {
            ex.printStackTrace();
        }
        return url;
    }

    public static URL buildUrl(int movieId) {
        Uri buildUri = Uri.parse(BASE_URL).buildUpon()
                .appendPath(String.valueOf(movieId))
                .appendQueryParameter(API_KEY_PARAM, API_KEY)
                .appendQueryParameter(RESPOND_PARAM, RESPOND_VALUE)
                .build();
        URL url = null;
        try {
            url = new URL(buildUri.toString() + RESPOND_ADD_VALUE);
        } catch (MalformedURLException ex) {
            ex.printStackTrace();
        }
        return url;
    }

    /**
     * Parses JSON from web response and return List of Movie objects
     *
     * @param json JSON response from server
     * @return List of Movie object
     * @throws JSONException if JSON cannot be properly parsed
     */
    public static List<MovieList> parseMovieJson(String json) throws JSONException {

        JSONObject root = new JSONObject(json);
        JSONArray results = root.getJSONArray(KEY_RESULTS);

        List<MovieList> movies = new ArrayList<>();

        for (int i = 0; i < results.length(); i++) {
            JSONObject result = results.getJSONObject(i);
            String title = result.getString(KEY_TITLE);
            String poster = result.getString(KEY_POSTER_PATH);
            int movieId = result.getInt(KEY_MOVIE_ID);
            movies.add(new MovieList(title, poster, movieId));
        }
        return movies;
    }

    public static Movie parseMovieDetailJson(String json) throws JSONException {

        JSONObject root = new JSONObject(json);

        String title = root.getString(KEY_TITLE);
        String poster = root.getString(KEY_POSTER_PATH);
        String backdrop = root.getString(KEY_BACKDROP);
        String releaseDate = root.getString(KEY_RELEASE_DATE);
        if (releaseDate.equals("null")) {
            releaseDate = "TBD";
        } else {
            releaseDate = Movie.convertDateString(releaseDate);
        }
        String overview = root.getString(KEY_OVERVIEW);
        String userRating = root.getString(KEY_USER_RATING);
        String language = root.getString(KEY_ORIGINAL_LANGUAGE);
        int movieId = root.getInt(KEY_MOVIE_ID);

        JSONArray genreList = root.getJSONArray(KEY_GENRES);

        ArrayList<String> genres = new ArrayList<>();
        for (int i = 0; i < genreList.length(); i++) {
            JSONObject genre = genreList.getJSONObject(i);
            String genreName = genre.getString(KEY_NAME);
            genres.add(genreName);
        }

        String runtime = root.getString(KEY_RUNTIME);
        Log.e("RUNTIME", runtime);
        if (runtime.equals("null")) runtime = "-";

//        JSONObject credits = root.getJSONObject(KEY_CREDITS);
//        JSONArray castList = credits.getJSONArray(KEY_CASTS);
//        for (int i = 0; i < castList.length(); i++) {
//            JSONObject cast = castList.getJSONObject(i);
//            String character = cast.getString(KEY_CHARACTER);
//            String actor = cast.getString(KEY_NAME);
//            String profilePath = cast.getString(KEY_PROFILE_PATH);
//        }

        ArrayList<String> videoKeys = new ArrayList<>();
        JSONObject videosList = root.getJSONObject(KEY_VIDEOS);
        JSONArray videoResults = videosList.getJSONArray(KEY_RESULTS);
        for (int i = 0; i < videoResults.length(); i++) {
            JSONObject videoKey = videoResults.getJSONObject(i);
            videoKeys.add(videoKey.getString(KEY_VIDEO_KEY));
        }

        JSONObject reviewsList = root.getJSONObject(KEY_REVIEWS);
        JSONArray reviewResults = reviewsList.getJSONArray(KEY_RESULTS);
        ArrayList<Review> reviews = new ArrayList<>();
        for (int i = 0; i < reviewResults.length(); i++) {
            JSONObject review = reviewResults.getJSONObject(i);
            String author = review.getString(KEY_REVIEW_AUTHOR);
            String content = review.getString(KEY_REVIEW_CONTENT);
            reviews.add(new Review(author, content));
        }
        return new Movie(title, poster, overview, userRating, releaseDate, backdrop, movieId, genres, runtime, language,
                reviews, videoKeys);
    }

    /**
     * Returns result from HTTP response
     *
     * @param url URL to fetch HTTP response.
     * @return content of HTTP response.
     * @throws IOException related to network and stream reading
     */
    public static String getResponseFromHttp(URL url) throws IOException {
        HttpsURLConnection urlConnection = (HttpsURLConnection) url.openConnection();
        try {
            InputStream in = urlConnection.getInputStream();
            Scanner scanner = new Scanner(in);
            scanner.useDelimiter("\\A");
            boolean hasInput = scanner.hasNext();
            if (hasInput) {
                return scanner.next();
            } else {
                return null;
            }
        } finally {
            urlConnection.disconnect();
        }
    }

    /**
     * Check internet access
     * Reference: https://stackoverflow.com/questions/1560788/how-to-check-internet-access-on-android-inetaddress-never-times-out
     *
     * @return true if is online
     */
    public static boolean isOnline() {
        try {
            int timeoutMs = 1500;
            Socket sock = new Socket();
            SocketAddress sockaddr = new InetSocketAddress("8.8.8.8", 53);

            sock.connect(sockaddr, timeoutMs);
            sock.close();

            return true;
        } catch (IOException e) {
            return false;
        }
    }

    /**
     * Returns default path for sorting movie data.
     *
     * @return default path
     */
    public static URL getDefaultSortByPathUrl() {
        return buildUrl(DEFAULT_SORT_BY_PATH);
    }
}
