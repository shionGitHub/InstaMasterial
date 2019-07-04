package com.insta.material.ui.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.TabLayout;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.Interpolator;
import android.widget.LinearLayout;

import com.bumptech.glide.Glide;
import com.insta.material.R;
import com.insta.material.ui.adapter.UserProfileAdapter;
import com.insta.material.ui.view.RevealBackgroundView;

import de.hdodenhof.circleimageview.CircleImageView;


/**
 * 头部的菜单的View----300ms ------移动
 * 头像 300ms 减速 延迟100ms  ---------------移动
 * 用户身份的信息 300ms 减速 延迟200ms---------移动
 * 用户的状态  200ms  减速 延迟400ms --------渐变
 * <p>
 * TAB的动画-----移动 300ms 减速
 * <p>
 * 内容RecyclerView的逐渐  (从小------>变大)
 */
public class UserProfileActivity extends BaseDrawerActivity
        implements RevealBackgroundView.OnStateChangeListener {

    private Interpolator INTERPOLATOR = new DecelerateInterpolator();

    public static final String ARG_ARRAY_POSITION = "ARG_ARRAY_POSITION";
    private int[] location;
    private AppBarLayout appBarLayout;
    private RevealBackgroundView revealBackgroundView;
    private LinearLayout vUserProfileRoot;
    private CircleImageView ivUserProfilePhoto;
    private LinearLayout vUserDetails;
    private LinearLayout vUserStats;
    private TabLayout tlUserProfileTabs;
    private RecyclerView recycler;

    public static void show(Context context, int[] arr) {
        Intent intent = new Intent(context, UserProfileActivity.class);
        intent.putExtra(ARG_ARRAY_POSITION, arr);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_profile);
        location = getIntent().getIntArrayExtra(ARG_ARRAY_POSITION);
        appBarLayout = findViewById(R.id.appBarLayout);
        revealBackgroundView = findViewById(R.id.vRevealBackgroundView);
        vUserProfileRoot = findViewById(R.id.vUserProfileRoot);
        ivUserProfilePhoto = findViewById(R.id.ivUserProfilePhoto);
        vUserDetails = findViewById(R.id.vUserDetails);
        vUserStats = findViewById(R.id.vUserStats);
        tlUserProfileTabs = findViewById(R.id.tlUserProfileTabs);
        recycler = findViewById(R.id.recycler);

        recycler.setLayoutManager(new GridLayoutManager(this, 3));
        recycler.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                if (newState == RecyclerView.SCROLL_STATE_DRAGGING) {

                }
            }
        });

        tlUserProfileTabs.addTab(tlUserProfileTabs.newTab().setIcon(R.drawable.ic_grid_on_white));
        tlUserProfileTabs.addTab(tlUserProfileTabs.newTab().setIcon(R.drawable.ic_list_white));
        tlUserProfileTabs.addTab(tlUserProfileTabs.newTab().setIcon(R.drawable.ic_place_white));
        tlUserProfileTabs.addTab(tlUserProfileTabs.newTab().setIcon(R.drawable.ic_label_white));


        String iconUrl = getResources().getString(R.string.user_profile_photo);
        int avatorSize = (int) getResources().getDimension(R.dimen.user_profile_avatar_size);
        Glide.with(this)
                .load(iconUrl)
                .override(avatorSize, avatorSize)
                .centerCrop()
                .into(ivUserProfilePhoto);

        revealBackgroundView.setOnStateChangeListener(this);
        revealBackgroundView.getViewTreeObserver()
                .addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {
                    @Override
                    public boolean onPreDraw() {
                        revealBackgroundView.getViewTreeObserver().removeOnPreDrawListener(this);
                        revealBackgroundView.setStartPosition(location);
                        return false;
                    }
                });

    }


    @Override
    public void onStateChange(int state) {
        if (state == RevealBackgroundView.STATE_FINISHED) {
            appBarLayout.setVisibility(View.VISIBLE);
            vUserProfileRoot.setVisibility(View.VISIBLE);
            tlUserProfileTabs.setVisibility(View.VISIBLE);
            recycler.setVisibility(View.VISIBLE);

            UserProfileAdapter adapter = new UserProfileAdapter(this);
            recycler.setAdapter(adapter);

            animateUserProfileOptions();
            animateUserProfileHeader();
        }
        else {
            appBarLayout.setVisibility(View.INVISIBLE);
            vUserProfileRoot.setVisibility(View.INVISIBLE);
            tlUserProfileTabs.setVisibility(View.INVISIBLE);
            recycler.setVisibility(View.INVISIBLE);
        }

    }

    private void animateUserProfileHeader() {
        tlUserProfileTabs.setTranslationY(-tlUserProfileTabs.getHeight());
        tlUserProfileTabs.animate()
                .translationY(0)
                .setDuration(300)
                .setInterpolator(INTERPOLATOR)
                .setStartDelay(400)
                .start();
    }

    private void animateUserProfileOptions() {
        vUserProfileRoot.setTranslationY(-vUserProfileRoot.getHeight());
        ivUserProfilePhoto.setTranslationY(-ivUserProfilePhoto.getHeight());
        vUserDetails.setTranslationY(-vUserDetails.getHeight());
        vUserStats.setAlpha(0.0f);

        vUserProfileRoot.animate().translationY(0.0f).setInterpolator(INTERPOLATOR).setDuration(300).start();
        ivUserProfilePhoto.animate().translationY(0.0f).setInterpolator(INTERPOLATOR).setDuration(300).setStartDelay(100).start();
        vUserDetails.animate().translationY(0.0f).setInterpolator(INTERPOLATOR).setDuration(300).setStartDelay(200).start();
        vUserStats.animate().alpha(1.0f).setDuration(300).setInterpolator(INTERPOLATOR).setStartDelay(400).start();

    }

}
