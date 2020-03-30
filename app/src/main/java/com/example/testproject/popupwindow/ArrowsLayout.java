package com.example.testproject.popupwindow;

import android.content.Context;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import androidx.annotation.Nullable;

/**
 * 箭头布局
 * 目前暂时加在下面，以后扩展
 * created by xiaojun at 2020/3/27
 */
public class ArrowsLayout extends LinearLayout {

    private ArrowsView mArrowsView;

    public ArrowsLayout(Context context) {
        this(context, null);
    }

    public ArrowsLayout(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ArrowsLayout(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mArrowsView = new ArrowsView(context);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        params.gravity = Gravity.CENTER_HORIZONTAL;
        mArrowsView.setLayoutParams(params);
        setOrientation(VERTICAL);
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        addView(mArrowsView);
    }

}
