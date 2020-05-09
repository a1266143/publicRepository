package com.example.testproject.customview;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.View;

import androidx.annotation.Nullable;

/**
 * 圆圈电池View
 * created by xiaojun
 */
public class CircleBatteryView extends View {

    private Paint mPaintBack = new Paint(Paint.ANTI_ALIAS_FLAG);
    private Paint mPaintFore = new Paint(Paint.ANTI_ALIAS_FLAG);
    private Paint mPaintText = new Paint(Paint.ANTI_ALIAS_FLAG);

    private int mWidth, mHeight;
    private float mStrokeWidth;
    private float mTextSize;
    private String mText;
    private float mDistance_center_baseline;
    private int mSweepAngle;//电池电量扫过的角度

    public CircleBatteryView(Context context) {
        this(context, null);
    }

    public CircleBatteryView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public CircleBatteryView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        mStrokeWidth = Utils.dp2px(getContext(), 3);
        mTextSize = Utils.dp2px(getContext(), 7);
        mText = "0";

        mPaintBack.setColor(Color.parseColor("#777777"));
        mPaintBack.setStyle(Paint.Style.STROKE);
        mPaintBack.setStrokeWidth(mStrokeWidth);

        mPaintFore.setColor(Color.GREEN);
        mPaintFore.setStyle(Paint.Style.STROKE);
        mPaintFore.setStrokeWidth(mStrokeWidth);

        mPaintText.setColor(Color.WHITE);
        mPaintText.setTextSize(mTextSize);
        mPaintText.setTextAlign(Paint.Align.CENTER);

        Paint.FontMetrics fontMetrics = mPaintText.getFontMetrics();
        //文字Y轴居中应该加上的距离
        mDistance_center_baseline = (fontMetrics.bottom - fontMetrics.top) / 2 - fontMetrics.bottom;
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        this.mWidth = w;
        this.mHeight = h;
    }

    /**
     * 设置电池电量
     *
     * @param batteryLevel
     */
    public void setBattery(int batteryLevel) {
        if (batteryLevel > 100 || batteryLevel < 0)
            throw new RuntimeException("电池电量必须在0~100");
        mText = batteryLevel + "";
        float rato = batteryLevel*1.f/100;
        mSweepAngle = (int) (rato*360);
        invalidate();
    }


    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        canvas.save();
        canvas.translate(mWidth / 2, mHeight / 2);
        //drawBack();
        canvas.drawArc(mStrokeWidth / 2 - mWidth / 2, -mHeight / 2 + mStrokeWidth / 2, mWidth / 2 - mStrokeWidth / 2, mHeight / 2 - mStrokeWidth / 2, 0, 360, false, mPaintBack);
        //drawForeground
        if (Integer.parseInt(mText)<30&&Integer.parseInt(mText)>15)
            mPaintFore.setColor(Color.YELLOW);
        else if (Integer.parseInt(mText)>=0&&Integer.parseInt(mText)<=15)
            mPaintFore.setColor(Color.RED);
        else
            mPaintFore.setColor(Color.GREEN);
        canvas.drawArc(mStrokeWidth / 2 - mWidth / 2, -mHeight / 2 + mStrokeWidth / 2, mWidth / 2 - mStrokeWidth / 2, mHeight / 2 - mStrokeWidth / 2, -90, mSweepAngle, false, mPaintFore);
        //drawText
        canvas.drawText(mText, 0, mDistance_center_baseline, mPaintText);
        canvas.restore();
    }
}
