package com.example.testproject.customview;

import android.content.Context;
import android.graphics.Camera;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.View;

import androidx.annotation.Nullable;

public class CameraView extends View {

    private Paint mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);

    private Camera mCamera = new Camera();

    public CameraView(Context context) {
        this(context,null);
    }

    public CameraView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs,0);
    }

    public CameraView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init(){
        mPaint.setColor(Color.GRAY);
        mPaint.setStyle(Paint.Style.FILL);

        mCamera.save();
        //在3D坐标系中将3D坐标系向右移动100像素，向下移动200像素，向屏幕垂直向内移动300像素
        mCamera.translate(100,-200,300);
        mCamera.restore();
    }

    private int mWidth,mHeight;

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        this.mWidth = w;
        this.mHeight = h;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        canvas.save();
        canvas.translate(mWidth/2f,mHeight/2f);
        canvas.drawCircle(0,0,100,mPaint);
        canvas.restore();
    }


}
