package com.example.android.popularmovies.di;

import android.app.Application;
import android.arch.persistence.room.Room;
import android.support.annotation.NonNull;

import com.example.android.popularmovies.BuildConfig;
import com.example.android.popularmovies.api.ApiInterface;
import com.example.android.popularmovies.database.MovieDao;
import com.example.android.popularmovies.database.MovieDatabase;
import com.example.android.popularmovies.repository.MovieRepository;
import com.example.android.popularmovies.utils.AppExecutor;
import com.example.android.popularmovies.utils.LiveDataCallAdapterFactory;

import java.io.IOException;

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
                .addCallAdapterFactory(new LiveDataCallAdapterFactory())
                .client(client)
                .build();
    }

    @Singleton
    @Provides
    ApiInterface provideApiInterface(Retrofit retrofit) {
        return retrofit.create(ApiInterface.class);
    }

    @Provides
    Interceptor provideApiKeyInterceptor() {
        return new Interceptor() {
            @Override
            public Response intercept(@NonNull Chain chain) throws IOException {
                Request original = chain.request();
                HttpUrl originalHttpUrl = original.url();
                HttpUrl newHttpUrl = originalHttpUrl.newBuilder()
                        .setQueryParameter(API_KEY_PARAM, BuildConfig.MOVIE_DB_API_KEY)
                        .build();
                Request request = original.newBuilder().url(newHttpUrl).build();
                return chain.proceed(request);
            }
        };
    }
}
