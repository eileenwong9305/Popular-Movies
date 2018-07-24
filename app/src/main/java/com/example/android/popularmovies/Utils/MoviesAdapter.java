package com.example.android.popularmovies.Utils;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.android.popularmovies.R;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class MoviesAdapter extends RecyclerView.Adapter<MoviesAdapter.MovieViewHolder> {

    private static final String BASE_PATH = "http://image.tmdb.org/t/p/w342";
    private ArrayList<Movie> movies;
    private GridItemListener listener;

    public MoviesAdapter(GridItemListener listener) {
        this.listener = listener;
    }

    @Override
    public MovieViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.grid_item, parent, false);
        return new MovieViewHolder(view);
    }

    @Override
    public void onBindViewHolder(MovieViewHolder holder, int position) {
        holder.bind(position);
    }

    @Override
    public int getItemCount() {
        if (movies == null) {
            return 0;
        }
        return movies.size();
    }

    /**
     * Set movie data on MovieAdapter
     *
     * @param movies movie data to be displayed
     */
    public void setMovies(ArrayList<Movie> movies) {
        this.movies = movies;
        notifyDataSetChanged();
    }

    public interface GridItemListener {
        void onClick(Movie movie);
    }

    public class MovieViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        final ImageView gridItemImageView;
        final TextView titleTextView;

        public MovieViewHolder(View view) {
            super(view);
            gridItemImageView = view.findViewById(R.id.iv_poster);
            view.setOnClickListener(this);
            titleTextView = view.findViewById(R.id.tv_title);
        }

        /**
         * Set movie title and image
         *
         * @param itemIndex position of item
         */
        void bind(int itemIndex) {
            String posterPath = movies.get(itemIndex).getPoster();

            if (posterPath.equals("null")) {
                Picasso.get().load(R.drawable.no_pic).fit().centerCrop().into(gridItemImageView);
            } else {
                String posterUrl = BASE_PATH + posterPath;
                Picasso.get().load(posterUrl).fit().centerCrop().into(gridItemImageView);
            }
            titleTextView.setText(movies.get(itemIndex).getTitle());
        }

        @Override
        public void onClick(View v) {
            int clickedPosition = getAdapterPosition();
            listener.onClick(movies.get(clickedPosition));
        }
    }
}
