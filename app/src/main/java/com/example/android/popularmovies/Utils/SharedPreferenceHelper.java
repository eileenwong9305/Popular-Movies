package com.example.android.popularmovies.Utils;

import android.content.Context;
import android.content.SharedPreferences;

import com.example.android.popularmovies.R;

public class SharedPreferenceHelper {

    public static void setSharedPreference(Context context, String key, String value) {
        SharedPreferences sortPreference = context.getSharedPreferences(
                context.getString(R.string.preference_key_file),
                Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sortPreference.edit();
        editor.putString(key, value);
        editor.apply();
    }

    public static String getSortPreferenceValue(Context context, String key) {
        SharedPreferences sortPreference = context.getSharedPreferences(
                context.getString(R.string.preference_key_file),
                Context.MODE_PRIVATE);
        return sortPreference.getString(key, context.getString(R.string.pref_sort_default));
    }
}
