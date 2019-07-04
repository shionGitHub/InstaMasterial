package com.insta.material.ui.activity;

import android.app.Activity;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.Menu;
import android.view.MenuItem;
import android.view.ViewTreeObserver;
import android.view.animation.OvershootInterpolator;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.ToggleButton;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.insta.material.R;
import com.insta.material.Utils;

public class PublishActivity extends BaseActivity implements CompoundButton.OnCheckedChangeListener {

    public static final String ARG_TAKEN_PHOTO_URI = "arg_taken_photo_uri";

    private Uri photoUri;

    ToggleButton tbFollowers;
    ToggleButton tbDirect;
    ImageView ivPhoto;

    public static void openWithPhotoUri(Activity openingActivity, Uri photoUri) {
        Intent intent = new Intent(openingActivity, PublishActivity.class);
        intent.putExtra(ARG_TAKEN_PHOTO_URI, photoUri);
        openingActivity.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_publish);
        mToolbar.setNavigationIcon(R.drawable.ic_arrow_back_grey600_24dp);

        if (savedInstanceState == null) {
            photoUri = getIntent().getParcelableExtra(ARG_TAKEN_PHOTO_URI);
        }
        else {
            photoUri = savedInstanceState.getParcelable(ARG_TAKEN_PHOTO_URI);
        }
        if (Utils.isAndroid5()) {
            getWindow().setStatusBarColor(0xff888888);
        }
        tbFollowers = findViewById(R.id.tbFollowers);
        tbDirect = findViewById(R.id.tbDirect);
        ivPhoto = findViewById(R.id.ivPhoto);
        ivPhoto.getViewTreeObserver().addOnPreDrawListener(
                new ViewTreeObserver.OnPreDrawListener() {
                    @Override
                    public boolean onPreDraw() {
                        ivPhoto.getViewTreeObserver().removeOnPreDrawListener(this);
                        loadThumbnailPhoto();
                        return true;
                    }
                });
        tbFollowers.setOnCheckedChangeListener(this);
        tbDirect.setOnCheckedChangeListener(this);
    }

    private void loadThumbnailPhoto() {
        ivPhoto.setScaleX(0);
        ivPhoto.setScaleY(0);
        Glide.with(this)
                .load(photoUri)
                .centerCrop()
                .listener(new RequestListener<Drawable>() {
                    @Override
                    public boolean onLoadFailed(@Nullable GlideException e,
                                                Object model,
                                                Target<Drawable> target,
                                                boolean isFirstResource) {
                        return false;
                    }

                    @Override
                    public boolean onResourceReady(Drawable resource,
                                                   Object model,
                                                   Target<Drawable> target,
                                                   DataSource dataSource,
                                                   boolean isFirstResource) {
                        ivPhoto.animate()
                                .scaleX(1.f).scaleY(1.f)
                                .setInterpolator(new OvershootInterpolator())
                                .setDuration(400)
                                .setStartDelay(200)
                                .start();
                        return false;
                    }
                })
                .into(ivPhoto);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_publish, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.action_publish) {
            bringMainActivityToTop();
            return true;
        }
        else {
            return super.onOptionsItemSelected(item);
        }
    }

    private void bringMainActivityToTop() {
        Intent intent = new Intent(this, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        intent.setAction(MainActivity.ACTION_SHOW_LOADING_ITEM);
        startActivity(intent);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelable(ARG_TAKEN_PHOTO_URI, photoUri);
    }

    private boolean propagatingToggleState = false;//防止连续点击

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean checked) {
        if (buttonView == tbDirect) {
            if (!propagatingToggleState) {
                propagatingToggleState = true;
                tbFollowers.setChecked(!checked);
                propagatingToggleState = false;
            }
        }
        else if (buttonView == tbFollowers) {
            if (!propagatingToggleState) {
                propagatingToggleState = true;
                tbDirect.setChecked(!checked);
                propagatingToggleState = false;
            }
        }

    }
}
