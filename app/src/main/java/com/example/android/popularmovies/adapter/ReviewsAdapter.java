package com.example.android.popularmovies.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v4.widget.TextViewCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.android.popularmovies.R;
import com.example.android.popularmovies.data.Review;

import java.util.List;

public class ReviewsAdapter extends RecyclerView.Adapter<ReviewsAdapter.ReviewViewHolder> {

    private static final int DEFAULT_MAX_LINE = 3;
    private List<Review> reviews;

    @NonNull
    @Override
    public ReviewViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        View view = LayoutInflater.from(context).inflate(R.layout.review_item, parent, false);
        return new ReviewViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final ReviewViewHolder holder, int position) {
        holder.bind(holder.getAdapterPosition());
        holder.contentTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int maxLines = TextViewCompat.getMaxLines(holder.contentTextView);
                switch (maxLines) {
                    case DEFAULT_MAX_LINE:
                        holder.contentTextView.setMaxLines(Integer.MAX_VALUE);
                        break;
                    default:
                        holder.contentTextView.setMaxLines(DEFAULT_MAX_LINE);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        if (reviews == null) return 0;
        return reviews.size();
    }

    public void setReviews(List<Review> reviews) {
        this.reviews = reviews;
        notifyDataSetChanged();
    }

    public class ReviewViewHolder extends RecyclerView.ViewHolder {

        final TextView authorTextView;
        final TextView contentTextView;

        public ReviewViewHolder(View itemView) {
            super(itemView);
            authorTextView = (TextView) itemView.findViewById(R.id.tv_review_author);
            contentTextView = (TextView) itemView.findViewById(R.id.tv_review_content);
        }

        void bind(int itemIndex) {
            authorTextView.setText(reviews.get(itemIndex).getAuthor());
            contentTextView.setText(reviews.get(itemIndex).getContent());
        }

    }
}
