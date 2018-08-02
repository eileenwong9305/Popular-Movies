package com.example.android.popularmovies.Utils;

import android.content.Context;
import android.content.res.ColorStateList;
import android.media.Image;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.v4.widget.ImageViewCompat;
import android.support.v7.content.res.AppCompatResources;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.example.android.popularmovies.R;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class TrailersAdapter extends RecyclerView.Adapter<TrailersAdapter.TrailerViewHolder> {

    ArrayList<String> trailerKeys;
    final private TrailerClickListener trailerClickListener;

    public interface TrailerClickListener {
        void onClick(String movieKey);
    }

    public TrailersAdapter(TrailerClickListener trailerClickListener) {
        this.trailerClickListener = trailerClickListener;
    }

    @NonNull
    @Override
    public TrailerViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        View view = LayoutInflater.from(context).inflate(R.layout.trailer_item, parent, false);
        return new TrailerViewHolder(view, context);
    }

    @Override
    public void onBindViewHolder(@NonNull TrailerViewHolder holder, int position) {
        holder.bind(position);
    }

    @Override
    public int getItemCount() {
        if (trailerKeys == null) return 0;
        return trailerKeys.size();
    }

    public class TrailerViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        ImageView trailerImageView;
        ImageView playIconImageView;
        Context context;

        public TrailerViewHolder(View itemView, Context context) {
            super(itemView);
            trailerImageView = (ImageView) itemView.findViewById(R.id.iv_detail_trailer);
            playIconImageView = itemView.findViewById(R.id.iv_play_icon);
            this.context = context;
            itemView.setOnClickListener(this);
        }

        void bind(int listIndex) {
            String trailerThumbnailUrl = context.getString(R.string.youtube_thumbanil_path,
                    trailerKeys.get(listIndex));
            Picasso.get().load(trailerThumbnailUrl).centerCrop().fit().into(trailerImageView);
        }

        @Override
        public void onClick(View view) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                playIconImageView.setImageTintList(context.getColorStateList(R.color.colorAccent));
            } else {
                ColorStateList csl = AppCompatResources.getColorStateList(context, R.color.colorAccent);
                ImageViewCompat.setImageTintList(playIconImageView, csl);
            }
            int position = getAdapterPosition();
            trailerClickListener.onClick(trailerKeys.get(position));
        }
    }

    public void setTrailerKeys(ArrayList<String> trailerKeys) {
        this.trailerKeys = trailerKeys;
        notifyDataSetChanged();
    }
}
