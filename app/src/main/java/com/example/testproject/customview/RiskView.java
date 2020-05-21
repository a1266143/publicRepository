package com.example.testproject.customview;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Point;
import android.graphics.PointF;
import android.graphics.Shader;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

import androidx.annotation.Nullable;

/**
 * 风险评估View
 * created by xiaojun at 2020/5/21
 */
public class RiskView extends View {

    private Paint mPaintProgressBar = new Paint(Paint.ANTI_ALIAS_FLAG);
    private Paint mPaintIndicator = new Paint(Paint.ANTI_ALIAS_FLAG);
    private Triangle mIndicator;
    private Path mPathIndicator;
    private float mStartOffsetX;

    private int mWidth, mHeight;
    private float mStrokeWidth;

    private LinearGradient mLinearGradient;
    private int[] colors = {Color.parseColor("#1fff00"), Color.parseColor("#faff00"), Color.parseColor("#ff1802")};

    public RiskView(Context context) {
        this(context, null);
    }

    public RiskView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public RiskView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        mPaintProgressBar.setStyle(Paint.Style.STROKE);
        mPaintProgressBar.setStrokeCap(Paint.Cap.ROUND);

//        mPaintIndicator.setColor(Color.GREEN);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        this.mWidth = w;
        this.mHeight = h;
        this.mStrokeWidth = h / 2.f;
        mPaintProgressBar.setStrokeWidth(mStrokeWidth);
        mLinearGradient = new LinearGradient(0, mStrokeWidth / 2.f, w, mStrokeWidth / 2.f, colors, null, Shader.TileMode.MIRROR);
        mPaintProgressBar.setShader(mLinearGradient);
        mPaintIndicator.setShader(mLinearGradient);
        //设置指示器
        this.mStartOffsetX = mStrokeWidth;
        PointF pointLeft = new PointF(mStartOffsetX / 2.f, mHeight);
        PointF pointTop = new PointF(mStartOffsetX, mStrokeWidth + mStrokeWidth / 3.f);
        PointF pointRight = new PointF(mStartOffsetX * 2 - mStartOffsetX / 2.f, mHeight);
        mIndicator = new Triangle(pointLeft, pointTop, pointRight);
        mPathIndicator = new Path();
        mPathIndicator.reset();
        mPathIndicator.moveTo(pointLeft.x, pointLeft.y);
        mPathIndicator.lineTo(pointTop.x, pointTop.y);
        mPathIndicator.lineTo(pointRight.x, pointRight.y);
        mPathIndicator.close();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        drawProgressBar(canvas);
        drawIndicator(canvas);
    }

    /**
     * 设置进度
     *
     * @param radio
     */
    public void setProgress(float radio) {
        if (radio < 0 || radio > 1) {
            Log.e("RiskView", "radio=" + radio + ",error!value must between 0 and 1");
            return;
        }
        float realTotalWidth = mWidth - mStartOffsetX - mStartOffsetX;
        float offsetX = realTotalWidth * radio;
        Log.e("xiaojun","offsetX = "+offsetX);
        mIndicator.peakLeft.x = offsetX+mStartOffsetX / 2.f;
        mIndicator.peakTop.x = offsetX+mStartOffsetX;
        mIndicator.peakRight.x = offsetX+mStartOffsetX * 2 - mStartOffsetX / 2.f;
        mPathIndicator.reset();
        mPathIndicator.moveTo(mIndicator.peakLeft.x,mIndicator.peakLeft.y);
        mPathIndicator.lineTo(mIndicator.peakTop.x,mIndicator.peakTop.y);
        mPathIndicator.lineTo(mIndicator.peakRight.x,mIndicator.peakRight.y);
        mPathIndicator.close();
        Log.e("xiaojun","mindicator.peakleft.x="+mIndicator.peakLeft.x+",mindicator.peakleft.y="+mIndicator.peakLeft.y+
                ",mindicator.peaktop.x="+mIndicator.peakTop.x+",mindicator.peaktop.y="+mIndicator.peakTop.y+
                ",mindicator.peakright.x="+mIndicator.peakRight.x+",mindicator.peakright.y="+mIndicator.peakRight.y);
        invalidate();
    }

    /**
     * 画进度条
     *
     * @param canvas
     */
    private void drawProgressBar(Canvas canvas) {
        canvas.drawLine(mStrokeWidth / 2.f + mStartOffsetX, mStrokeWidth / 2.f, mWidth - mStrokeWidth / 2.f - mStartOffsetX, mStrokeWidth / 2.f, mPaintProgressBar);
    }

    /**
     * 画指示器
     *
     * @param canvas
     */
    private void drawIndicator(Canvas canvas) {
        canvas.drawPath(mPathIndicator, mPaintIndicator);
    }

    /**
     * 三角形
     * created by xiaojun at 2020/5/21
     */
    class Triangle {
        public Triangle(PointF peakLeft, PointF peakTop, PointF peakRight) {
            this.peakLeft = peakLeft;
            this.peakTop = peakTop;
            this.peakRight = peakRight;
        }

        public PointF peakTop, peakLeft, peakRight;
    }

}
