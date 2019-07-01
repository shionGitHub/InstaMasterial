package com.insta.material.ui.view;

import android.content.Context;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;

import com.insta.material.R;
import com.insta.material.Utils;

public class FeedContextMenu extends LinearLayout implements View.OnClickListener {

    private static final int CONTEXT_WIDTH_MENU = Utils.dpToPx(240);

    private Button btnReport, btnSharePhoto, btnCopyShareUrl, btnCancel;

    public FeedContextMenu(Context context) {
        super(context);
        init();
    }

    public FeedContextMenu(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public FeedContextMenu(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public FeedContextMenu(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init();
    }

    private void init() {

        setOrientation(VERTICAL);
        setBackgroundResource(R.drawable.bg_container_shadow);
        inflate(getContext(), R.layout.view_context_menu, this);
        setLayoutParams(new ViewGroup.LayoutParams(CONTEXT_WIDTH_MENU, ViewGroup.LayoutParams.WRAP_CONTENT));

        btnReport = findViewById(R.id.btnReport);
        btnSharePhoto = findViewById(R.id.btnSharePhoto);
        btnCopyShareUrl = findViewById(R.id.btnCopyShareUrl);
        btnCancel = findViewById(R.id.btnCancel);

        btnReport.setOnClickListener(this);
        btnSharePhoto.setOnClickListener(this);
        btnCopyShareUrl.setOnClickListener(this);
        btnCancel.setOnClickListener(this);

    }


    public void dismiss() {
        ViewGroup group = (ViewGroup) getParent();
        if (group != null) {
            group.removeView(this);
        }
    }

    @Override
    public void onClick(View v) {
        if (v == btnReport) {
            if (onFeedContextMenuItemClickListener != null) {
                onFeedContextMenuItemClickListener.onReportClick();
            }
        }
        else if (v == btnSharePhoto) {
            if (onFeedContextMenuItemClickListener != null) {
                onFeedContextMenuItemClickListener.onSharePhotoClick();
            }
        }
        else if (v == btnCopyShareUrl) {
            if (onFeedContextMenuItemClickListener != null) {
                onFeedContextMenuItemClickListener.onCopyShareUrlClick();
            }
        }
        else if (v == btnCancel) {
            if (onFeedContextMenuItemClickListener != null) {
                onFeedContextMenuItemClickListener.onCancelClick();
            }
        }

    }

    OnFeedContextMenuItemClickListener onFeedContextMenuItemClickListener;

    public void setOnFeedContextMenuItemClickListener(OnFeedContextMenuItemClickListener listener) {
        this.onFeedContextMenuItemClickListener = listener;
    }

    public interface OnFeedContextMenuItemClickListener {
        public void onReportClick();

        public void onSharePhotoClick();

        public void onCopyShareUrlClick();

        public void onCancelClick();
    }


}
