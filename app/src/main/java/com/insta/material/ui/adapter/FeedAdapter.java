package com.insta.material.ui.adapter;

import android.content.Context;
import android.support.annotation.MainThread;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextSwitcher;

import com.insta.material.R;
import com.insta.material.ui.activity.MainActivity;
import com.insta.material.ui.view.LoadingFeedItemView;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class FeedAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    public static final String ACTION_LIKE_BUTTON_CLICKED = "action_like_button_button";
    public static final String ACTION_LIKE_IMAGE_CLICKED = "action_like_image_button";

    public static final int VIEW_TYPE_DEFAULT = 1;
    public static final int VIEW_TYPE_LOADER = 2;

    private final List<FeedItem> feedItems = new ArrayList<>();

    private Context context;
    private OnFeedItemClickListener onFeedItemClickListener;
    private boolean showLoadingView = false;

    public FeedAdapter(Context context) {
        this.context = context;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int type) {
        if (type == VIEW_TYPE_DEFAULT) {
            LayoutInflater inflater = LayoutInflater.from(context);
            View view = inflater.inflate(R.layout.item_feed, viewGroup, false);
            CellFeedViewHolder holder = new CellFeedViewHolder(view);
            setupClickableViews(view, holder);
            return holder;
        }
        else if (type == VIEW_TYPE_LOADER) {
            LoadingFeedItemView view = new LoadingFeedItemView(context);
            view.setLayoutParams(new ViewGroup.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT));
            return new LoadingCellFeedViewHolder(view);
        }
        return null;
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int pos) {
        ((CellFeedViewHolder) holder).bindView(feedItems.get(pos));
        if (getItemViewType(pos) == VIEW_TYPE_LOADER) {
            bindLoadingFeedItem((LoadingCellFeedViewHolder) holder);
        }
    }

    @Override
    public int getItemViewType(int position) {
        if (showLoadingView && position == 0) {
            return VIEW_TYPE_LOADER;
        }
        return VIEW_TYPE_DEFAULT;

    }

    private void bindLoadingFeedItem(LoadingCellFeedViewHolder holder) {
        holder.loadingFeedItemView.setOnLoadingFinishedListener(
                new LoadingFeedItemView.OnLoadingFinishedListener() {
                    @Override
                    public void onLoadingFinished() {
                        showLoadingView = false;
                        notifyItemChanged(0);
                    }
                });

        holder.loadingFeedItemView.startLoading();

    }

    @Override
    public int getItemCount() {
        return feedItems.size();
    }

    private void setupClickableViews(final View view, final CellFeedViewHolder cellFeedViewHolder) {
        cellFeedViewHolder.ivUserProfile.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        onFeedItemClickListener.onProfileClick(view);
                    }
                }
        );
        cellFeedViewHolder.btnComments.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        onFeedItemClickListener.onCommentsClick(cellFeedViewHolder.btnComments, cellFeedViewHolder.getAdapterPosition());
                    }
                });
        cellFeedViewHolder.btnMore.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        onFeedItemClickListener.onMoreClick(cellFeedViewHolder.btnMore, cellFeedViewHolder.getAdapterPosition());
                    }
                }
        );
        cellFeedViewHolder.btnLike.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        int adapterPosition = cellFeedViewHolder.getAdapterPosition();
                        feedItems.get(adapterPosition).likesCount++;
                        notifyItemChanged(adapterPosition, ACTION_LIKE_BUTTON_CLICKED);
                        if (context instanceof MainActivity) {
                            ((MainActivity) context).showLikedSnackbar();
                        }
                    }
                }
        );
        cellFeedViewHolder.ivFeedCenter.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        int adapterPosition = cellFeedViewHolder.getAdapterPosition();
                        feedItems.get(adapterPosition).likesCount++;
                        notifyItemChanged(adapterPosition, ACTION_LIKE_IMAGE_CLICKED);
                        if (context instanceof MainActivity) {
                            ((MainActivity) context).showLikedSnackbar();
                        }
                    }
                }
        );

    }

    public void updateItems(boolean animated) {
        feedItems.clear();
        feedItems.addAll(Arrays.asList(
                new FeedItem(33, false),
                new FeedItem(1, false),
                new FeedItem(223, false),
                new FeedItem(2, false),
                new FeedItem(6, false),
                new FeedItem(8, false),
                new FeedItem(99, false)
        ));
        if (animated) {
            notifyItemRangeInserted(0, feedItems.size());
        }
        else {
            notifyDataSetChanged();
        }
    }

    public void setOnFeedItemClickListener(OnFeedItemClickListener onFeedItemClickListener) {
        this.onFeedItemClickListener = onFeedItemClickListener;
    }

    public void showLoadingView() {
        showLoadingView = true;
        notifyItemChanged(0);
    }

    public static class CellFeedViewHolder extends RecyclerView.ViewHolder {

        ImageView ivFeedCenter;
        View vBgLike;
        ImageView ivLike;

        ImageButton btnLike;
        ImageButton btnComments;
        ImageButton btnMore;

        ImageView ivFeedBottom;

        TextSwitcher tsLikesCounter;
        ImageView ivUserProfile;
        FrameLayout vImageRoot;

        public FeedItem feedItem;

        public CellFeedViewHolder(@NonNull View itemView) {
            super(itemView);
            vImageRoot = itemView.findViewById(R.id.vImageRoot);
            ivUserProfile = itemView.findViewById(R.id.ivUserProfile);
            tsLikesCounter = itemView.findViewById(R.id.tsLikesCounter);
            ivFeedBottom = itemView.findViewById(R.id.ivFeedBottom);
            btnMore = itemView.findViewById(R.id.btnMore);
            btnComments = itemView.findViewById(R.id.btnComments);
            btnLike = itemView.findViewById(R.id.btnLike);
            ivLike = itemView.findViewById(R.id.ivLike);
            vBgLike = itemView.findViewById(R.id.vBgLike);
            ivFeedCenter = itemView.findViewById(R.id.ivFeedCenter);
        }

        public void bindView(FeedItem feedItem) {
            this.feedItem = feedItem;
            int adapterPosition = getAdapterPosition();
            ivFeedCenter.setImageResource(adapterPosition % 2 == 0 ? R.drawable.img_feed_center_1 : R.drawable.img_feed_center_2);
            ivFeedBottom.setImageResource(adapterPosition % 2 == 0 ? R.drawable.img_feed_bottom_1 : R.drawable.img_feed_bottom_2);
            tsLikesCounter.setCurrentText(
                    itemView.getContext()
                            .getResources()
                            .getQuantityString(R.plurals.likes_count, feedItem.likesCount, feedItem.likesCount)
            );


        }


    }

    public static class LoadingCellFeedViewHolder extends CellFeedViewHolder {
        public LoadingFeedItemView loadingFeedItemView;

        public LoadingCellFeedViewHolder(LoadingFeedItemView itemView) {
            super(itemView);
            this.loadingFeedItemView = itemView;
        }

        @Override
        public void bindView(FeedItem feedItem) {
            super.bindView(feedItem);
        }
    }

    public static class FeedItem {
        public int likesCount;
        public boolean isLiked;

        public FeedItem(int likesCount, boolean isLiked) {
            this.likesCount = likesCount;
            this.isLiked = isLiked;
        }
    }

    public interface OnFeedItemClickListener {
        void onCommentsClick(View v, int position);

        void onMoreClick(View v, int position);

        void onProfileClick(View v);
    }

}
