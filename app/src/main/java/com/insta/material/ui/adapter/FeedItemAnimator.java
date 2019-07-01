package com.insta.material.ui.adapter;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.support.annotation.NonNull;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.OvershootInterpolator;

import com.insta.material.R;
import com.insta.material.Utils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Item动画的添加
 * <p>
 * 从屏幕的底部向上减速运行 持续700ms
 * <p>
 * Item动画的更新
 * <p>
 * //点击心形的时候
 * 心形按钮 -- 先是rotate (300) 加速旋转360度----然后缩放0.2f-1.0f 撞击的动画300
 * 文字移动 TextSwitcher 动画
 * <p>
 * 点击中见图图片的时候
 * 上面的动画
 * 喜欢的大背景 0.1--1.0f缩放 透明度1.0f
 * 喜欢的小图标 缩放  0.1f
 * <p>
 * first
 * 大背景 200ms 缩放从 0.1f - 1.0f 透明度从 1.0f - 0.0f(延迟150) 减速
 * 小图标 300ms 缩放 从0.1f --1.0f
 * <p>
 * second
 * 小图标
 * 300ms 缩放 从1.0f -- 0.0f 加速
 */
public class FeedItemAnimator extends DefaultItemAnimator {

    //用到的插值器
    private static final DecelerateInterpolator DECELERATE_INTERPOLATOR = new DecelerateInterpolator();
    private static final AccelerateInterpolator ACCELERATE_INTERPOLATOR = new AccelerateInterpolator();
    private static final OvershootInterpolator OVERSHOOT_INTERPOLATOR = new OvershootInterpolator(4);

    //使用Map表存储动画，可以取消未结束的动画
    private Map<RecyclerView.ViewHolder, AnimatorSet> likeAnimationsMap = new HashMap<>();
    private Map<RecyclerView.ViewHolder, AnimatorSet> heartAnimationsMap = new HashMap<>();

    private int lastAddAnimatedItem = -2;//最后添加动画的item

    //更新的时候可以福永holder
    @Override
    public boolean canReuseUpdatedViewHolder(@NonNull RecyclerView.ViewHolder viewHolder) {
        return true;
    }

    @Override
    public boolean animateAdd(RecyclerView.ViewHolder holder) {
        Log.e("1234", "-------------------pos : " + holder.getAdapterPosition());
        int adapterPosition = holder.getAdapterPosition();
        if (adapterPosition > lastAddAnimatedItem) {
            lastAddAnimatedItem++;
            runEnterAnimation(holder);
            return false;
        }
        dispatchAddFinished(holder);
        return false;
    }

    //添加进入时候的动画
    private void runEnterAnimation(final RecyclerView.ViewHolder holder) {
        int screenH = Utils.getScreenHeight(holder.itemView.getContext());
        holder.itemView.setTranslationY(screenH);
        holder.itemView
                .animate()
                .translationY(0)
                .setDuration(700)
                .setListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        dispatchAddFinished(holder);
                    }
                })
                .setInterpolator(new DecelerateInterpolator(3.0f))
                .start();

    }

    @NonNull
    @Override
    public ItemHolderInfo recordPreLayoutInformation(@NonNull RecyclerView.State state,
                                                     @NonNull RecyclerView.ViewHolder viewHolder,
                                                     int changeFlags,
                                                     @NonNull List<Object> payloads) {

        if (changeFlags == FLAG_CHANGED) {//Item改变动画的时候
            for (Object payload : payloads) {
                if (payload instanceof String) {
                    return new FeedItemHolderInfo((String) payload);//标记的字符串
                }
            }
        }

        return super.recordPreLayoutInformation(state, viewHolder, changeFlags, payloads);
    }

    //item改变的动画
    @Override
    public boolean animateChange(
            @NonNull RecyclerView.ViewHolder oldHolder,
            @NonNull RecyclerView.ViewHolder newHolder,
            @NonNull ItemHolderInfo preInfo,
            @NonNull ItemHolderInfo postInfo) {
        cancelCurrentAnimationIfExists(newHolder);
        if (preInfo instanceof FeedItemHolderInfo) {//属于自定义的类型

            animateHeartButton((FeedAdapter.CellFeedViewHolder) newHolder);
            updateLikesCounter((FeedAdapter.CellFeedViewHolder) newHolder, ((FeedAdapter.CellFeedViewHolder) newHolder).feedItem.likesCount);

            if (((FeedItemHolderInfo) preInfo).action.equals(FeedAdapter.ACTION_LIKE_IMAGE_CLICKED)) {//点击了图片的时候
                animatePhotoLike((FeedAdapter.CellFeedViewHolder) newHolder);
            }

            return false;
        }

        return super.animateChange(oldHolder, newHolder, preInfo, postInfo);
    }

    private void cancelCurrentAnimationIfExists(RecyclerView.ViewHolder holder) {

        if (heartAnimationsMap.containsKey(holder)) {
            heartAnimationsMap.get(holder).cancel();
        }
        if (likeAnimationsMap.containsKey(holder)) {
            likeAnimationsMap.get(holder).cancel();
        }
    }

    private void dispatchChangeFinishedIfAllAnimationsEnded(RecyclerView.ViewHolder holder) {
        if (heartAnimationsMap.containsKey(holder) || likeAnimationsMap.containsKey(holder)) {
            return;
        }

        dispatchAnimationFinished(holder);
    }

    //中间的图片的动画
    private void animatePhotoLike(final FeedAdapter.CellFeedViewHolder holder) {
        holder.vBgLike.setVisibility(View.VISIBLE);
        holder.ivLike.setVisibility(View.VISIBLE);
        holder.vBgLike.setScaleY(0.1f);
        holder.vBgLike.setScaleX(0.1f);
        holder.vBgLike.setScaleY(1.0f);
        holder.ivLike.setScaleX(0.1f);
        holder.ivLike.setScaleY(0.1f);

        AnimatorSet set = new AnimatorSet();

        ObjectAnimator bgScaleXAnim = ObjectAnimator.ofFloat(holder.vBgLike, "scaleX", 0.1f, 1.0f);
        bgScaleXAnim.setDuration(200);
        bgScaleXAnim.setInterpolator(DECELERATE_INTERPOLATOR);

        ObjectAnimator bgScaleYAnim = ObjectAnimator.ofFloat(holder.vBgLike, "scaleY", 0.1f, 1.0f);
        bgScaleYAnim.setDuration(200);
        bgScaleYAnim.setInterpolator(DECELERATE_INTERPOLATOR);

        ObjectAnimator bgAlphaAnim = ObjectAnimator.ofFloat(holder.vBgLike, "alpha", 1.0f, 0.0f);
        bgAlphaAnim.setDuration(200);
        bgAlphaAnim.setStartDelay(150);
        bgAlphaAnim.setInterpolator(DECELERATE_INTERPOLATOR);

        ObjectAnimator ivScaleXAnim = ObjectAnimator.ofFloat(holder.ivLike, "scaleX", 0.1f, 1.0f);
        ivScaleXAnim.setDuration(300);
        ivScaleXAnim.setInterpolator(DECELERATE_INTERPOLATOR);

        ObjectAnimator ivScaleYAnim = ObjectAnimator.ofFloat(holder.ivLike, "scaleY", 0.1f, 1.0f);
        ivScaleYAnim.setDuration(300);
        ivScaleYAnim.setInterpolator(DECELERATE_INTERPOLATOR);

        ObjectAnimator ivScaleXXAnim = ObjectAnimator.ofFloat(holder.ivLike, "scaleX", 1.0f, 0.0f);
        ivScaleXXAnim.setDuration(300);
        ivScaleXXAnim.setInterpolator(ACCELERATE_INTERPOLATOR);

        ObjectAnimator ivScaleYYAnim = ObjectAnimator.ofFloat(holder.ivLike, "scaleY", 1.0f, 0.0f);
        ivScaleYYAnim.setDuration(300);
        ivScaleYYAnim.setInterpolator(ACCELERATE_INTERPOLATOR);

        set.playTogether(bgScaleXAnim, bgScaleYAnim, bgAlphaAnim, ivScaleXAnim, ivScaleYAnim);
        set.play(ivScaleXXAnim).with(ivScaleYYAnim).after(bgScaleXAnim);

        set.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                likeAnimationsMap.remove(holder);
                holder.vBgLike.setVisibility(View.INVISIBLE);
                holder.ivLike.setVisibility(View.INVISIBLE);
                dispatchChangeFinishedIfAllAnimationsEnded(holder);
            }
        });
        set.start();
        likeAnimationsMap.put(holder, set);
    }

    //心形按钮动画
    private void animateHeartButton(final FeedAdapter.CellFeedViewHolder holder) {

        AnimatorSet set = new AnimatorSet();
        ObjectAnimator rotateAnim = ObjectAnimator.ofFloat(holder.btnLike,
                "rotation", 0, 360);
        rotateAnim.setDuration(300);
        rotateAnim.setInterpolator(ACCELERATE_INTERPOLATOR);

        ObjectAnimator scaleXAnim = ObjectAnimator.ofFloat(holder.btnLike,
                "scaleX", 0.2f, 1.0f);
        scaleXAnim.setDuration(300);
        scaleXAnim.setInterpolator(OVERSHOOT_INTERPOLATOR);

        ObjectAnimator scaleYAnim = ObjectAnimator.ofFloat(holder.btnLike,
                "scaleY", 0.2f, 1.0f);
        scaleYAnim.setDuration(300);
        scaleYAnim.setInterpolator(OVERSHOOT_INTERPOLATOR);

        set.play(scaleXAnim).with(scaleYAnim).after(rotateAnim);

        set.addListener(new AnimatorListenerAdapter() {

            @Override
            public void onAnimationStart(Animator animation) {
                holder.btnLike.setImageResource(R.drawable.ic_heart_red);
            }

            @Override
            public void onAnimationEnd(Animator animation) {
                heartAnimationsMap.remove(holder);
                dispatchChangeFinishedIfAllAnimationsEnded(holder);
            }
        });
        set.start();

        heartAnimationsMap.put(holder, set);//动画添加到Map这张表中
    }

    //更新文字的变化
    private void updateLikesCounter(FeedAdapter.CellFeedViewHolder holder, int count) {
        String currentText = holder.itemView.getResources()
                .getQuantityString(R.plurals.likes_count,
                        count - 1, count - 1);
        holder.tsLikesCounter.setCurrentText(currentText);
        String text = holder.itemView.getResources()
                .getQuantityString(R.plurals.likes_count,
                        count, count);
        holder.tsLikesCounter.setText(currentText);

    }

    public static class FeedItemHolderInfo extends ItemHolderInfo {
        public String action;

        public FeedItemHolderInfo(String action) {
            this.action = action;
        }

    }


    @Override
    public void endAnimation(RecyclerView.ViewHolder item) {
        super.endAnimation(item);
        cancelCurrentAnimationIfExists(item);
    }

    @Override
    public void endAnimations() {
        super.endAnimations();
        for (AnimatorSet animatorSet : likeAnimationsMap.values()) {
            animatorSet.cancel();
        }
        for (AnimatorSet animatorSet : heartAnimationsMap.values()) {
            animatorSet.cancel();
        }
    }

}
