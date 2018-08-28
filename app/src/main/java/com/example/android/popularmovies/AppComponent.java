package com.example.android.popularmovies;

import android.app.Application;

import com.example.android.popularmovies.ui.detail.DetailActivity;
import com.example.android.popularmovies.ui.main.MainActivity;

import javax.inject.Singleton;

import dagger.BindsInstance;
import dagger.Component;

@Singleton
@Component(modules = {AppModule.class, ActivityModule.class})
public interface AppComponent {

    @Component.Builder
    interface Builder {
        AppComponent build();
        @BindsInstance Builder application(Application application);
    }

    void inject(MovieApplication movieApplication);
}
