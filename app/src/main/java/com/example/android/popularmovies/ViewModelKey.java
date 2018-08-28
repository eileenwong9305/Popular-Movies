package com.example.android.popularmovies;

import android.arch.lifecycle.ViewModel;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import dagger.MapKey;

/**
 * Reference: https://github.com/googlesamples/android-architecture-components/blob/76efb3e5c18f964327b77a17a5ff1b10a7e063e7/GithubBrowserSample/app/src/main/java/com/android/example/github/di/ViewModelKey.java
 */
@Documented
@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@MapKey
@interface ViewModelKey {
    Class<? extends ViewModel> value();
}