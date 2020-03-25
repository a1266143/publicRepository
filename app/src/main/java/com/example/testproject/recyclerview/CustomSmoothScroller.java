package com.example.testproject.recyclerview;

import android.content.Context;

import androidx.recyclerview.widget.LinearSmoothScroller;

/**
 * 自定义SmoothScroller以让 {@link CenterShowLayoutManager}
 * 支持{#smoothScrollToPosition}方法
 *
 * created by xiaojun at 2020/3/23
 */
public class CustomSmoothScroller extends LinearSmoothScroller {


    public CustomSmoothScroller(Context context) {
        super(context);
    }

    @Override
    public int calculateDtToFit(int viewStart, int viewEnd, int boxStart, int boxEnd, int snapPreference) {
        return (boxStart + (boxEnd - boxStart) / 2) - (viewStart + (viewEnd - viewStart) / 2);
    }
}
