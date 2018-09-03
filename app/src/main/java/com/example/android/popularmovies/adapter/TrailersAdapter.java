package com.example.android.popularmovies.adapter;

import android.app.Activity;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v4.app.ShareCompat;
import android.support.v7.widget.RecyclerView;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;

import com.example.android.popularmovies.R;
import com.example.android.popularmovies.data.Trailer;
import com.example.android.popularmovies.ui.detail.DetailActivity;
import com.squareup.picasso.Picasso;

import java.util.List;

public class TrailersAdapter extends RecyclerView.Adapter<TrailersAdapter.TrailerViewHolder> {

    final private TrailerClickListener trailerClickListener;
    List<Trailer> trailers;
    private Activity activity;

    public TrailersAdapter(Activity activity, TrailerClickListener trailerClickListener) {
        this.activity = activity;
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
    public void onBindViewHolder(@NonNull final TrailerViewHolder holder, final int position) {
        holder.bind(position);
        holder.shareIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                PopupMenu menu = new PopupMenu(activity, view, Gravity.RIGHT);
                MenuInflater inflater = menu.getMenuInflater();
                inflater.inflate(R.menu.trailer_overflow, menu.getMenu());
                menu.show();

                menu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem menuItem) {
                        int id = menuItem.getItemId();
                        if (id == R.id.action_share) {
                            Trailer selectedTrailer = trailers.get(position);
                            String mimeType = "text/plain";
                            String title = "Share a link";
                            String linkToShare = "Watch \"" + selectedTrailer.getTitle() + "\" on Youtube\n\n" +
                                    DetailActivity.YOUTUBE_BASE_URL + selectedTrailer.getVideoKey();
                            ShareCompat.IntentBuilder.from(activity)
                                    .setChooserTitle(title)
                                    .setType(mimeType)
                                    .setText(linkToShare)
                                    .startChooser();
                        }
                        return false;
                    }
                });
            }
        });
    }

    @Override
    public int getItemCount() {
        if (trailers == null) return 0;
        return trailers.size();
    }

    public void setTrailers(List<Trailer> trailers) {
        this.trailers = trailers;
        notifyDataSetChanged();
    }

    public interface TrailerClickListener {
        void onClick(String movieKey);
    }

    public class TrailerViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        ImageView trailerImageView;
        TextView videoTitleTextView;
        TextView videoTypeTextView;
        ImageView shareIcon;
        Context context;

        public TrailerViewHolder(View itemView, final Context context) {
            super(itemView);
            trailerImageView = (ImageView) itemView.findViewById(R.id.iv_detail_trailer);
            videoTitleTextView = itemView.findViewById(R.id.tv_video_title);
            videoTypeTextView = itemView.findViewById(R.id.tv_video_type);
            shareIcon = itemView.findViewById(R.id.share_icon);

            this.context = context;
            itemView.setOnClickListener(this);
        }

        void bind(int listIndex) {
            String trailerThumbnailUrl = context.getString(R.string.youtube_thumbanil_path,
                    trailers.get(listIndex).getVideoKey());
            Picasso.get().load(trailerThumbnailUrl).centerCrop().fit().into(trailerImageView);
            videoTitleTextView.setText(trailers.get(listIndex).getTitle());
            videoTypeTextView.setText(trailers.get(listIndex).getType());
        }

        @Override
        public void onClick(View view) {
            int position = getAdapterPosition();
            trailerClickListener.onClick(trailers.get(position).getVideoKey());
        }
    }

}
