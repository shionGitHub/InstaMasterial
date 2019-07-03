package com.insta.material.ui.adapter;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.DecelerateInterpolator;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.insta.material.R;

import de.hdodenhof.circleimageview.CircleImageView;

public class CommentAdapter extends RecyclerView.Adapter<CommentAdapter.CommentHolder> {

    private int itemCount;

    public CommentAdapter() {

    }

    @NonNull
    @Override
    public CommentHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        LayoutInflater inflater = LayoutInflater.from(viewGroup.getContext());
        View view = inflater.inflate(R.layout.item_comment, viewGroup, false);
        return new CommentHolder(view);
    }


    @Override
    public void onBindViewHolder(@NonNull CommentHolder holder, int pos) {
        Log.e("CommentAdapter", "------------------pos: " + pos);
        runEnterAnimation(holder, pos);
        Glide.with(holder.itemView.getContext())
                .load(R.drawable.ic_launcher)
                .centerCrop()
                .into(holder.image);
        switch (pos % 3) {
            case 0:
                holder.text.setText("中华人民共和国万岁！中华人民共和国万岁！中华人民共和国万岁！");
                break;
            case 1:
                holder.text.setText("北京将推动垃圾分类立法，罚款不低于上海。");
                break;
            case 2:
                holder.text.setText("璞石科技（深圳）璞石科技（深圳）璞石科技（深圳）");
                break;
        }

    }

    private int lastAnimationPosition = -1;//最后一个做动画的位置
    public boolean lockAnimation = false;//是否锁定动画

    private void runEnterAnimation(CommentHolder holder, int position) {
        if (lockAnimation) {
            return;
        }

        if (lastAnimationPosition < position) {
            lastAnimationPosition = position;
            holder.itemView.setAlpha(0.0f);
            holder.itemView.setTranslationY(100);
            holder.itemView.animate()
                    .translationY(0)
                    .alpha(1.0f)
                    .setInterpolator(new DecelerateInterpolator(2.0f))
                    .setDuration(300)
                    .setStartDelay(0)
                    .setListener(new AnimatorListenerAdapter() {
                        @Override
                        public void onAnimationEnd(Animator animation) {
                            lockAnimation = true;
                        }
                    })
                    .start();

        }
    }

    @Override
    public int getItemCount() {
        return itemCount;
    }

    public void updateItem() {
        itemCount = 10;
        notifyDataSetChanged();
    }

    public void addItem() {
        itemCount++;
        notifyItemInserted(itemCount - 1);
    }

    public static class CommentHolder extends RecyclerView.ViewHolder {
        public CircleImageView image;
        public TextView text;

        public CommentHolder(@NonNull View itemView) {
            super(itemView);
            image = itemView.findViewById(R.id.image);
            text = itemView.findViewById(R.id.text);
        }
    }

}
