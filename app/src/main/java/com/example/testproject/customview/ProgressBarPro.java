package com.example.testproject.customview;

import android.content.Context;
import android.graphics.Canvas;
import android.util.AttributeSet;
import android.view.View;

import androidx.annotation.Nullable;

/**
 * 用图片作为进度条进度提示条的进度条
 * created by xiaojun at 2019/12/13
 */
public class ProgressBarPro extends View {

    public ProgressBarPro(Context context) {
        this(context,null);
    }

    public ProgressBarPro(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs,0);
    }

    public ProgressBarPro(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

    }
}
