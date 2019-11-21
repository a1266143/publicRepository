package com.example.testproject.customview;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Shader;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import androidx.annotation.Nullable;

import com.example.testproject.R;

public class BitmapShaderView extends View {

    private Paint mPaint;
    private Bitmap mBitmapHeadImg;

    public BitmapShaderView(Context context) {
        this(context,null);
    }

    public BitmapShaderView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs,0);
    }

    public BitmapShaderView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init(){
        mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);

        mBitmapHeadImg = BitmapFactory.decodeResource(getResources(), R.drawable.test);
        mPaint.setShader(new BitmapShader(mBitmapHeadImg, Shader.TileMode.CLAMP, Shader.TileMode.CLAMP));
        mPaint.setTextSize(64);
        mPaint.setTextAlign(Paint.Align.CENTER);
    }

    private int mWidth,mHeight;

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        mWidth = w;
        mHeight = h;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        drawHeadImg(canvas);
    }

    private float mLastX,mLastY;

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        touched = true;
        float x = event.getX();
        float y = event.getY();
        switch (event.getAction()){
            case MotionEvent.ACTION_DOWN:
                mLastX = x;
                mLastY = y;
                break;
            case MotionEvent.ACTION_MOVE:
                mLastX = x;
                mLastY = y;
                break;
            case MotionEvent.ACTION_UP:
                touched = false;
                break;
        }
        invalidate();
        return true;
    }

    private boolean touched;
    private float mCenterX,mCenterY;

    private void drawHeadImg(Canvas canvas){
//        canvas.drawBitmap(mBitmapHeadImg,0,0,mPaint);
        if (touched)
        canvas.drawCircle(mLastX,mLastY,200,mPaint);
        else
            canvas.drawText("点击屏幕试试",mWidth/2.f,mHeight/2.f,mPaint);
    }
}
