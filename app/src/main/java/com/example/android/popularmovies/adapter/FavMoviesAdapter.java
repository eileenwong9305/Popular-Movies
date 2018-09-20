package com.example.android.popularmovies.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.android.popularmovies.R;
import com.example.android.popularmovies.data.Movie;
import com.example.android.popularmovies.utils.GridItemClickListener;
import com.squareup.picasso.Picasso;

import java.util.List;

public class FavMoviesAdapter extends RecyclerView.Adapter<FavMoviesAdapter.FavMovieViewHolder> {

    private static final String BASE_PATH = "http://image.tmdb.org/t/p/w342";
    private List<Movie> movies;
    private GridItemClickListener listener;

    public FavMoviesAdapter(GridItemClickListener listener) {
        this.listener = listener;
    }

    @Override
    public FavMovieViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.grid_item, parent, false);
        return new FavMovieViewHolder(view);
    }

    @Override
    public void onBindViewHolder(FavMovieViewHolder holder, int position) {
        holder.bind(holder.getAdapterPosition());
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
    public void setMovies(List<Movie> movies) {
        this.movies = movies;
        notifyDataSetChanged();
    }

    public class FavMovieViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        final ImageView gridItemImageView;
        final TextView titleTextView;

        public FavMovieViewHolder(View view) {
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
            String posterUrl = BASE_PATH + posterPath;
            Picasso.get()
                    .load(posterUrl)
                    .placeholder(R.drawable.poster_placeholder)
                    .error(R.drawable.no_pic)
                    .fit()
                    .centerCrop()
                    .into(gridItemImageView);

            titleTextView.setText(movies.get(itemIndex).getTitle());
        }

        @Override
        public void onClick(View v) {
            int clickedPosition = getAdapterPosition();
            listener.onClick(movies.get(clickedPosition));
        }
    }
}
