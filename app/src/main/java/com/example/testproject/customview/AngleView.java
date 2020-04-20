package com.example.testproject.customview;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Point;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

import androidx.annotation.Nullable;

/**
 * 角度View
 * created by xiaojun at 2020/4/20
 */
public class AngleView extends View {

    private Triangle mTriangle;
    private int mWidth, mHeight;
    private Path mPath = new Path();
    private Paint mPaintLine, mPaintBall;
    private int mRadius = 10;
    private int mDragPoint = -1;
    private int mAngle;

    public AngleView(Context context) {
        this(context, null);
    }

    public AngleView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public AngleView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        mTriangle = new Triangle();
        mPaintLine = new Paint(Paint.ANTI_ALIAS_FLAG);
        mPaintLine.setStyle(Paint.Style.STROKE);
        mPaintLine.setStrokeWidth(3.f);
        mPaintLine.setColor(Color.GREEN);
        mPaintBall = new Paint(Paint.ANTI_ALIAS_FLAG);
        mPaintBall.setColor(Color.RED);
        mPaintBall.setTextSize(18);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        this.mWidth = w;
        this.mHeight = h;
        Point pointPeak = new Point(mWidth / 2, mHeight / 3);//顶点
        Point pointEndLeft = new Point(mWidth / 3, mHeight / 2);//左端点
        Point pointEndRight = new Point((int) (mWidth / 1.5f), mHeight / 2);//右端点
        mTriangle.mPointPeak = pointPeak;
        mTriangle.mEndPointLeft = pointEndLeft;
        mTriangle.mEndPointRight = pointEndRight;
        mAngle = calculateAngle();
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        float x = event.getX();
        float y = event.getY();
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                if (canDrag(x, y))
                    return true;
                break;
            case MotionEvent.ACTION_MOVE:
                if (mDragPoint == mTriangle.DRAG_LEFT) {
                    dragLeftPoint(x, y);
                } else if (mDragPoint == mTriangle.DRAG_PEAK) {
                    dragPeakPoint(x, y);
                } else if (mDragPoint == mTriangle.DRAG_RIGHT) {
                    dragRightPoint(x, y);
                }
                mAngle = calculateAngle();
                invalidate();
                return true;
            case MotionEvent.ACTION_UP:

                break;
        }
        return false;
    }

    /**
     * 通过向量计算角度
     * Peak_Left 向量
     * Peak_Right 向量
     * 参考 https://blog.csdn.net/zhang1244j/article/details/55053184
     * https://blog.csdn.net/coding__madman/article/details/51684641
     */
    private int calculateAngle() {
        //PeakLeft向量的x，y坐标
        int x_vector_peakLeft = mTriangle.mEndPointLeft.x - mTriangle.mPointPeak.x;
        int y_vector_peakLeft = mTriangle.mEndPointLeft.y - mTriangle.mPointPeak.y;
        //PeakRight向量的x,y坐标
        int x_vector_peakRight = mTriangle.mEndPointRight.x - mTriangle.mPointPeak.x;
        int y_vector_peakRight = mTriangle.mEndPointRight.y - mTriangle.mPointPeak.y;
        //PeakLeft向量的模
        double lengthPeakLeft = Math.sqrt((Math.pow(x_vector_peakLeft, 2) + Math.pow(y_vector_peakLeft, 2)));
        //PeakRight向量的模
        double lengthPeakRight = Math.sqrt((Math.pow(x_vector_peakRight,2)+Math.pow(y_vector_peakRight,2)));
        //向量PeakLeft和向量PeakRight的叉积
//        double crossProduct = x_vector_peakLeft*y_vector_peakRight-x_vector_peakRight*y_vector_peakLeft;
        //向量PeakLeft和向量PeakRight的点积
        double crossProduct = x_vector_peakLeft*x_vector_peakRight+y_vector_peakLeft*y_vector_peakRight;
        //求出 Left-Peak-Right的余弦值
        double cosine = crossProduct/(lengthPeakLeft*lengthPeakRight);
        //求出角度
        double radian = Math.acos(cosine);
        double angle = radian*180/Math.PI;
        Log.e("xiaojun","角度为:"+ angle);
        return (int) angle;
    }

    private void dragLeftPoint(float x, float y) {
        mTriangle.mEndPointLeft.x = (int) x;
        mTriangle.mEndPointLeft.y = (int) y;
    }

    private void dragPeakPoint(float x, float y) {
        mTriangle.mPointPeak.x = (int) x;
        mTriangle.mPointPeak.y = (int) y;
    }

    private void dragRightPoint(float x, float y) {
        mTriangle.mEndPointRight.x = (int) x;
        mTriangle.mEndPointRight.y = (int) y;
    }

    /**
     * 触摸点是否可以拖动
     *
     * @param x
     * @param y
     * @return
     */
    private boolean canDrag(float x, float y) {
        double distancePointLeft = Math.abs(Math.sqrt(Math.pow(x - mTriangle.mEndPointLeft.x, 2) + Math.pow(y - mTriangle.mEndPointLeft.y, 2)));
        double distancePointRight = Math.abs(Math.sqrt(Math.pow(x - mTriangle.mEndPointRight.x, 2) + Math.pow(y - mTriangle.mEndPointRight.y, 2)));
        double distancePeak = Math.abs(Math.sqrt(Math.pow(x - mTriangle.mPointPeak.x, 2) + Math.pow(y - mTriangle.mPointPeak.y, 2)));
        if (distancePointLeft <= mRadius + 20) {
            mDragPoint = mTriangle.DRAG_LEFT;
            return true;
        } else if (distancePeak <= mRadius + 20) {
            mDragPoint = mTriangle.DRAG_PEAK;
            return true;
        } else if (distancePointRight <= mRadius + 20) {
            mDragPoint = mTriangle.DRAG_RIGHT;
            return true;
        }
        mDragPoint = mTriangle.DRAG_NONE;
        return false;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        drawPath(canvas);
        drawBall(canvas);
        drawAngleText(canvas);
    }

    private void drawPath(Canvas canvas) {
        mPath.reset();
        mPath.moveTo(mTriangle.mEndPointLeft.x, mTriangle.mEndPointLeft.y);
        mPath.lineTo(mTriangle.mPointPeak.x, mTriangle.mPointPeak.y);
        mPath.lineTo(mTriangle.mEndPointRight.x, mTriangle.mEndPointRight.y);
        canvas.drawPath(mPath, mPaintLine);
    }

    private void drawBall(Canvas canvas) {
        mPaintBall.setColor(Color.RED);
        canvas.drawCircle(mTriangle.mEndPointLeft.x, mTriangle.mEndPointLeft.y, mRadius, mPaintBall);
        mPaintBall.setColor(Color.BLUE);
        canvas.drawCircle(mTriangle.mPointPeak.x, mTriangle.mPointPeak.y, mRadius, mPaintBall);
        mPaintBall.setColor(Color.RED);
        canvas.drawCircle(mTriangle.mEndPointRight.x, mTriangle.mEndPointRight.y, mRadius, mPaintBall);
    }

    private void drawAngleText(Canvas canvas){
        canvas.drawText(mAngle+"°",100,100,mPaintBall);
    }

    /**
     * 三角形
     */
    class Triangle {
        public int DRAG_NONE = -1, DRAG_LEFT = 0, DRAG_PEAK = 1, DRAG_RIGHT = 2;
        Point mPointPeak;
        Point mEndPointLeft;
        Point mEndPointRight;
    }

}
