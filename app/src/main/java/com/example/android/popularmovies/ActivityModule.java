package com.example.android.popularmovies;

import com.example.android.popularmovies.ui.detail.DetailActivity;
import com.example.android.popularmovies.ui.main.MainActivity;

import dagger.Module;
import dagger.android.ContributesAndroidInjector;

@Module
public abstract class ActivityModule {

    @ContributesAndroidInjector
    abstract MainActivity contributeMainActivity();

    @ContributesAndroidInjector
    abstract DetailActivity contributeDetailActivity();
}
