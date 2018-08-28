package com.example.android.popularmovies;

import android.app.Application;
import android.arch.persistence.room.Room;
import android.support.annotation.NonNull;

import com.example.android.popularmovies.Database.MovieDao;
import com.example.android.popularmovies.Database.MovieDatabase;
import com.example.android.popularmovies.Utils.AppExecutor;
import com.example.android.popularmovies.ui.main.MainViewModelFactory;
import com.google.gson.GsonBuilder;

import java.io.IOException;

import javax.inject.Inject;
import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import okhttp3.HttpUrl;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

@Module(includes = ViewModelModule.class)
class AppModule {

    private static final String BASE_URL = "https://api.themoviedb.org/3/movie/";
    private static final String API_KEY_PARAM = "api_key";
    private static final String API_KEY = BuildConfig.API_KEY;

    @Singleton
    @Provides
    MovieRepository provideMovieRepository(ApiInterface apiInterface, MovieDao dao, AppExecutor executor) {
        return new MovieRepository(apiInterface, dao, executor);
    }

    @Singleton
    @Provides
    AppExecutor provideAppExecutor() {
        return new AppExecutor();
    }

    @Singleton
    @Provides
    MovieDatabase provideMovieDatabase(Application application) {
        return Room.databaseBuilder(application, MovieDatabase.class, MovieDatabase.DATABASE_NAME)
                .build();
    }

    @Singleton
    @Provides
    MovieDao provideMovieDao(MovieDatabase db) {
        return db.movieDao();
    }


    @Provides
    OkHttpClient provideOkHttpClient(Interceptor apiKeyInterceptor) {
        return new OkHttpClient().newBuilder().addInterceptor(apiKeyInterceptor).build();
    }


    @Provides
    Retrofit provideRetrofit(OkHttpClient client) {
        return new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .client(client)
                .build();
    }

    @Singleton
    @Provides
    ApiInterface provideApiInterface(Retrofit retrofit) {
        return retrofit.create(ApiInterface.class);
    }

    @Singleton
    @Provides
    MainViewModelFactory provideMainViewModelFactory(MovieRepository movieRepository) {
        return new MainViewModelFactory(movieRepository);

    }

    @Provides
    Interceptor provideApiKeyInterceptor() {
        return new Interceptor() {
            @Override
            public Response intercept(@NonNull Chain chain) throws IOException {
                Request original = chain.request();
                HttpUrl originalHttpUrl = original.url();
                HttpUrl newHttpUrl = originalHttpUrl.newBuilder()
                        .setQueryParameter(API_KEY_PARAM, API_KEY)
                        .build();
                Request request = original.newBuilder().url(newHttpUrl).build();
                return chain.proceed(request);
            }
        };
    }
}
