package com.example.testproject.recyclerview;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.testproject.R;

/**
 * RecyclerViewHolder
 * created by xiaojun at 2020/3/9
 */
public class RecyclerViewHolder extends RecyclerView.ViewHolder {

    public TextView mTv;
    public ImageView mIv;

    public RecyclerViewHolder(@NonNull View itemView) {
        super(itemView);
        mTv = itemView.findViewById(R.id.item_layout_tv);
        mIv = itemView.findViewById(R.id.item_layout_iv);
    }
}
