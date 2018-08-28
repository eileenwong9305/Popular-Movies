package com.example.android.popularmovies.Database;

import android.arch.persistence.room.TypeConverter;
import android.text.TextUtils;

import com.example.android.popularmovies.Data.Genre;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class GenreConverter {

    @TypeConverter
    public static String toListString(List<Genre> genres) {
        if (genres == null || genres.size() == 0) {
            return null;
        }
        List<String> genreListString = new ArrayList<>();
        for (Genre genre : genres) {
            genreListString.add(genre.getName());
        }
        return TextUtils.join(",", genreListString);
    }

    @TypeConverter
    public static List<Genre> toList(String genreString) {
        if (genreString == null || genreString.isEmpty()) {
            return null;
        }
        List<String> genreListString = Arrays.asList(TextUtils.split(genreString, ","));
        List<Genre> genres = new ArrayList<>();
        for (String name : genreListString) {
            genres.add(new Genre(name));
        }
        return genres;
    }
}
