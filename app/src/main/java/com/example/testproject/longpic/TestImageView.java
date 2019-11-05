package com.example.testproject.longpic;

import android.content.Context;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.util.AttributeSet;
import android.view.MotionEvent;

import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatImageView;

import com.example.testproject.R;


public class TestImageView extends AppCompatImageView {
    public TestImageView(Context context) {
        this(context,null);
    }

    public TestImageView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs,0);
    }

    public TestImageView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init(){

        setScaleType(ScaleType.MATRIX);

        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inScaled = false;
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeResource(getResources(), R.drawable.bolang,options);
        this.mWidthBitmap = options.outWidth;
        this.mHeightBitmap = options.outHeight;

        this.mMatrix = new Matrix();
    }

    private Matrix mMatrix;

    private int mWidth,mHeight,mWidthBitmap,mHeightBitmap;

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        this.mWidth = w;
        this.mHeight = h;
        //缩放Y轴到View的高度，X轴不缩放
        float scaleY = 1.f*mHeight/mHeightBitmap;
        mMatrix.setScale(1,scaleY);
        setImageMatrix(mMatrix);
    }

    private float mDownX;
    private float mTotalOffsetX;

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction()){
            case MotionEvent.ACTION_DOWN:
                mDownX = event.getX();
                return true;
            case MotionEvent.ACTION_MOVE:
                float movedX = event.getX();
                float diffX = movedX - mDownX;
                mTotalOffsetX += diffX;
                mMatrix.setTranslate(mTotalOffsetX,0);
                float scaleY = 1.f*mHeight/mHeightBitmap;
                mMatrix.postScale(1,scaleY);
                setImageMatrix(mMatrix);
                mDownX = movedX;
                return true;
            case MotionEvent.ACTION_UP:
                return false;
        }
        return false;
    }

}
