package com.insta.material.ui.view;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.FrameLayout;

import com.insta.material.R;

public class LoadingFeedItemView extends FrameLayout {

    SendingProgressView vSendingProgress;
    View vProgressBg;

    private OnLoadingFinishedListener onLoadingFinishedListener;

    public LoadingFeedItemView(Context context) {
        super(context);
        init();
    }

    public LoadingFeedItemView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public LoadingFeedItemView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public LoadingFeedItemView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init();
    }

    private void init() {
        LayoutInflater.from(getContext()).inflate(R.layout.item_feed_loader, this, true);
        vSendingProgress = findViewById(R.id.vSendingProgress);
        vProgressBg = findViewById(R.id.vProgressBg);
    }

    public void startLoading() {
        vSendingProgress.getViewTreeObserver()
                .addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {
                    @Override
                    public boolean onPreDraw() {
                        vSendingProgress.getViewTreeObserver().removeOnPreDrawListener(this);
                        vSendingProgress.simulateProgress();
                        return true;
                    }
                });
        vSendingProgress.setOnLoadingFinishedListener(
                new SendingProgressView.OnLoadingFinishedListener() {
                    @Override
                    public void onLoadingFinished() {
                        vSendingProgress.animate().scaleY(0).scaleX(0).setDuration(200).setStartDelay(100);
                        vProgressBg.animate().alpha(0.f).setDuration(200).setStartDelay(100)
                                .setListener(new AnimatorListenerAdapter() {
                                    @Override
                                    public void onAnimationEnd(Animator animation) {
                                        vSendingProgress.setScaleX(1);
                                        vSendingProgress.setScaleY(1);
                                        vProgressBg.setAlpha(1);
                                        if (onLoadingFinishedListener != null) {
                                            onLoadingFinishedListener.onLoadingFinished();
                                            onLoadingFinishedListener = null;
                                        }
                                    }
                                }).start();
                    }
                });
    }

    public void setOnLoadingFinishedListener(OnLoadingFinishedListener onLoadingFinishedListener) {
        this.onLoadingFinishedListener = onLoadingFinishedListener;
    }

    public interface OnLoadingFinishedListener {
        void onLoadingFinished();
    }


}
