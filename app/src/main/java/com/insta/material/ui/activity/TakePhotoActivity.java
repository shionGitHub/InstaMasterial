package com.insta.material.ui.activity;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.hardware.Camera;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.Interpolator;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ViewSwitcher;

import com.insta.material.R;
import com.insta.material.Utils;
import com.insta.material.ui.adapter.PhotoFiltersAdapter;
import com.insta.material.ui.view.RevealBackgroundView;
import com.wonderkiln.camerakit.CameraKitError;
import com.wonderkiln.camerakit.CameraKitEvent;
import com.wonderkiln.camerakit.CameraKitEventListener;
import com.wonderkiln.camerakit.CameraKitEventListenerAdapter;
import com.wonderkiln.camerakit.CameraKitImage;
import com.wonderkiln.camerakit.CameraKitVideo;
import com.wonderkiln.camerakit.CameraView;

import java.io.File;
import java.io.FileOutputStream;
import java.util.Set;

public class TakePhotoActivity extends BaseActivity
        implements RevealBackgroundView.OnStateChangeListener, View.OnClickListener {

    private static final String ARG_ARRAY_POSITION = "ARG_ARRAY_POSITION";

    private static final Interpolator ACCELERATE_INTERPOLATOR = new AccelerateInterpolator();
    private static final Interpolator DECELERATE_INTERPOLATOR = new DecelerateInterpolator();

    private static final int STATE_TAKE_PHOTO = 0;
    private static final int STATE_SETUP_PHOTO = 1;

    RevealBackgroundView vRevealBackground;

    ViewSwitcher vUpperPanel;
    ImageButton btnAccept;

    View vTakePhotoRoot;
    CameraView cameraView;
    ImageView ivTakenPhoto;
    View vShutter;

    ViewSwitcher vLowerPanel;
    RecyclerView rvFilters;
    Button btnTakePhoto;

    private int[] location;
    private int currentState = STATE_TAKE_PHOTO;

    public static void show(Context context, int[] arr) {
        Intent intent = new Intent(context, TakePhotoActivity.class);
        intent.putExtra(ARG_ARRAY_POSITION, arr);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_take_photo);
        if (Utils.isAndroid5()) {
            getWindow().setStatusBarColor(0xff111111);
        }
        vRevealBackground = findViewById(R.id.vRevealBackgroundView);

        vUpperPanel = findViewById(R.id.vUpperPanel);
        btnAccept = findViewById(R.id.btnAccept);

        vTakePhotoRoot = findViewById(R.id.vPhotoRoot);
        cameraView = findViewById(R.id.cameraView);
        ivTakenPhoto = findViewById(R.id.ivTakenPhoto);
        vShutter = findViewById(R.id.vShutter);

        vLowerPanel = findViewById(R.id.vLowerPanel);
        rvFilters = findViewById(R.id.rvFilters);
        btnTakePhoto = findViewById(R.id.btnTakePhoto);

        btnAccept.setOnClickListener(this);
        btnTakePhoto.setOnClickListener(this);

        setupRevealBackground();
        setupPhotoFilters();
        cameraView.addCameraKitListener(new CameraKitEventListenerAdapter() {
            @Override
            public void onImage(CameraKitImage image) {
                Log.e("1234", "------------------------onImage");
                showTakenPicture(image.getBitmap());
                saveBitmapToSDCard(image.getJpeg());
            }
        });
    }

    private File photoPath;

    private void saveBitmapToSDCard(final byte[] capturedImage) {
        File savedPhoto = new File(getExternalFilesDir("pushi"), "photo.jpg");
        try {
            FileOutputStream outputStream = new FileOutputStream(savedPhoto.getPath());
            outputStream.write(capturedImage);
            outputStream.close();
            photoPath = savedPhoto;
        } catch (java.io.IOException e) {
            e.printStackTrace();
        }
    }

    private void setupRevealBackground() {
        vRevealBackground.setFillPaintColor(0xFF16181a);
        vRevealBackground.setOnStateChangeListener(this);
        location = getIntent().getIntArrayExtra(ARG_ARRAY_POSITION);
        vRevealBackground.getViewTreeObserver().addOnPreDrawListener(
                new ViewTreeObserver.OnPreDrawListener() {
                    @Override
                    public boolean onPreDraw() {
                        vRevealBackground.getViewTreeObserver().removeOnPreDrawListener(this);
                        vRevealBackground.setStartPosition(location);
                        return false;
                    }
                }
        );

    }

    private void setupPhotoFilters() {
        PhotoFiltersAdapter photoFiltersAdapter = new PhotoFiltersAdapter(this);
        rvFilters.setHasFixedSize(true);
        rvFilters.setAdapter(photoFiltersAdapter);
        rvFilters.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
    }

    @Override
    public void onStateChange(int state) {
        if (state == RevealBackgroundView.STATE_FINISHED) {
            vLowerPanel.setVisibility(View.VISIBLE);
            vUpperPanel.setVisibility(View.VISIBLE);
            vTakePhotoRoot.setVisibility(View.VISIBLE);
            startIntroAnimation();

        }
        else {
            cameraView.setVisibility(View.INVISIBLE);
            vLowerPanel.setVisibility(View.INVISIBLE);
            vUpperPanel.setVisibility(View.INVISIBLE);
            vTakePhotoRoot.setVisibility(View.INVISIBLE);
            btnTakePhoto.setEnabled(false);
        }
    }

    private void startIntroAnimation() {
        vUpperPanel.setTranslationY(-vUpperPanel.getHeight());
        vLowerPanel.setTranslationY(vLowerPanel.getHeight());
        vUpperPanel.animate().translationY(0).setDuration(400).setInterpolator(DECELERATE_INTERPOLATOR).start();
        vLowerPanel.animate().translationY(0).setDuration(400).setInterpolator(DECELERATE_INTERPOLATOR)
                .setListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        cameraView.setVisibility(View.VISIBLE);
                        isShowCamera = true;
                        cameraView.start();
                        btnTakePhoto.setEnabled(true);
                    }
                }).start();

    }

    private boolean isShowCamera = false;

    @Override
    protected void onResume() {
        super.onResume();
        if (isShowCamera) {
            cameraView.start();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (isShowCamera) {
            cameraView.stop();
        }
    }

    private void showTakenPicture(Bitmap bitmap) {
        vUpperPanel.showNext();
        vLowerPanel.showNext();
        ivTakenPhoto.setImageBitmap(bitmap);
        updateState(STATE_SETUP_PHOTO);
    }

    private void updateState(int state) {
        this.currentState = state;
        if (state == STATE_SETUP_PHOTO) {
            vUpperPanel.setInAnimation(this, R.anim.slide_in_from_left);
            vLowerPanel.setInAnimation(this, R.anim.slide_in_from_left);
            vUpperPanel.setOutAnimation(this, R.anim.slide_out_to_right);
            vLowerPanel.setOutAnimation(this, R.anim.slide_out_to_right);
            ivTakenPhoto.setVisibility(View.VISIBLE);
        }
        else {
            vUpperPanel.setInAnimation(this, R.anim.slide_in_from_right);
            vLowerPanel.setInAnimation(this, R.anim.slide_in_from_right);
            vUpperPanel.setOutAnimation(this, R.anim.slide_out_to_left);
            vLowerPanel.setOutAnimation(this, R.anim.slide_out_to_left);
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    ivTakenPhoto.setVisibility(View.GONE);
                }
            }, 400);
        }
    }

    @Override
    public void onClick(View v) {
        if (v == btnAccept) {
            if (photoPath != null && photoPath.exists()) {
                Log.e("1234", "-------------------path:\n" + photoPath.getAbsolutePath());
                PublishActivity.openWithPhotoUri(this, Uri.fromFile(photoPath));
                overridePendingTransition(0, 0);
            }
        }
        else if (v == btnTakePhoto) {
            cameraView.captureImage();
            animateShutter();
        }
    }

    private void animateShutter() {
        vShutter.setVisibility(View.VISIBLE);
        vShutter.setAlpha(0.f);

        ObjectAnimator alphaInAnim = ObjectAnimator.ofFloat(vShutter, "alpha", 0f, 0.8f);
        alphaInAnim.setDuration(100);
        alphaInAnim.setStartDelay(100);
        alphaInAnim.setInterpolator(ACCELERATE_INTERPOLATOR);

        ObjectAnimator alphaOutAnim = ObjectAnimator.ofFloat(vShutter, "alpha", 0.8f, 0f);
        alphaOutAnim.setDuration(200);
        alphaOutAnim.setInterpolator(DECELERATE_INTERPOLATOR);

        AnimatorSet animatorSet = new AnimatorSet();
        animatorSet.playSequentially(alphaInAnim, alphaOutAnim);
        animatorSet.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                vShutter.setVisibility(View.GONE);
            }
        });
        animatorSet.start();
    }


    @Override
    public void onBackPressed() {
        if (currentState == STATE_SETUP_PHOTO) {
            btnTakePhoto.setEnabled(true);
            vUpperPanel.showNext();
            vLowerPanel.showNext();
            updateState(STATE_TAKE_PHOTO);
        }
        else {
            super.onBackPressed();
        }
    }

}
