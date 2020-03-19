package com.example.testproject.customview;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.RectF;
import android.graphics.Xfermode;
import android.util.AttributeSet;
import android.view.View;

import androidx.annotation.Nullable;

public class TestBatteryView extends View {

    private Paint mPaint;
    private int mWidth,mHeight;

    public TestBatteryView(Context context) {
        this(context,null);
    }

    public TestBatteryView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs,0);
    }

    public TestBatteryView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init(){
        mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mPaint.setColor(Color.RED);

//        setLayerType(View.LAYER_TYPE_SOFTWARE, null);
    }

    private Xfermode mXfermode = new PorterDuffXfermode(PorterDuff.Mode.SRC_IN);

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        setBackgroundColor(Color.BLUE);//BLUE
        int layerID = canvas.saveLayer(0,0,mWidth*2,mHeight*2,mPaint,Canvas.ALL_SAVE_FLAG);
        canvas.drawBitmap(createBitmapBackground(),0,0,mPaint);//background RED
        mPaint.setXfermode(mXfermode);//SRC_IN
        canvas.drawBitmap(createBitmapForeground(),0,0,mPaint);//foreground GREEN
        mPaint.setXfermode(null);
        canvas.restoreToCount(layerID);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        mWidth = w;
        mHeight = h;
    }

    private Bitmap createBitmapBackground(){
        Bitmap bitmap = Bitmap.createBitmap(mWidth,mHeight, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        RectF rectF = new RectF(0,0,mWidth,mHeight);
        Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        paint.setColor(Color.RED);
//        canvas.drawRect(rectF,mPaint);
        canvas.drawCircle(mWidth/2.f,mHeight/2.f,mWidth/3.f,paint);
        return bitmap;
    }

    private Bitmap createBitmapForeground(){
        Bitmap bitmap = Bitmap.createBitmap(mWidth,mHeight, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        RectF rectF = new RectF(100,100,mWidth,mHeight);
        Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        paint.setColor(Color.GREEN);
        canvas.drawRect(rectF,paint);
        return bitmap;
    }

}
