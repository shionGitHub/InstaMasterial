package com.insta.material.ui.activity;

import android.os.Handler;
import android.os.Looper;
import android.support.design.widget.NavigationView;
import android.support.v4.widget.DrawerLayout;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.bumptech.glide.Glide;
import com.insta.material.R;

import de.hdodenhof.circleimageview.CircleImageView;

//抽象出Drawer侧滑的基类
public class BaseDrawerActivity extends BaseActivity {

    protected DrawerLayout mDrawerLayout;
    protected NavigationView mNavigationView;

    protected CircleImageView ivMenuUserProfilePhoto;

    protected Handler mHandler = new Handler(Looper.getMainLooper());

    @Override
    public void setContentView(int layoutResID) {
        setContentViewWithOutInject(R.layout.activity_drawer);
        ViewGroup viewGroup = findViewById(R.id.flContentRoot);
        LayoutInflater.from(this).inflate(layoutResID, viewGroup, true);
        initView();
        setupToolbar();
        setupHeader();
    }

    @Override
    protected void initView() {
        super.initView();
        mDrawerLayout = findViewById(R.id.drawerLayout);
        mNavigationView = findViewById(R.id.vNavigation);
        ivMenuUserProfilePhoto = findViewById(R.id.ivMenuUserProfilePhoto);
    }

    @Override
    protected void setupToolbar() {
        super.setupToolbar();
        if (mToolbar != null) {
            mToolbar.setNavigationOnClickListener(
                    new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            mDrawerLayout.openDrawer(Gravity.START);
                        }
                    });
        }

    }

    protected void setupHeader() {
        View headerView = mNavigationView.getHeaderView(0);

        //顶部菜单
        View menuHeader = headerView.findViewById(R.id.vGlobalMenuHeader);
        menuHeader.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onGlobalMenuHeaderClick(v);
            }
        });

        ivMenuUserProfilePhoto = headerView.findViewById(R.id.ivMenuUserProfilePhoto);

        Glide.with(this)
                .load(getString(R.string.user_profile_photo))
                .centerCrop()
                .into(ivMenuUserProfilePhoto);
    }

    //顶部菜单被点击的时候
    public void onGlobalMenuHeaderClick(final View v) {
        mDrawerLayout.closeDrawer(Gravity.START);
        mHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                //跳转到用户头像的页面
                int[] outLocation = new int[2];
                v.getLocationOnScreen(outLocation);
                outLocation[0] += v.getWidth() / 2;
                UserProfileActivity.show(v.getContext(), outLocation);
                overridePendingTransition(0, 0);
            }
        }, 200);

    }


}
