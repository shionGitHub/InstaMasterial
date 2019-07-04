package com.insta.material.ui.adapter;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.Interpolator;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.insta.material.R;

import java.util.Arrays;
import java.util.List;

public class UserProfileAdapter extends RecyclerView.Adapter<UserProfileAdapter.UserProfileHolder> {


    private Context mContext;
    private List<String> photos;

    private static final int PHOTO_ANIMATION_DELAY = 600;
    private static final Interpolator INTERPOLATOR = new DecelerateInterpolator();

    public UserProfileAdapter(Context context) {
        this.mContext = context;
        photos = Arrays.asList(context.getResources().getStringArray(R.array.user_photos));

    }

    @NonNull
    @Override
    public UserProfileHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        LayoutInflater inflater = LayoutInflater.from(viewGroup.getContext());
        View v = inflater.inflate(R.layout.item_photo, viewGroup, false);
        return new UserProfileHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull UserProfileHolder holder, int position) {
        bindToPhoto(holder, position);
        if (lastAnimationItem < position) {
            lastAnimationItem = position;
        }
    }

    private int lastAnimationItem = -1;

    private void bindToPhoto(final UserProfileHolder holder, final int position) {
        Glide.with(mContext)
                .load(photos.get(position))
                .centerCrop()
                .listener(new RequestListener<Drawable>() {
                    @Override
                    public boolean onLoadFailed(@Nullable GlideException e,
                                                Object model,
                                                Target<Drawable> target,
                                                boolean isFirstResource) {
                        return false;
                    }

                    @Override
                    public boolean onResourceReady(Drawable resource,
                                                   Object model,
                                                   Target<Drawable> target,
                                                   DataSource dataSource,
                                                   boolean isFirstResource) {
                        animationToItem(holder.imageView, position);
                        return false;
                    }
                })
                .into(holder.imageView);
    }

    public boolean lockAnimation = false;

    private void animationToItem(View view, int position) {
        if (lockAnimation) {
            return;
        }

        view.setScaleX(0.0f);
        view.setScaleY(0.0f);
        view.setPivotX(view.getWidth() / 2);
        view.setPivotY(view.getHeight() / 2);
        int startDelayTime = PHOTO_ANIMATION_DELAY + position * 30;
        view.animate().scaleY(1.0f).scaleX(1.0f)
                .setInterpolator(INTERPOLATOR)
                .setDuration(200)
                .setStartDelay(startDelayTime)
                .setListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        lockAnimation = true;
                    }
                })
                .start();

    }

    @Override
    public int getItemCount() {
        return photos.size();
    }

    public static class UserProfileHolder extends RecyclerView.ViewHolder {

        public ImageView imageView;

        public UserProfileHolder(@NonNull View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.image);
        }

    }

}
