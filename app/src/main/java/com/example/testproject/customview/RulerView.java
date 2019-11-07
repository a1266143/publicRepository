package com.example.testproject.customview;

import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

import androidx.annotation.Nullable;

import com.example.testproject.customview.ruler.Utils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

/**
 * 尺子View
 * created by xiaojun at 2019/11/7
 */
public class RulerView extends View {

    private Paint mPaintDividing;//刻度画笔
    private Paint mPaintText;//刻度值画笔
    private Paint mPaintTriangle;//三角形画笔
    private List<String> mListData;//数据集
    private List<Integer> mListIndexOnScreen;//显示在屏幕上的数据集的索引
    private List<Float> mListPositionXOnScreen;//显示在屏幕上的数据集的x轴的位置

    private int mWidthView, mHeightView;
    private float mWidthText, mHeightText;

    private float mHeightDividing, mHeightDividCenter;

    public RulerView(Context context) {
        this(context, null);
    }

    public RulerView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public RulerView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        mPaintDividing = new Paint(Paint.ANTI_ALIAS_FLAG);
        mPaintDividing.setStyle(Paint.Style.STROKE);
        mPaintDividing.setStrokeWidth(6);
        mPaintDividing.setColor(Color.parseColor("#DDDDDD"));

        mPaintText = new Paint(Paint.ANTI_ALIAS_FLAG);
        mPaintText.setTextSize(40);
        mPaintText.setColor(Color.WHITE);
        mPaintText.setTextAlign(Paint.Align.CENTER);

        mPaintTriangle = new Paint(Paint.ANTI_ALIAS_FLAG);
        mPaintTriangle.setStyle(Paint.Style.STROKE);
        mPaintTriangle.setStrokeWidth(10);
        mPaintTriangle.setColor(Color.RED);

        mListData = new ArrayList<>();
        for (int i = 0; i < 1000; i++) {
            mListData.add(i + "Hz");
        }

        mListIndexOnScreen = new ArrayList<>();
        mListPositionXOnScreen = new ArrayList<>();

        mAnimator = new ValueAnimator();
        mAnimator.addUpdateListener(animation -> {
            mOffsetX = (float) animation.getAnimatedValue();
            calculatePosition();
            invalidate();
        });

        calculatePosition();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        this.mWidthView = w;
        this.mHeightView = h;
    }

    private float mLastTouchX;

    List<Float> mListOffset = new ArrayList<>();

    /**
     * 找出距离中点最短的点的索引
     *
     * @return
     */
    private int findShortestIndex() {
        mListOffset.clear();
        for (int i = 0; i < mListPositionXOnScreen.size(); i++) {
            float valueX = mListPositionXOnScreen.get(i);
            float distance = Math.abs(valueX - mWidthView / 2.f);//每个点距离中点的距离
            mListOffset.add(distance);
        }
        return Utils.getMinValueIndex(mListOffset);
    }

    /**
     * 计算每一个line的坐标
     */
    private void calculatePosition() {
        mListIndexOnScreen.clear();
        mListPositionXOnScreen.clear();
        mListPositionXOnScreenSmall.clear();
        for (int i = 0; i < mListData.size(); i++) {
            mPaintDividing.setStrokeWidth(10);
            float length = i * 5 * mWidthBetweenDivid;
            float xBig = mWidthView / 2.f + length + mOffsetX;
            if (xBig >= 0 && xBig <= mWidthView) {//只画屏幕上显示的
                mListIndexOnScreen.add(i);//保存显示在屏幕上的View的索引
                mListPositionXOnScreen.add(xBig);//保存显示在屏幕上的View的x轴的距离
            }
            if (i < mListData.size()) {
                mPaintDividing.setStrokeWidth(6);
                for (int j = 0; j < 4; j++) {
                    float subLength = length + mWidthBetweenDivid * (j + 1);
                    float xSmall = mWidthView / 2.f + subLength + mOffsetX;
                    if (xSmall >= 0 && xSmall <= mWidthView) {//只画屏幕上显示的{
                        mListPositionXOnScreenSmall.add(xSmall);
                    }
                }

            }
        }

    }

    private ValueAnimator mAnimator;

    @Override
    public synchronized boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction() & MotionEvent.ACTION_MASK) {
            case MotionEvent.ACTION_DOWN:
                mLastTouchX = event.getX();
                break;
            case MotionEvent.ACTION_MOVE:
                float x = event.getX();
                mOffsetX = mOffsetX + (x - mLastTouchX);
                mLastTouchX = event.getX();
                calculatePosition();
                invalidate();
                break;
            case MotionEvent.ACTION_UP:
                int index = findShortestIndex();
                animationFingerUp(index);
                break;
        }
        return true;
    }

    private void animationFingerUp(int index) {
        float positionX = mListPositionXOnScreen.get(index);
        float offsetX = positionX - mWidthView / 2.f;
        if (offsetX < 0)
            offsetX = -offsetX;
        else if (offsetX > 0)
            offsetX = -offsetX;
        if (offsetX != 0) {
            mAnimator.cancel();
            mAnimator.setDuration(300);
            mAnimator.setFloatValues(mOffsetX, mOffsetX + offsetX);
            mAnimator.start();
            //找出屏幕上离中间线最近的线段，然后通过动画滑动到中间
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        drawDividing2(canvas);
        drawText(canvas);
        drawCenterTriangle(canvas);
    }

    private float mOffsetX;//尺子整体偏移量
    private float mBigDividHeight = 200;//大刻度高度
    private float mSmallDividHeight = 100;//小刻度高度

    private float mWidthBetweenDivid = 50;//刻度尺之间的距离

    private void drawDividing2(Canvas canvas) {
        for (int i = 0; i < mListPositionXOnScreen.size(); i++) {
            float xBig = mListPositionXOnScreen.get(i);
            canvas.drawLine(xBig, 0, xBig, mBigDividHeight, mPaintDividing);//画长刻度
        }
        for (int i = 0; i < mListPositionXOnScreenSmall.size(); i++) {
            float xSmall = mListPositionXOnScreenSmall.get(i);
            canvas.drawLine(xSmall, 0, xSmall, mSmallDividHeight, mPaintDividing);
        }
    }

    private List<Float> mListPositionXOnScreenSmall = new ArrayList<>();

    /**
     * 画刻度
     *
     * @param canvas
     */
    private void drawDividing(Canvas canvas) {
        mListIndexOnScreen.clear();
        mListPositionXOnScreen.clear();
        mListPositionXOnScreenSmall.clear();
        for (int i = 0; i < mListData.size(); i++) {
            mPaintDividing.setStrokeWidth(10);
            float length = i * 5 * mWidthBetweenDivid;
            float xBig = mWidthView / 2.f + length + mOffsetX;
            if (xBig >= 0 && xBig <= mWidthView) {//只画屏幕上显示的
                canvas.drawLine(xBig, 0, xBig, mBigDividHeight, mPaintDividing);
                mListIndexOnScreen.add(i);//保存显示在屏幕上的View的索引
                mListPositionXOnScreen.add(xBig);//保存显示在屏幕上的View的x轴的距离
            }
            if (i < mListData.size()) {
                mPaintDividing.setStrokeWidth(6);
                for (int j = 0; j < 4; j++) {
                    float subLength = length + mWidthBetweenDivid * (j + 1);
                    float xSmall = mWidthView / 2.f + subLength + mOffsetX;
                    if (xSmall >= 0 && xSmall <= mWidthView) {//只画屏幕上显示的{
                        canvas.drawLine(xSmall, 0, xSmall, mSmallDividHeight, mPaintDividing);
                        mListPositionXOnScreenSmall.add(xSmall);
                    }
                }

            }
        }

    }

    private void drawText(Canvas canvas) {
        for (int i = 0; i < mListData.size(); i++) {
            float length = i * 5 * mWidthBetweenDivid;
            float xText = mWidthView / 2.f + length + mOffsetX;
            if (xText >= 0 && xText <= mWidthView)//只画显示在屏幕上
                canvas.drawText(mListData.get(i), xText, mBigDividHeight + 50, mPaintText);
        }
    }

    private void drawCenterTriangle(Canvas canvas) {
        canvas.drawLine(mWidthView / 2.f, 0, mWidthView / 2.f, 30, mPaintTriangle);
    }
}
