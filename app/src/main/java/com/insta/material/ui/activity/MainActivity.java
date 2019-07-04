package com.insta.material.ui.activity;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.View;
import android.view.animation.OvershootInterpolator;
import android.widget.Toast;

import com.insta.material.R;
import com.insta.material.Utils;
import com.insta.material.ui.activity.BaseDrawerActivity;
import com.insta.material.ui.adapter.FeedAdapter;
import com.insta.material.ui.adapter.FeedItemAnimator;
import com.insta.material.ui.view.FeedContextMenu;
import com.insta.material.ui.view.FeedContextMenuManager;

public class MainActivity extends BaseDrawerActivity
        implements FeedAdapter.OnFeedItemClickListener,
        FeedContextMenu.OnFeedContextMenuItemClickListener,
        View.OnClickListener {

    public static final String ACTION_SHOW_LOADING_ITEM = "action_show_loading_item";

    private static final int ANIM_DURATION_TOOLBAR = 300;//Toolbar动画时间
    private static final int ANIM_DURATION_FAB = 400;//FAB动画时间

    private RecyclerView rvFeed;
    private FloatingActionButton fabCreate;
    private CoordinatorLayout clContent;

    private FeedAdapter feedAdapter;

    private boolean pendingIntroAnimation;//延迟引导动画与否


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setupFeed();

        if (savedInstanceState == null) {
            pendingIntroAnimation = true;
        }
        else {
            feedAdapter.updateItems(false);
        }

    }

    @Override
    protected void initView() {
        super.initView();
        rvFeed = findViewById(R.id.rvFeed);
        fabCreate = findViewById(R.id.btnCreate);
        clContent = findViewById(R.id.content);
        fabCreate.setOnClickListener(this);
    }

    private void setupFeed() {
        LinearLayoutManager manager =
                new LinearLayoutManager(this) {
                    @Override
                    protected int getExtraLayoutSpace(
                            RecyclerView.State state) {
                        super.getExtraLayoutSpace(state);
                        //解决在RecyclerView的元素比较高
                        // 一屏只能显示一个元素的时候，
                        // 第一次滑动到第二个元素会卡顿。
                        return 300;
                    }
                };
        rvFeed.setLayoutManager(manager);

        feedAdapter = new FeedAdapter(this);
        feedAdapter.setOnFeedItemClickListener(this);
        rvFeed.setAdapter(feedAdapter);

        //滚动
        rvFeed.addOnScrollListener(FeedContextMenuManager.getInstance());
        //item的动画
        rvFeed.setItemAnimator(new FeedItemAnimator());
    }


    @Override
    public void onCommentsClick(View v, int position) {
        int[] location = new int[2];
        v.getLocationOnScreen(location);
        CommentsActivity.show(this, location[1]);
        overridePendingTransition(0, 0);
    }

    @Override
    public void onMoreClick(View v, int position) {
        FeedContextMenuManager.getInstance().toggleContextMenuFromView(v, this);
    }

    @Override
    public void onProfileClick(View v) {
        int[] outLocation = new int[2];
        v.getLocationOnScreen(outLocation);
        outLocation[0] += v.getWidth() / 2;
        UserProfileActivity.show(this, outLocation);
        overridePendingTransition(0, 0);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        if (pendingIntroAnimation) {
            pendingIntroAnimation = false;
            startIntroAnimation();
        }
        return true;
    }

    //toolbar -- 从上往下 ---300ms ---- 延迟300
    //中间logo -- 从上往下 ---300ms ---- 延迟400
    //右上角菜单 -- 从上往下 ---300ms ---- 延迟500
    private void startIntroAnimation() {
        fabCreate.setTranslationY(2 * getResources().getDimension(R.dimen.btn_fab_size));
        int actionbarSize = Utils.dpToPx(56);

        mToolbar.setTranslationY(-actionbarSize);
        ivLogo.setTranslationY(-actionbarSize);
        mMenuItem.getActionView().setTranslationY(-actionbarSize);

        mToolbar.animate().translationY(0).setDuration(ANIM_DURATION_TOOLBAR).setStartDelay(300);
        ivLogo.animate().translationY(0).setDuration(ANIM_DURATION_TOOLBAR).setStartDelay(400);
        mMenuItem.getActionView().animate().translationY(0).setDuration(ANIM_DURATION_TOOLBAR).setStartDelay(500)
                .setListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        startContentAnimation();
                    }
                }).start();
    }

    //下面FAB时一个300m的撞击动画，延迟400执行
    //RecyclerView的item动画
    private void startContentAnimation() {
        fabCreate.animate()
                .translationY(0)
                .setInterpolator(new OvershootInterpolator(1.0f))
                .setStartDelay(300)
                .setDuration(ANIM_DURATION_FAB)
                .start();
        feedAdapter.updateItems(true);
    }

    public void showLikedSnackbar() {
        Snackbar.make(clContent, "Liked", Snackbar.LENGTH_SHORT).show();
    }

    @Override
    public void onReportClick() {
        FeedContextMenuManager.getInstance().hideAnimation();
    }

    @Override
    public void onSharePhotoClick() {
        FeedContextMenuManager.getInstance().hideAnimation();
    }

    @Override
    public void onCopyShareUrlClick() {
        FeedContextMenuManager.getInstance().hideAnimation();
    }

    @Override
    public void onCancelClick() {
        FeedContextMenuManager.getInstance().hideAnimation();
    }

    @Override
    public void onClick(View v) {
        if (v == fabCreate) {

            int[] outLocation = new int[2];
            v.getLocationOnScreen(outLocation);
            outLocation[0] += v.getWidth() / 2;
            TakePhotoActivity.show(this, outLocation);
            overridePendingTransition(0, 0);
        }
    }

//    @Override
//    protected void onNewIntent(Intent intent) {
//        super.onNewIntent(intent);
//        if (ACTION_SHOW_LOADING_ITEM.equals(intent.getAction())) {
//            showFeedLoadingItemDelayed();
//        }
//    }
//
//    private void showFeedLoadingItemDelayed() {
//        new Handler().postDelayed(new Runnable() {
//            @Override
//            public void run() {
//                rvFeed.smoothScrollToPosition(0);
//                feedAdapter.showLoadingView();
//            }
//        }, 500);
//    }

}
