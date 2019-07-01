package com.insta.material.ui.view;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.OvershootInterpolator;

import com.insta.material.Utils;

/**
 * 展示动画showAnimation
 * 1.缩放 从 0.1f --- 1.0f 150ms 撞击动画
 * <p>
 * 隐藏动画hideAnimation
 * <p>
 * 2.缩放 从 1.0f --- 0.1f 150ms 急速动画 延迟100ms执行
 */


public class FeedContextMenuManager extends RecyclerView.OnScrollListener
        implements View.OnAttachStateChangeListener {

    private FeedContextMenuManager() {
    }

    private static final FeedContextMenuManager instance = new FeedContextMenuManager();

    public static FeedContextMenuManager getInstance() {
        return instance;
    }

    private FeedContextMenu contextMenu;//展示的菜单


    public void toggleContextMenuFromView(View v,
                                          FeedContextMenu.OnFeedContextMenuItemClickListener listener) {
        if (contextMenu == null) {//说明还没有
            showFeedContextMenu(v, listener);
        }
        else {
            hideAnimation();
        }
    }


    private void showFeedContextMenu(final View currentView,
                                     FeedContextMenu.OnFeedContextMenuItemClickListener listener) {

        if (contextMenu == null) {
            contextMenu = new FeedContextMenu(currentView.getContext());
        }

        ((ViewGroup) (currentView.getRootView().findViewById(android.R.id.content)))
                .addView(contextMenu);
        contextMenu.setOnFeedContextMenuItemClickListener(listener);
        contextMenu.addOnAttachStateChangeListener(this);
        contextMenu.getViewTreeObserver().addOnPreDrawListener(
                new ViewTreeObserver.OnPreDrawListener() {
                    @Override
                    public boolean onPreDraw() {
                        contextMenu.getViewTreeObserver().removeOnPreDrawListener(this);
                        setupContextMenuInitialPosition(currentView);
                        performShowAnimation();
                        return false;
                    }
                });
    }

    //设置展示的ContextMenu的初始位置
    private void setupContextMenuInitialPosition(View view) {

        int[] outLocation = new int[2];
        view.getLocationOnScreen(outLocation);

        int contextMenuWidth = contextMenu.getWidth();
        int contextMenuHeight = contextMenu.getHeight();

        int left = outLocation[0];
        int top = outLocation[1];

        int bottomMargin = Utils.dpToPx(16);

        contextMenu.setTranslationX(left - contextMenuWidth / 2 + view.getWidth() / 2);
        contextMenu.setTranslationY(top - contextMenuHeight - bottomMargin);

    }

    //ContextMenu的展示动画
    private void performShowAnimation() {
        //设置展示菜单的缩放的中心点
        contextMenu.setPivotX(contextMenu.getWidth() / 2);
        contextMenu.setPivotY(contextMenu.getHeight());

        contextMenu.setScaleX(0.1f);
        contextMenu.setPivotY(0.1f);

        contextMenu.animate()
                .scaleX(1.0f)
                .scaleY(1.0f)
                .setInterpolator(new OvershootInterpolator())
                .setDuration(150)
                .setListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animation) {

                    }
                })
                .start();

    }

    private boolean isAnimationDismiss = false;

    public void hideAnimation() {
        if (!isAnimationDismiss) {
            isAnimationDismiss = true;
            performHideAnimation();
        }

    }

    //设置隐藏的ContextMenu的动画
    private void performHideAnimation() {

        contextMenu.setPivotX(contextMenu.getWidth() / 2);
        contextMenu.setPivotY(contextMenu.getHeight());

        contextMenu.setScaleX(1.0f);
        contextMenu.setScaleY(1.0f);

        contextMenu.animate()
                .scaleX(0.0f)
                .scaleY(0.0f)
                .setDuration(150)
                .setStartDelay(100)
                .setInterpolator(new AccelerateInterpolator())
                .setListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        if (contextMenu != null) {
                            contextMenu.dismiss();
                        }
                        isAnimationDismiss = false;
                    }
                })
                .start();

    }

    @Override
    public void onViewAttachedToWindow(View v) {
        Log.e("1234", "-------------onViewAttachedToWindow--------------");
    }

    @Override
    public void onViewDetachedFromWindow(View v) {
        Log.e("1234", "-------------onViewDetachedFromWindow--------------");
        contextMenu = null;
    }

    public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
    }

    public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
        Log.e("1234", "dx: " + dx + "-------------dy: " + dy);
        if (contextMenu != null) {
            hideAnimation();
            float translationY = contextMenu.getTranslationY();
            contextMenu.setTranslationY(translationY - dy);
        }
    }


}
