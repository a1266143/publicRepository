package com.example.testproject.customview;

import android.animation.Animator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.DecelerateInterpolator;

import androidx.annotation.Nullable;

import java.util.ArrayList;
import java.util.List;

/**
 * 连接扫描View
 * created by xiaojun at 2020/5/8
 */
public class ScanView2 extends View {

    private Paint mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);

    private int mNumberOfLine;//线段数量
    private float mHeightOfLineFirst;//第一条线段高度
    private float mHeightOfLineOther;//其它线段高度
    private List<Line> mLines = new ArrayList<>();
    private int mHeight;

    public ScanView2(Context context) {
        this(context, null);
    }

    public ScanView2(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ScanView2(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        mPaint.setColor(Color.parseColor("#569aff"));
        mPaint.setStyle(Paint.Style.STROKE);

        mHeightOfLineFirst = Utils.dp2px(getContext(), 4);
        mHeightOfLineOther = Utils.dp2px(getContext(), 2);
    }

    public void startAnimation(){
        for (int i = 0; i < mLines.size(); i++) {
            Line line = mLines.get(i);
            ValueAnimator animator = ValueAnimator.ofFloat(mHeight,0);
            animator.addUpdateListener(animation -> {
                line.startY = (float) animation.getAnimatedValue();
                line.stopY = (float) animation.getAnimatedValue();
                invalidate();
            });
//            animator.setInterpolator(new AccelerateDecelerateInterpolator());
            animator.setDuration(1700);
            animator.setRepeatMode(ValueAnimator.REVERSE);
            animator.setRepeatCount(ValueAnimator.INFINITE);
            animator.setStartDelay(8*i);
            animator.start();
        }
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        mHeight = h;
        int numberOfOtherLine = (int) (h / 2.f / mHeightOfLineOther / 2);
        int numberOfFirstLine = 1;
        mNumberOfLine = numberOfOtherLine + numberOfFirstLine;
        //初始化所有线段的初始位置
        for (int i = 0; i < mNumberOfLine; i++) {
            Line line = new Line();
            line.startX = 0;
            line.stopX = w;
            line.alpha = (int) (1.f*(mNumberOfLine-i)/mNumberOfLine*255);
            line.startY = h;
            line.stopY = h;
            if (i == 0){
                line.strokeWidth = Utils.dp2px(getContext(),4);
                line.offsetY = 0;
            }else{
                line.strokeWidth = Utils.dp2px(getContext(),2);
                line.offsetY = i*line.strokeWidth;
            }
            mLines.add(line);
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        for (int i = 0; i < mLines.size(); i++) {
            Line line = mLines.get(i);
            mPaint.setAlpha(line.alpha);
            mPaint.setStrokeWidth(line.strokeWidth);
            canvas.drawLine(line.startX,line.startY,line.stopX,line.stopY,mPaint);
        }
    }

    /**
     * 线段对象
     */
    class Line {
        public float startX, startY, stopX, stopY,strokeWidth;
        public float offsetY;
        public int alpha;//0~255
    }

}
