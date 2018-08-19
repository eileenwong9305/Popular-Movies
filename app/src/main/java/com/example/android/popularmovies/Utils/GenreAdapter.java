package com.example.android.popularmovies.Utils;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.android.popularmovies.R;

import java.util.ArrayList;
import java.util.List;

public class GenreAdapter extends RecyclerView.Adapter<GenreAdapter.GenreViewHolder>{

    private List<String> genres;


    @NonNull
    @Override
    public GenreViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        View view = LayoutInflater.from(context).inflate(R.layout.genre_list, parent, false);
        return new GenreViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull GenreViewHolder holder, int position) {
        holder.bind(position);
    }

    @Override
    public int getItemCount() {
        if (genres == null) return 0;
        return genres.size();
    }

    public class GenreViewHolder extends RecyclerView.ViewHolder {

        TextView genreTextView;

        public GenreViewHolder(View itemView) {
            super(itemView);
            genreTextView = itemView.findViewById(R.id.tv_genre);
        }

        void bind(int itemIndex) {
            genreTextView.setText(genres.get(itemIndex));
        }
    }

    public void setGenres (List<String> genres) {
        this.genres = genres;
        notifyDataSetChanged();
    }
}
