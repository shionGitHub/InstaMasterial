package com.insta.material.ui.view;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.util.AttributeSet;
import android.view.View;
import android.view.animation.AccelerateInterpolator;

public class RevealBackgroundView extends View {

    public static final int STATE_NOT_START = 0;
    public static final int STATE_FILL_START = 1;
    public static final int STATE_FINISHED = 2;
    private int state = STATE_NOT_START;

    private Paint fillPaint;
    private float currentProgress;//当前的进度
    private Animator revealAnimator;

    private int[] location;
    private int startPositionX, startPositionY;

    public RevealBackgroundView(Context context) {
        super(context);
        init();
    }

    public RevealBackgroundView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public RevealBackgroundView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public RevealBackgroundView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init();
    }

    private void init() {
        fillPaint = new Paint();
        fillPaint.setStyle(Paint.Style.FILL);
        fillPaint.setColor(Color.WHITE);

        revealAnimator = ObjectAnimator.ofFloat(this,
                "currentProgress",
                0.0f, 1.0f);
        revealAnimator.setDuration(400);
        revealAnimator.setInterpolator(new AccelerateInterpolator());
        revealAnimator.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                setFrameToFinished();
            }
        });

    }

    public void setFrameToFinished() {
        this.state = STATE_FINISHED;
        postInvalidate();
        if (onStateChangeListener != null) {
            onStateChangeListener.onStateChange(this.state);
        }
    }

    public void setStartPosition(int[] location) {
        this.location = location;
        startPositionX = location[0];
        startPositionY = location[1];
        setCurrentState(STATE_FILL_START);//开始了
    }

    private void setCurrentState(int state) {
        if (this.state == state) {
            return;
        }
        this.state = state;
        if (this.state == STATE_FILL_START) {
            revealAnimator.start();
        }
        if (onStateChangeListener != null) {
            onStateChangeListener.onStateChange(this.state);
        }

    }

    @Override
    protected void onDraw(Canvas canvas) {
        if (this.state == STATE_FINISHED) {
            canvas.drawRect(0, 0, getWidth(), getHeight(), fillPaint);
        }
        else {
            canvas.drawCircle(startPositionX, startPositionY, (getWidth() + getHeight()) * currentProgress, fillPaint);
        }
    }

    public void setCurrentProgress(float progress) {
        this.currentProgress = progress;
        invalidate();
    }

    private OnStateChangeListener onStateChangeListener;

    public void setOnStateChangeListener(OnStateChangeListener listener) {
        this.onStateChangeListener = listener;
    }

    public interface OnStateChangeListener {
        void onStateChange(int state);
    }

}
