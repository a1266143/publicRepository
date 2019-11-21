package com.example.testproject.customview;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.util.AttributeSet;
import android.view.View;

import androidx.annotation.Nullable;

/**
 * 混合模式自定义View
 * created by xiaojun at 2019/11/21
 */
public class MixedModeView extends View {

    private Paint mPaint,mPaintBitmapDst,mPaintBitmapSrc;
    private PorterDuffXfermode mPorterDuffXfermode;
    private int mWidth,mHeight;
    private Bitmap mBitmapSrc,mBitmapDst;

    public MixedModeView(Context context) {
        this(context,null);
    }

    public MixedModeView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs,0);
    }

    public MixedModeView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
        setBackgroundColor(Color.BLUE);
    }

    private void init(){
        mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mPaint.setTextSize(24);
        mPaint.setColor(Color.RED);

        mPaintBitmapDst = new Paint(Paint.ANTI_ALIAS_FLAG);
        mPaintBitmapDst.setColor(Color.BLUE);

        mPaintBitmapSrc = new Paint(Paint.ANTI_ALIAS_FLAG);
        mPaintBitmapSrc.setColor(Color.GRAY);

        mPorterDuffXfermode = new PorterDuffXfermode(PorterDuff.Mode.SRC_IN);

    }

    private Bitmap makeDst(){
        Bitmap bitmap = Bitmap.createBitmap(mWidth,mHeight, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        canvas.drawRect(0,0,mWidth/2.f+mWidth/4.f,mHeight/2.f+mHeight/4.f,mPaintBitmapDst);
        return bitmap;
    }

    private Bitmap makeSrc(){
        Bitmap bitmap = Bitmap.createBitmap(mWidth,mHeight, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        canvas.save();
        canvas.translate(mWidth/2.f+mWidth/4.f,mHeight/2.f+mHeight/4.f);
        canvas.drawCircle(0,0,mWidth/4.f,mPaintBitmapSrc);
        canvas.restore();
        return bitmap;
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        this.mWidth = w;
        this.mHeight = h;
        mBitmapSrc = makeSrc();
        mBitmapDst = makeDst();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        int layerID = canvas.saveLayer(0,0,mWidth,mHeight,mPaint);
        canvas.drawText("哈哈哈",600,600,mPaint);//目标图像
//        drawDst(canvas);//目标图像
        mPaint.setXfermode(mPorterDuffXfermode);
        drawSrc(canvas);
        mPaint.setXfermode(null);
        canvas.restoreToCount(layerID);
    }

    private void drawDst(Canvas canvas){
        canvas.drawBitmap(mBitmapDst,0,0,mPaint);
    }

    private void drawSrc(Canvas canvas){
        canvas.drawBitmap(mBitmapSrc,0,0,mPaint);
    }

}
