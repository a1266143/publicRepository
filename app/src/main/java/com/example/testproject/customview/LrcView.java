package com.example.testproject.customview;

import android.content.Context;
import android.graphics.BlurMaskFilter;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import androidx.annotation.Nullable;

public class LrcView extends View {

    private PorterDuff.Mode mMode = PorterDuff.Mode.SRC_IN;

    private Paint mPaint;
    private int mWidth, mHeight;
    private PorterDuffXfermode mPorterDuffXfermode;
    private Rect mRect;

    public LrcView(Context context) {
        this(context, null);
    }

    public LrcView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public LrcView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mPaint.setColor(Color.WHITE);
        mPaint.setTextAlign(Paint.Align.CENTER);
        mPaint.setTextSize(80);

        mRect = new Rect();

        mPorterDuffXfermode = new PorterDuffXfermode(mMode);

        mPaint.setMaskFilter(new BlurMaskFilter(1, BlurMaskFilter.Blur.INNER));

        setBackgroundColor(Color.BLACK);
    }


    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        this.mWidth = w;
        this.mHeight = h;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        int layerId = canvas.saveLayer(0, 0, mWidth, mHeight, mPaint);
        drawLrcText(canvas);
        mPaint.setXfermode(mPorterDuffXfermode);
        drawAnotherColorLrcText(canvas);
        mPaint.setXfermode(null);
        canvas.restoreToCount(layerId);
        invalidate();
    }

    private float mLastX, mLastY;

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        float x = event.getX();
        float y = event.getY();
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                mLastX = x;
                mLastY = y;
                break;
            case MotionEvent.ACTION_MOVE:
                float offsetX = x - mLastX;
                float offsetY = y - mLastY;
                scrollBy(-(int)offsetX,-(int)offsetY);
                mLastX = x;
                mLastY = y;
                break;
            case MotionEvent.ACTION_UP:

                break;
        }
        return true;
    }

    private void drawLrcText(Canvas canvas) {
        mPaint.setColor(Color.WHITE);
        canvas.drawText("这是一行文字这是一行文字这是一行文字这是一行文字这是一行文字这是一行文字", mWidth / 2.f, mHeight / 2.f, mPaint);
    }

    private int right = 1;
    private int step = 3;

    private void drawAnotherColorLrcText(Canvas canvas) {
        mPaint.setColor(Color.BLUE);
        mRect.set(0, 0, right, mHeight);
        canvas.drawRect(mRect, mPaint);
        right = right + step;
        if (right > mWidth)
            right = 1;
    }


}
