package com.example.android.popularmovies.Database;

import android.arch.persistence.room.TypeConverter;
import android.text.TextUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class GenreConverter {

    @TypeConverter
    public static String toListString(List<String> genres) {
        if (genres == null || genres.size() == 0) {
            return null;
        } else {
            return TextUtils.join(",", genres);
        }
    }

    @TypeConverter
    public static List<String> toList(String genreString) {
        if (genreString == null || genreString.isEmpty()) {
            return null;
        } else {
            return Arrays.asList(TextUtils.split(genreString, ","));
        }
    }
}
