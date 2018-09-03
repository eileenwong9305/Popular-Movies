package com.example.android.popularmovies.utils;

import android.content.Context;
import android.content.SharedPreferences;

import com.example.android.popularmovies.MovieApplication;
import com.example.android.popularmovies.R;

public class SharedPreferenceHelper {

    public static void setSharedPreference(String key, String value) {
        Context context = MovieApplication.getContext();
        SharedPreferences sortPreference = context.getSharedPreferences(
                context.getString(R.string.preference_key_file),
                Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sortPreference.edit();
        editor.putString(key, value);
        editor.apply();
    }

    public static void setSharedPreference(String key, long value) {
        Context context = MovieApplication.getContext();
        SharedPreferences sortPreference = context.getSharedPreferences(
                context.getString(R.string.preference_key_file),
                Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sortPreference.edit();
        editor.putLong(key, value);
        editor.apply();
    }

    public static void setSharedPreference(String key, boolean value) {
        Context context = MovieApplication.getContext();
        SharedPreferences sortPreference = context.getSharedPreferences(
                context.getString(R.string.preference_key_file),
                Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sortPreference.edit();
        editor.putBoolean(key, value);
        editor.apply();
    }

    public static String getSortPreferenceValue(String key, String defaultValue) {
        Context context = MovieApplication.getContext();
        SharedPreferences sortPreference = context.getSharedPreferences(
                context.getString(R.string.preference_key_file),
                Context.MODE_PRIVATE);
        return sortPreference.getString(key, defaultValue);
    }

    public static long getSortPreferenceValue(String key, long defaultValue) {
        Context context = MovieApplication.getContext();
        SharedPreferences sortPreference = context.getSharedPreferences(
                context.getString(R.string.preference_key_file),
                Context.MODE_PRIVATE);
        return sortPreference.getLong(key, defaultValue);
    }

    public static boolean getSortPreferenceValue(String key, boolean defaultValue) {
        Context context = MovieApplication.getContext();
        SharedPreferences sortPreference = context.getSharedPreferences(
                context.getString(R.string.preference_key_file),
                Context.MODE_PRIVATE);
        return sortPreference.getBoolean(key, defaultValue);
    }
}
