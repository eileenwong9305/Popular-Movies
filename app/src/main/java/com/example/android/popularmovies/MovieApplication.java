package com.example.android.popularmovies;

import android.app.Activity;
import android.app.Application;
import android.content.Context;

import com.example.android.popularmovies.di.DaggerAppComponent;

import javax.inject.Inject;

import dagger.android.AndroidInjector;
import dagger.android.DispatchingAndroidInjector;
import dagger.android.HasActivityInjector;

public class MovieApplication extends Application implements HasActivityInjector {

    private static Context context;
    @Inject
    DispatchingAndroidInjector<Activity> dispatchingAndroidInjector;

    public static Context getContext() {
        return context;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        DaggerAppComponent.builder().application(this).build().inject(this);
        context = getApplicationContext();
    }

    @Override
    public AndroidInjector<Activity> activityInjector() {
        return dispatchingAndroidInjector;
    }
}
