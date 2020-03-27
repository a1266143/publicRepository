package com.example.testproject.popupwindow;

import android.content.Context;
import android.graphics.Color;
import android.util.AttributeSet;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import androidx.annotation.Nullable;

import com.example.testproject.R;

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

    /**
     * 这里需要注意Padding和Margin值
     * 此ViewGroup的Padding
     * 此ViewGroup的Margin
     * 子View的Margin
     * 子View的Padding
     * @param widthMeasureSpec
     * @param heightMeasureSpec
     */
    /*@Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        Log.e("xiaojun","onMeasure=====");
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int width = 0;
        int totalHeight = 0;
        mArrowsView.measure(widthMeasureSpec,heightMeasureSpec);
        //在进行测量的时候需要加上三角形的高度,这样才能返回正确的此ViewGroup的正确高度
        for (int i = 0; i < getChildCount(); i++) {
            View view = getChildAt(i);
            //先测量子View
            measureChild(view,widthMeasureSpec,heightMeasureSpec);
            if (view.getMeasuredWidth()>width)
                width = view.getMeasuredWidth();
            //获取最大的一个子View的宽度为默认宽度
            int viewHeight = view.getMeasuredHeight();
            totalHeight+=viewHeight;
            if (i==getChildCount()-1){
                totalHeight += mArrowsView.getMeasuredHeight();
            }
        }

        setMeasuredDimension(width,totalHeight);
    }*/


    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        Log.e("xiaojun","onFinishInflate=====");
        addView(mArrowsView);
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        Log.e("xiaojun","onLayout======");
        super.onLayout(changed, l, t, r, b);

    }


}
