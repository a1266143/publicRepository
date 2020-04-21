package com.example.testproject.customview;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.DashPathEffect;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Point;
import android.graphics.PointF;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

import androidx.annotation.Nullable;

import java.util.ArrayList;
import java.util.List;

/**
 * 角度View
 * created by xiaojun at 2020/4/20
 */
public class AngleView extends View {

    private Triangle mTriangle;
    private int mWidth, mHeight;
    private Path mPath = new Path();
    private Paint mPaintLine, mPaintBall,mPaintDotLine,mPaintText;
    private int mRadius = 10;
    private int mDragPoint = -1;
    private int mAngle;
    private VectorUtils mVectorUtils;
    private int mOffsetY;

    public AngleView(Context context) {
        this(context, null);
    }

    public AngleView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public AngleView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
        setBackgroundColor(Color.BLACK);
    }

    private void init() {
        mTriangle = new Triangle();
        mPaintLine = new Paint(Paint.ANTI_ALIAS_FLAG);
        mPaintLine.setStyle(Paint.Style.STROKE);
        mPaintLine.setTextAlign(Paint.Align.CENTER);
        mPaintLine.setColor(Color.GREEN);
        mPaintLine.setTextSize(18);
        mPaintBall = new Paint(Paint.ANTI_ALIAS_FLAG);
        mPaintBall.setColor(Color.RED);
        mPaintBall.setTextSize(18);
        mPaintDotLine = new Paint(Paint.ANTI_ALIAS_FLAG);
        mPaintDotLine.setColor(Color.YELLOW);
        mPaintDotLine.setPathEffect(new DashPathEffect(new float[]{15,15},0));

        mPaintText = new Paint(Paint.ANTI_ALIAS_FLAG);
        mPaintText.setTextSize(dip2px(getContext(),10));
        mPaintText.setStyle(Paint.Style.FILL);
        mPaintText.setColor(Color.GREEN);

        setLayerType(LAYER_TYPE_SOFTWARE,null);

        mVectorUtils = new VectorUtils();
        mOffsetY = dip2px(getContext(),30);
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

    private float mLastX,mLastY;
    private boolean mDrawDotLine;

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        float x = event.getX();
        float y = event.getY();
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                if (canDrag(x, y)){
                    mLastX = x;
                    mLastY = y;
                    if (mDragPoint == mTriangle.DRAG_TRIANGLE)
                        mDrawDotLine = true;
                    return true;
                }

                break;
            case MotionEvent.ACTION_MOVE:
                if (mDragPoint == mTriangle.DRAG_LEFT) {
                    dragLeftPoint(x, y);
                } else if (mDragPoint == mTriangle.DRAG_PEAK) {
                    dragPeakPoint(x, y);
                } else if (mDragPoint == mTriangle.DRAG_RIGHT) {
                    dragRightPoint(x, y);
                }else if (mDragPoint == mTriangle.DRAG_TRIANGLE){
                    dragTriangle(x-mLastX,y-mLastY);
                }
                mAngle = calculateAngle();
                mLastX =  x;
                mLastY =  y;
                invalidate();
                return true;
            case MotionEvent.ACTION_UP:
                if (mDragPoint == mTriangle.DRAG_TRIANGLE)
                    mDrawDotLine = false;
                invalidate();
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
        return mVectorUtils.calculateAngleWithVector(mTriangle.mPointPeak, mTriangle.mEndPointLeft, mTriangle.mEndPointRight);
    }

    private void dragLeftPoint(float x, float y) {
        mTriangle.mEndPointLeft.x = (int) x;
        mTriangle.mEndPointLeft.y = (int) y-mOffsetY;
    }

    private void dragPeakPoint(float x, float y) {
        mTriangle.mPointPeak.x = (int) x;
        mTriangle.mPointPeak.y = (int) y-mOffsetY;
    }

    private void dragRightPoint(float x, float y) {
        mTriangle.mEndPointRight.x = (int) x;
        mTriangle.mEndPointRight.y = (int) y-mOffsetY;
    }

    private void dragTriangle(float offsetX,float offsetY){
        Log.e("xiaojun","offsetX="+offsetX+",offsetY="+offsetY);
        mTriangle.mEndPointRight.x += offsetX;
        mTriangle.mEndPointRight.y += offsetY;
        mTriangle.mEndPointLeft.x += offsetX;
        mTriangle.mEndPointLeft.y += offsetY;
        mTriangle.mPointPeak.x += offsetX;
        mTriangle.mPointPeak.y += offsetY;
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
        List<Point> list = new ArrayList<>();
        list.add(mTriangle.mEndPointLeft);
        list.add(mTriangle.mEndPointRight);
        list.add(mTriangle.mPointPeak);
        if (distancePointLeft <= mRadius + 20) {
            mDragPoint = mTriangle.DRAG_LEFT;
            return true;
        } else if (distancePeak <= mRadius + 20) {
            mDragPoint = mTriangle.DRAG_PEAK;
            return true;
        } else if (distancePointRight <= mRadius + 20) {
            mDragPoint = mTriangle.DRAG_RIGHT;
            return true;
        } else if (isPointInPolygon(new Point((int)x,(int)y),list)){
            mDragPoint = mTriangle.DRAG_TRIANGLE;
            mDrawDotLine = true;
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
        drawPeakBall(canvas);
        drawAngleText(canvas);
        drawArc(canvas);
        if (mDrawDotLine)
            drawDotLine(canvas);
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
        mPaintBall.setColor(Color.YELLOW);
        mPaintBall.setStyle(Paint.Style.STROKE);
        canvas.drawCircle(mTriangle.mPointPeak.x, mTriangle.mPointPeak.y, mRadius, mPaintBall);
        mPaintBall.setColor(Color.YELLOW);
        mPaintBall.setStyle(Paint.Style.FILL);
        canvas.drawCircle(mTriangle.mEndPointRight.x, mTriangle.mEndPointRight.y, mRadius, mPaintBall);
    }

    private void drawPeakBall(Canvas canvas){
        canvas.drawCircle(mTriangle.mPointPeak.x,mTriangle.mPointPeak.y,mRadius-5,mPaintBall);
    }

    private void drawAngleText(Canvas canvas) {
        mPaintBall.setColor(Color.RED);
        canvas.drawText(mAngle + "°", 100, 100, mPaintBall);
    }


    private Point mPointHorizontal = new Point(100, 0);
    private Point mPointOrigin = new Point(0, 0);
    private Point mPointLeftNew = new Point(0, 0);
    private Point mPointRightNew = new Point(0, 0);

    private void drawArc(Canvas canvas) {
        canvas.save();
        canvas.translate(mTriangle.mPointPeak.x, mTriangle.mPointPeak.y);
        mPointLeftNew.x = mTriangle.mEndPointLeft.x - mTriangle.mPointPeak.x;
        mPointLeftNew.y = mTriangle.mEndPointLeft.y - mTriangle.mPointPeak.y;
        mPointRightNew.x = mTriangle.mEndPointRight.x - mTriangle.mPointPeak.x;
        mPointRightNew.y = mTriangle.mEndPointRight.y - mTriangle.mPointPeak.y;
        int angleLeft = mVectorUtils.calculateAngleWithVector(mPointOrigin, mPointHorizontal, mPointLeftNew);
        int angleRight = mVectorUtils.calculateAngleWithVector(mPointOrigin, mPointHorizontal, mPointRightNew);
        int angleInclude = mAngle;
        //右边点在上面
        if (mPointRightNew.y<=0){
            //左边点在上面
            if (mPointLeftNew.y<=0){
                angleLeft = -angleLeft;
                if (Math.abs(angleLeft)<Math.abs(angleRight)){
                    angleInclude = -angleInclude;
                    drawArcAngle(canvas,-(Math.abs(mAngle/2)+Math.abs(angleLeft)));
                }else{
                    drawArcAngle(canvas,-(Math.abs(angleLeft)-Math.abs(mAngle/2)));
                }
            }
            //左边点在下面
            else{
                angleLeft = Math.abs(angleLeft);
                if (Math.abs(angleLeft)+Math.abs(angleRight)<=180){
                    angleInclude = -angleInclude;
                    drawArcAngle(canvas,-((Math.abs(angleLeft)+Math.abs(angleRight))/2- Math.abs(angleLeft)));
                }else{
                    drawArcAngle(canvas,-(Math.abs(angleRight)+Math.abs(mAngle)/2));
                }
            }
        }
        //右边点在下面
        else{
            //左边点在上面
            if (mPointLeftNew.y<=0){
                angleLeft = -angleLeft;
                if (Math.abs(angleLeft)+Math.abs(angleRight)>180){
                    angleInclude = -angleInclude;
                    drawArcAngle(canvas,-(Math.abs(angleLeft)+Math.abs(mAngle)/2));
                }else{
                    drawArcAngle(canvas,-(Math.abs(mAngle)/2-Math.abs(angleRight)));
                }
            }
            //左边点在下面
            else{
                if (angleLeft>=angleRight){
                    angleInclude = -angleInclude;
                    drawArcAngle(canvas,Math.abs(mAngle)/2+Math.abs(angleRight));
                }else{
                    drawArcAngle(canvas,Math.abs(mAngle)/2+Math.abs(angleLeft));
                }
            }
        }
        canvas.drawArc(-25, -25, 25, 25, angleLeft, angleInclude, false, mPaintLine);
        canvas.restore();
    }

    private void drawDotLine(Canvas canvas){
        canvas.drawLine(mTriangle.mEndPointLeft.x,mTriangle.mEndPointLeft.y,mTriangle.mEndPointRight.x,mTriangle.mEndPointRight.y,mPaintDotLine);
    }

    /**
     * 画弧的角度
     * @param canvas
     * @param angle
     */
    private void drawArcAngle(Canvas canvas,int angle){
        canvas.save();
        canvas.rotate(angle);
        Paint.FontMetrics fontMetrics = mPaintText.getFontMetrics();
        String angleStr = mAngle + "°";
        float distance = (fontMetrics.bottom-fontMetrics.top)/2-fontMetrics.bottom;
        canvas.drawText(angleStr,40,distance,mPaintText);
        canvas.restore();
    }

    private static int dip2px(Context context, float dpValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dpValue * scale + 0.5f);
    }

    /**
     * 利用光投射算法计算点是否在多边形内
     *
     * @param point 需要判断的点的坐标
     * @param vertices 多边形按顺时针或逆时针顺序的顶点坐标集合
     * @return 点是否在多边形内
     */
    public static boolean isPointInPolygon(Point point, List<Point> vertices) {
        boolean contains = false;
        for(int i = 0, j = vertices.size() - 1; i < vertices.size(); j = i++) {
            if(((vertices.get(i).y >= point.y) != (vertices.get(j).y >= point.y)) &&
                    (point.x <= (vertices.get(j).x - vertices.get(i).x) * (point.y - vertices.get(i).y) / (vertices.get(j).y - vertices.get(i).y) + vertices.get(i).x))
                contains = !contains;
        }
        return contains;
    }

    /**
     * 三角形
     */
    class Triangle {
        public int DRAG_NONE = -1, DRAG_LEFT = 0, DRAG_PEAK = 1, DRAG_RIGHT = 2,DRAG_TRIANGLE=3;
        Point mPointPeak;
        Point mEndPointLeft;
        Point mEndPointRight;
    }

}
