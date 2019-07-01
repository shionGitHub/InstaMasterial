package com.insta.material.ui.activity;

import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.ImageView;

import com.insta.material.R;

//基类抽取Toolbar
public class BaseActivity extends AppCompatActivity {

    protected Toolbar mToolbar;
    protected ImageView ivLogo;

    protected MenuItem mMenuItem;

    @Override
    public void setContentView(int layoutResID) {
        super.setContentView(layoutResID);
        initView();
        setupToolbar();
    }

    protected void initView() {
        mToolbar = findViewById(R.id.toolbar);
        ivLogo = findViewById(R.id.ivLogo);

    }


    //调用最原始的 super.setContentView(layoutResID);
    public void setContentViewWithOutInject(int layoutResID) {
        super.setContentView(layoutResID);
    }

    //初始化Toolbar
    protected void setupToolbar() {
        if (mToolbar != null) {
            setSupportActionBar(mToolbar);
            mToolbar.setNavigationIcon(R.drawable.ic_menu_white);
        }

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.menu_main, menu);
        mMenuItem = menu.findItem(R.id.action_inbox);
        //自己设置右上角的菜单按钮
        mMenuItem.setActionView(R.layout.menu_item_view);
        return true;
    }
}
