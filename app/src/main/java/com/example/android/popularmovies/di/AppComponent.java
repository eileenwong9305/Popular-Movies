package com.example.android.popularmovies.di;

import android.app.Application;

import com.example.android.popularmovies.MovieApplication;

import javax.inject.Singleton;

import dagger.BindsInstance;
import dagger.Component;

@Singleton
@Component(modules = {AppModule.class, ActivityModule.class})
public interface AppComponent {

    void inject(MovieApplication movieApplication);

    @Component.Builder
    interface Builder {
        AppComponent build();

        @BindsInstance
        Builder application(Application application);
    }
}
