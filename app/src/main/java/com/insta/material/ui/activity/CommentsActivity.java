package com.insta.material.ui.activity;

import android.animation.Animator;
import android.animation.AnimatorInflater;
import android.animation.AnimatorListenerAdapter;
import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v4.view.ViewCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.ViewTreeObserver;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.LinearInterpolator;
import android.widget.EditText;
import android.widget.LinearLayout;

import com.insta.material.R;
import com.insta.material.Utils;
import com.insta.material.ui.adapter.CommentAdapter;
import com.insta.material.ui.view.SendCommentButton;

/**
 * 1.缩放当前位置开始 0.1f---1.0f 200ms 加速
 * 2.内容 recyclerview 底部向上200 100ms 减速2.0f
 * 3，评论底部向上 200 200 减速1.0f
 */

public class CommentsActivity extends BaseDrawerActivity
        implements SendCommentButton.OnSendCommentListener {
    public static final String ARG_POSITION = "ARG_POSITION";

    private LinearLayout clContent;
    private RecyclerView mRecyclerView;
    private LinearLayout llAddComment;
    private EditText editText;
    private SendCommentButton btnSendComment;

    private CommentAdapter adapter;
    private int startPositionY;


    public static void show(Context context, int positionY) {
        Intent intent = new Intent(context, CommentsActivity.class);
        intent.putExtra(ARG_POSITION, positionY);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_comments);
        startPositionY = getIntent().getIntExtra(ARG_POSITION, 0);
        clContent = findViewById(R.id.clContent);
        mRecyclerView = findViewById(R.id.recycler);
        llAddComment = findViewById(R.id.llAddComment);
        editText = findViewById(R.id.editText);
        btnSendComment = findViewById(R.id.btnSendComment);
        btnSendComment.setOnSendCommentListener(this);
        adapter = new CommentAdapter();
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mRecyclerView.setAdapter(adapter);
        mRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                if (newState == RecyclerView.SCROLL_STATE_DRAGGING) {
                    adapter.lockAnimation = true;
                }
            }
        });
        if (savedInstanceState == null) {
            clContent.getViewTreeObserver().addOnPreDrawListener(
                    new ViewTreeObserver.OnPreDrawListener() {
                        @Override
                        public boolean onPreDraw() {
                            clContent.getViewTreeObserver().removeOnPreDrawListener(this);
                            startIntroAnimation(startPositionY);
                            return false;
                        }
                    });
        }
    }

    private void startIntroAnimation(int pivotY) {
        clContent.setScaleY(0.1f);
        clContent.setPivotY(pivotY);//以此处为中心点展开
        clContent.animate().scaleY(1.0f)
                .setDuration(200)
                .setInterpolator(new AccelerateInterpolator())
                .setListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        animationContent();
                    }
                })
                .start();
    }

    private void animationContent() {
        adapter.updateItem();
        llAddComment.setTranslationY(200);
        llAddComment
                .animate()
                .translationY(0)
                .setInterpolator(new DecelerateInterpolator())
                .setDuration(200)
                .start();
    }

    @Override
    public void onBackPressed() {
        ViewCompat.setElevation(mToolbar, 0);
        int screenH = Utils.getScreenHeight(this);
        clContent.animate()
                .translationY(screenH)
                .setDuration(200)
                .setInterpolator(new DecelerateInterpolator())
                .setListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        CommentsActivity.super.onBackPressed();
                        overridePendingTransition(0, 0);
                    }
                })
                .start();

    }

    @Override
    public void onSendComment() {
        if (validContent()) {
            adapter.lockAnimation = false;
            adapter.addItem();
            int itemHeight = mRecyclerView.getChildAt(0).getHeight();
            int itemCount = adapter.getItemCount();
            mRecyclerView.smoothScrollBy(0, itemHeight * itemCount);
            editText.setText(null);
            btnSendComment.changeState(SendCommentButton.STATE_SEND);
        }
    }

    private boolean validContent() {
        if (TextUtils.isEmpty(editText.getText())) {
            Animation anim = AnimationUtils.loadAnimation(this, R.anim.shake_error);
            btnSendComment.startAnimation(anim);
            return false;
        }
        return true;
    }


}
