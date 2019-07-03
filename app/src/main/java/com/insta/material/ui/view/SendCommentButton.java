package com.insta.material.ui.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ViewAnimator;

import com.insta.material.R;

public class SendCommentButton extends ViewAnimator implements View.OnClickListener {

    public static final int STATE_SEND = 0;
    public static final int STATE_DONE = 1;

    private int currentState = STATE_DONE;//默认完成了发送

    public SendCommentButton(Context context) {
        super(context);
        init();
    }

    public SendCommentButton(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    private void init() {
        setOnClickListener(this);
        inflate(getContext(), R.layout.view_send_comment_button, this);

    }

    private Runnable resetRunnable = new Runnable() {
        @Override
        public void run() {
            changeState(STATE_DONE);
        }
    };


    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        removeCallbacks(resetRunnable);
    }

    public void changeState(int state) {
        if (this.currentState == state) {
            return;
        }
        this.currentState = state;

        if (currentState == STATE_SEND) {//DONE ----> SEND
            postDelayed(resetRunnable, 2000);
            setEnabled(false);//让这个不可用
            setInAnimation(getContext(), R.anim.anim_send_in);
            setOutAnimation(getContext(), R.anim.anim_send_out);

        }
        else if (currentState == STATE_DONE) {//SEND ----> DONE
            setEnabled(true);//让这个可用
            setInAnimation(getContext(), R.anim.anim_done_in);
            setOutAnimation(getContext(), R.anim.anim_done_out);
        }
        showNext();

    }


    @Override
    public void onClick(View v) {
        if (onSendCommentListener != null) {
            onSendCommentListener.onSendComment();
        }
    }

    OnSendCommentListener onSendCommentListener;

    public void setOnSendCommentListener(OnSendCommentListener listener) {
        this.onSendCommentListener = listener;
    }

    public interface OnSendCommentListener {
        void onSendComment();
    }


}
