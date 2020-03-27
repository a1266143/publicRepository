package com.example.testproject.popupwindow;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.util.AttributeSet;
import android.view.View;

import androidx.annotation.Nullable;

/**
 * 等边三角形View
 * 目前只实现向下箭头,以后需要可以扩展
 * created by xiaojun at 2020/3/27
 */
public class ArrowsView extends View {

    private Paint mPaint;
    private Path mPath;

    private int mTriangleWidth = 50;//三角形长度

    public ArrowsView(Context context) {
        this(context,null);
    }

    public ArrowsView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs,0);
    }

    public ArrowsView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init(){
        mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mPaint.setColor(Color.WHITE);

        mPath = new Path();
    }



    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int widthMode = MeasureSpec.getMode(widthMeasureSpec);
        int heightMode = MeasureSpec.getMode(heightMeasureSpec);
        int width = MeasureSpec.getSize(widthMeasureSpec);
        int height = MeasureSpec.getSize(heightMeasureSpec);

        int defaultWidth = mTriangleWidth;//默认宽度
        int defaultHeight = (int) (mTriangleWidth/2*Math.sqrt(3));//等边三角形求高定理

        int realWidth = defaultWidth;//真实宽度
        int realHeight = defaultHeight;//真实高度

        //如果是wrap_content或者未指定大小
        if (widthMode ==MeasureSpec.EXACTLY){
            realWidth = width;
        }
        if (heightMode == MeasureSpec.EXACTLY){
            realHeight = height;
        }
        setMeasuredDimension(realWidth,realHeight);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        mPath.moveTo(w/2.f-mTriangleWidth/2.f,0);
        mPath.lineTo(w/2.f+mTriangleWidth/2.f,0);
        mPath.lineTo(w/2.f,h);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        drawTriangle(canvas);
    }

    /**
     * 画三角形
     * @param canvas
     */
    private void drawTriangle(Canvas canvas){
        canvas.drawPath(mPath,mPaint);
    }

}
