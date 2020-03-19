package com.example.testproject.customview.batteryview;

import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.RectF;
import android.graphics.Typeface;
import android.graphics.Xfermode;
import android.util.AttributeSet;
import android.view.View;
import android.view.animation.LinearInterpolator;

import androidx.annotation.Nullable;

import com.example.testproject.customview.ruler.Utils;

/**
 * 电池View
 * Created by xiaojun at 2020/2/26
 */
public class BatteryView extends View {

    private Paint mPaintLine;
    private Paint mPaintbackground;
    private Paint mPaint;
    private Paint mPaintText;
    private float mStrokeWidth = 2.5f;
    private Bitmap mBitmapSrc, mBitmapDst;
    private float mBatteryLevelX;
    private int mWidth, mHeight;
    private Canvas mCanvasDst, mCanvasSrc;
    private Paint mPaintDst, mPaintSrc;
    private float mOffsetBackground;
    private float mBaseLine;
    private int mCapacity;
    private Xfermode mXfermode = new PorterDuffXfermode(PorterDuff.Mode.DST_IN);

    public BatteryView(Context context) {
        this(context, null);
    }

    public BatteryView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public BatteryView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        mPaintLine = new Paint(Paint.ANTI_ALIAS_FLAG);
        mPaintLine.setStyle(Paint.Style.STROKE);
        mPaintLine.setStrokeWidth(mStrokeWidth);
        mPaintLine.setColor(Color.WHITE);

        mPaintbackground = new Paint(Paint.ANTI_ALIAS_FLAG);
        mPaintbackground.setColor(Color.GRAY);
        mPaintbackground.setAlpha(128);

        mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mPaint.setColor(Color.GREEN);

        mPaintText = new Paint(Paint.ANTI_ALIAS_FLAG);
        mPaintText.setColor(Color.WHITE);
        mPaintText.setTextAlign(Paint.Align.CENTER);
        mPaintText.setTextSize(24);
        Typeface font = Typeface.create(Typeface.SANS_SERIF, Typeface.BOLD);
        mPaintText.setTypeface(font);


        this.mPaintDst = new Paint(Paint.ANTI_ALIAS_FLAG);
        this.mPaintDst.setColor(Color.GREEN);
        this.mPaintDst.setAlpha(128);
        this.mPaintSrc = new Paint(Paint.ANTI_ALIAS_FLAG);
        this.mPaintSrc.setColor(Color.RED);

        setLayerType(View.LAYER_TYPE_SOFTWARE, null);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        this.mWidth = w;
        this.mHeight = h;
        this.mOffsetBackground = mStrokeWidth / 2.f + mStrokeWidth * 2 - mStrokeWidth * 4.f / 5.f;
        this.mBatteryLevelX = (mWidth - mOffsetBackground * 2) * (1 - mCapacity / 100f);//百分之50电量
        this.mBitmapDst = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888);
        this.mBitmapSrc = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888);
        this.mCanvasDst = new Canvas(mBitmapDst);
        this.mCanvasSrc = new Canvas(mBitmapSrc);
        Paint.FontMetrics fontMetrics = mPaintText.getFontMetrics();
        float distance = (fontMetrics.bottom - fontMetrics.top) / 2 - fontMetrics.bottom;
        mBaseLine = mHeight / 2.f + distance;
    }

    private boolean mIsCharge;
    private ValueAnimator mChargeAnimation;

    public void setCapacity(float capacity, boolean isCharge) {
        this.mIsCharge = isCharge;
        if (!isCharge){
            if (capacity > 100 || capacity < 0)
                throw new RuntimeException("capacity must be 0~100");
            if (mChargeAnimation!=null&&mChargeAnimation.isRunning())
                mChargeAnimation.cancel();
            this.mCapacity = (int) capacity;
            this.mBatteryLevelX = (mWidth - mOffsetBackground * 2) * (1 - mCapacity / 100.f);
            if (capacity <= 30 && capacity > 20)
                mPaintDst.setColor(Color.YELLOW);
            else if (capacity <= 20)
                mPaintDst.setColor(Color.RED);
            else
                mPaintDst.setColor(Color.GREEN);
            invalidate();
        }else{
            if (mChargeAnimation == null){
                mChargeAnimation = ValueAnimator.ofInt(0,100);
                mChargeAnimation.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                    @Override
                    public void onAnimationUpdate(ValueAnimator animation) {
                       mCapacity  = (int) animation.getAnimatedValue();
                        mBatteryLevelX = (mWidth - mOffsetBackground * 2) * (1 - mCapacity / 100.f);
                        if (mCapacity <= 30 && mCapacity > 20)
                            mPaintDst.setColor(Color.YELLOW);
                        else if (mCapacity <= 20)
                            mPaintDst.setColor(Color.RED);
                        else
                            mPaintDst.setColor(Color.GREEN);
                        invalidate();
                    }
                });
                mChargeAnimation.setRepeatCount(ValueAnimator.INFINITE);
                mChargeAnimation.setRepeatMode(ValueAnimator.RESTART);
                mChargeAnimation.setInterpolator(new LinearInterpolator());
                mChargeAnimation.setDuration(4000);
            }
            if (mChargeAnimation.isRunning())
                mChargeAnimation.cancel();
            mChargeAnimation.start();
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        clearCanvas();
        drawLine(canvas);//画电池线条
//        drawBackground(canvas);//画电池背景
        int layerID = canvas.saveLayer(0, 0, mWidth, mHeight, mPaint, Canvas.ALL_SAVE_FLAG);
        drawBatteryBackground(canvas);//画电池电量
        mPaint.setXfermode(mXfermode);
        drawBatteryForeground(canvas);//画电池电量上层
        mPaint.setXfermode(null);
        canvas.restoreToCount(layerID);
        if (!mIsCharge)
            drawText(canvas);//画文本
    }


    private void clearCanvas() {
        mCanvasSrc.drawColor(Color.TRANSPARENT, PorterDuff.Mode.CLEAR);
        mCanvasDst.drawColor(Color.TRANSPARENT, PorterDuff.Mode.CLEAR);
    }

    private void drawLine(Canvas canvas) {
        RectF rect = new RectF(mStrokeWidth / 2.f, mStrokeWidth / 2.f, mWidth - mStrokeWidth / 2.f, mHeight - mStrokeWidth / 2.f);
        //圆角矩形
        canvas.drawRoundRect(rect, mHeight / 2.f - mStrokeWidth, mHeight / 2.f - mStrokeWidth, mPaintLine);
    }

    private void drawBackground(Canvas canvas) {
        RectF rect = new RectF(mOffsetBackground, mOffsetBackground, mWidth - mOffsetBackground, mHeight - mOffsetBackground);
        canvas.drawRoundRect(rect, mHeight / 2.f - mOffsetBackground, mHeight / 2.f - mOffsetBackground, mPaintbackground);
    }

    private void drawBatteryBackground(Canvas canvas) {
        //DrawDst
        RectF rect = new RectF(mOffsetBackground, mOffsetBackground, mWidth - mOffsetBackground, mHeight - mOffsetBackground);
        mCanvasDst.drawRoundRect(rect, mHeight / 2.f - mOffsetBackground, mHeight / 2.f - mOffsetBackground, mPaintDst);
        canvas.drawBitmap(mBitmapDst, 0, 0, mPaint);
    }

    //画蒙板，遮住电池电量的进度条
    private void drawBatteryForeground(Canvas canvas) {
        mCanvasSrc.drawRect(mOffsetBackground + mBatteryLevelX, mOffsetBackground, mWidth - mOffsetBackground, mHeight - mOffsetBackground, mPaintSrc);
        canvas.drawBitmap(mBitmapSrc, 0, 0, mPaint);
    }


    private void drawText(Canvas canvas) {
        canvas.drawText(mCapacity + "", mWidth / 2.f, mBaseLine, mPaintText);
    }

}
