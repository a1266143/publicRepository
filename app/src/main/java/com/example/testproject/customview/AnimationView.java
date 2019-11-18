package com.example.testproject.customview;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PointF;
import android.graphics.PorterDuff;
import android.util.AttributeSet;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import com.example.testproject.Utils;
import com.example.testproject.UtilsBezier;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * SurfaceView中的动画效果
 * created by xiaojun at 2019/11/15
 */
public class AnimationView extends SurfaceView implements SurfaceHolder.Callback, Runnable {

    private UtilsBezier mUtilsBezier = new UtilsBezier();

    public AnimationView(Context context) {
        this(context, null);
    }

    public AnimationView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public AnimationView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private SurfaceHolder mHolder;
    private Canvas mCanvas;
    private Paint mPaint;

    private boolean mIsRunning;

    private void init() {
        this.mHolder = getHolder();
        this.mHolder.addCallback(this);

//        setZOrderOnTop(true);
//        this.mHolder.setFormat(PixelFormat.TRANSLUCENT);

        this.mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        this.mPaint.setColor(Color.RED);
        this.mPaint.setStyle(Paint.Style.STROKE);
        this.mPaint.setStrokeWidth(3);

    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        mIsRunning = true;
        new Thread(this).start();
    }

    private Utils mUtils = new Utils();
    private List<Triangle> mListTriangle = new ArrayList<>();

    //随机生成三角形
    private void randomGenerateTriangle() {
        if (mUtils.getRandomTrueOrFalse()) {
            PointF point1 = new PointF(0, -mUtils.getRandomNumber(12));
            PointF point2 = new PointF(-mUtils.getRandomNumber(30), mUtils.getRandomNumber(20));
            PointF point3 = new PointF(mUtils.getRandomNumber(30), mUtils.getRandomNumber(20));
            //为三角形的每个点生成贝塞尔对象
            int randomDirection = mUtils.getRandomNumber(360);
            Triangle triangle = new Triangle(point1, point2, point3, randomDirection);
            mListTriangle.add(triangle);
        }
    }

    private int moveValueY = 3;
    private float moveValueX = 0;

    //移动生成的三角形
    private void moveGenerateTriangle() {
        Iterator<Triangle> iterator = mListTriangle.iterator();
        while (iterator.hasNext()) {
            Triangle triangle = iterator.next();
            PointF point1 = triangle.point1;
            PointF point2 = triangle.point2;
            PointF point3 = triangle.point3;
            point1.set(point1.x + moveValueX, point1.y + moveValueY);
            point2.set(point2.x + moveValueX, point2.y + moveValueY);
            point3.set(point3.x + moveValueX, point3.y + moveValueY);
            //设置透明度的值
            setTriangleAlpha(triangle);
        }
    }

    //设置每个三角形对应位置上的透明度
    //注意：透明度的值应该为该View所在矩形的对角线的一半为母
    private void setTriangleAlpha(Triangle triangle) {
        PointF point1 = triangle.getPoint1();
        double ratio = point1.y / mWidthCenter;
        if (ratio > 1)
            ratio = 1;
        triangle.setAlpha((float) (1 - ratio));
    }


    private void removeTriangleOutScreen(Triangle triangle, Iterator<Triangle> iterator) {
        PointF point1 = triangle.point1;
        PointF point2 = triangle.point2;
        PointF point3 = triangle.point3;
        /*if (point1.x < -mHeightCenter || point1.x > mHeightCenter || point1.y < -mWidthCenter || point1.y > mWidthCenter ||
                point2.x < -mHeightCenter || point2.x > mHeightCenter || point2.y < -mWidthCenter || point2.y > mWidthCenter ||
                point3.x < -mHeightCenter || point3.x > mHeightCenter || point3.y < -mWidthCenter || point3.y > mWidthCenter) {
            iterator.remove();
        }*/
        float halfOfDiagonal = mDiagonal / 2.f;
        if (point1.y > halfOfDiagonal || point2.y > halfOfDiagonal || point3.y > halfOfDiagonal) {
            iterator.remove();
        }

    }

    Path mPath = new Path();
    private List<TriangleBezier> mListTriangleBezier = new ArrayList<>();


    //随机生成贝塞尔三角形
    private void randomGenerateTriangleBezier(){
        if (mUtils.getRandomTrueOrFalse()) {
            //生成贝塞尔对象
            PointF startPoint = new PointF(0,0);
            PointF endPoint = new PointF(0,-mWidthCenter);
            PointF controlPoint = new PointF(-mWidthCenter/2f,-mWidthCenter/2f);
            Bezier bezier = new Bezier();
            bezier.setmStartPoint(startPoint);
            bezier.setmEndPoint(endPoint);
            bezier.setmControlPoint(controlPoint);
            bezier.setU(0);
            PointF pointOnTheBezier = mUtilsBezier.CalculateBezierPointForQuadratic(bezier.getU(),startPoint,controlPoint,endPoint);//生成贝塞尔曲线上对应的点
            bezier.setmPointOnTheBezier(pointOnTheBezier);
            //生成三角形
            PointF point1 = new PointF(bezier.getmPointOnTheBezier().x, bezier.getmPointOnTheBezier().y-mUtils.getRandomNumber(12));
            PointF point2 = new PointF(bezier.getmPointOnTheBezier().x-mUtils.getRandomNumber(30), bezier.getmPointOnTheBezier().y+mUtils.getRandomNumber(20));
            PointF point3 = new PointF(bezier.getmPointOnTheBezier().x+mUtils.getRandomNumber(30), bezier.getmPointOnTheBezier().y+mUtils.getRandomNumber(20));
            Triangle triangle = new Triangle(point1,point2,point3,0);//这里不控制direction，由TriangleBezier对象控制绘制角度
            TriangleBezier triangleBezier = new TriangleBezier(bezier,triangle,mUtils.getRandomNumber(360));
            mListTriangleBezier.add(triangleBezier);
        }
    }

    //移动贝塞尔曲线的比例，然后改变对应三角形的坐标
    private void moveGenerateTriangleBezier(){
        Iterator<TriangleBezier> iterator = mListTriangleBezier.iterator();
        while (iterator.hasNext()) {
            TriangleBezier triangleBezier = iterator.next();
            Bezier bezier = triangleBezier.getmBezier();
            float u = bezier.getU()+0.01f;
            if (u>=1)
                u = 1;
            bezier.setU(u);
            //重新计算在贝塞尔曲线上的点的坐标(更新贝塞尔曲线上的点)
            PointF pointOnTheBezier = mUtilsBezier.CalculateBezierPointForQuadratic(bezier.getU(),bezier.getmStartPoint(),bezier.getmControlPoint(),bezier.getmEndPoint());
            //更新三角形的坐标
            Triangle triangle = triangleBezier.getmTriangle();
            float offsetX = bezier.getmPointOnTheBezier().x-pointOnTheBezier.x;
            float offsetY = bezier.getmPointOnTheBezier().y-pointOnTheBezier.y;
            PointF point1 = triangle.getPoint1();
            PointF point2 = triangle.getPoint2();
            PointF point3 = triangle.getPoint3();
            point1.set(point1.x+offsetX,point1.y+offsetY);
            point2.set(point2.x+offsetX,point2.y+offsetY);
            point3.set(point3.x+offsetX,point3.y+offsetY);
            //更新bezier对象
            bezier.setmPointOnTheBezier(pointOnTheBezier);
            //设置透明度的值
            setTriangleAlpha(triangle);
        }
    }

    private void drawTriangleBeziers(Canvas canvas){
        canvas.drawColor(Color.BLACK, PorterDuff.Mode.CLEAR);
        Iterator<TriangleBezier> iterator = mListTriangleBezier.iterator();
        mPaint.setColor(Color.GREEN);
        while (iterator.hasNext()) {
            TriangleBezier triangleBezier = iterator.next();
            Triangle triangle = triangleBezier.getmTriangle();
            canvas.save();
            canvas.translate(mWidthCenter, mHeightCenter);
            canvas.rotate(triangleBezier.getmDegree());
            mPath.reset();
            mPath.moveTo(triangle.point1.x, -triangle.point1.y);
            mPath.lineTo(triangle.point2.x, -triangle.point2.y);
            mPath.lineTo(triangle.point3.x, -triangle.point3.y);
            mPath.close();
            //设置透明度
            mPaint.setAlpha((int) (255 * triangle.getAlpha()));
            canvas.drawPath(mPath, mPaint);
            canvas.restore();
            removeTriangleBezierOutOfScreen(triangle,iterator);
        }
    }

    private Paint mPaintBall = new Paint(Paint.ANTI_ALIAS_FLAG);

    private void drawBall(Canvas canvas){
        mPaintBall.setColor(Color.GREEN);
        canvas.save();
        canvas.translate(mWidthCenter,mHeightCenter);
        canvas.drawCircle(0,0,25,mPaintBall);
        canvas.restore();
    }

    private void removeTriangleBezierOutOfScreen(Triangle triangle,Iterator<TriangleBezier> iterator){
        PointF point1 = triangle.point1;
        PointF point2 = triangle.point2;
        PointF point3 = triangle.point3;
        /*if (triangle.getAlpha()<=0){
            iterator.remove();
            Log.e("xiaojun","remove");
        }*/

        float halfOfDiagonal = mDiagonal / 3.f;
        if (point1.y > halfOfDiagonal || point2.y > halfOfDiagonal || point3.y > halfOfDiagonal) {
            iterator.remove();
            Log.e("xiaojun","remove");
        }
    }

    //绘制三角形
    private void drawView(Canvas canvas) {
        //随机生成三角形
//        randomGenerateTriangle();
        //移动生成的三角形
//        moveGenerateTriangle();
        //画三角形
//        drawTriangles(canvas);
        //画最外圈的圆
//        drawOutCircle(canvas);

        //随机生成贝塞尔三角形
        randomGenerateTriangleBezier();
        //移动生成的贝塞尔三角形
        moveGenerateTriangleBezier();
        drawTriangleBeziers(canvas);
        drawOutCircle(canvas);
        drawBall(canvas);

    }

    private int mCircleAlpha;
    private boolean mChangeLight = true;//是否变亮

    private void drawOutCircle(Canvas canvas) {
        mPaint.setColor(Color.GREEN);
        if (mChangeLight)
            mCircleAlpha += 5;
        else
            mCircleAlpha -= 5;
        if (mCircleAlpha >= 255)
            mChangeLight = false;
        else if (mCircleAlpha <= 0)
            mChangeLight = true;
        if (mCircleAlpha > 255)
            mCircleAlpha = 255;
        if (mCircleAlpha < 0)
            mCircleAlpha = 0;
        //设置外圈透明度
        mPaint.setAlpha(mCircleAlpha);
        canvas.save();
        canvas.translate(mWidthCenter, mHeightCenter);
        canvas.drawCircle(0, 0, mWidthCenter - 3, mPaint);
        canvas.restore();
    }

    private void drawTriangles(Canvas canvas) {
        if (mListTriangle.size() != 0)
            Log.e("xiaojun", "mList.get(0).x=" + mListTriangle.get(0).getPoint1().x+",mList.get(0).getDirection="+mListTriangle.get(0).getDirection());
        canvas.drawColor(Color.BLACK, PorterDuff.Mode.CLEAR);
        Iterator<Triangle> iterator = mListTriangle.iterator();
        while (iterator.hasNext()) {
            Triangle triangle = iterator.next();
            canvas.save();
            canvas.translate(mWidthCenter, mHeightCenter);
            canvas.rotate(triangle.direction);
            mPath.reset();
            mPath.moveTo(triangle.point1.x, triangle.point1.y);
            mPath.lineTo(triangle.point2.x, triangle.point2.y);
            mPath.lineTo(triangle.point3.x, triangle.point3.y);
            mPath.close();
            //设置透明度
            mPaint.setAlpha((int) (255 * triangle.getAlpha()));
            canvas.drawPath(mPath, mPaint);
            canvas.restore();
            removeTriangleOutScreen(triangle, iterator);//移出屏幕外的三角形
        }
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {

    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        mIsRunning = false;
    }

    @Override
    public void run() {
        while (mIsRunning) {
            startDraw();
        }
    }

    private float mWidth, mHeight, mWidthCenter, mHeightCenter, mDiagonal;//对角线长度

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        this.mWidth = w;
        this.mHeight = h;
        this.mWidthCenter = w / 2.f;
        this.mHeightCenter = h / 2.f;
        this.mDiagonal = (float) Math.sqrt(Math.pow(mWidth, 2) + Math.pow(mHeight, 2));
    }


    private void startDraw() {
        mCanvas = mHolder.lockCanvas();
        if (mCanvas != null) {
            drawView(mCanvas);
            mHolder.unlockCanvasAndPost(mCanvas);
        }
    }

    //贝塞尔曲线(二阶)
    class Bezier{
        private PointF mStartPoint;//起点(数据点)
        private PointF mEndPoint;//终点(数据点)
        private PointF mControlPoint;//控制点
        private float u;//比例

        private PointF mPointOnTheBezier;//在贝塞尔曲线上的点

        public PointF getmPointOnTheBezier() {
            return mPointOnTheBezier;
        }

        public void setmPointOnTheBezier(PointF mPointOnTheBezier) {
            this.mPointOnTheBezier = mPointOnTheBezier;
        }

        public float getU() {
            return u;
        }

        public void setU(float u) {
            this.u = u;
        }

        public PointF getmStartPoint() {
            return mStartPoint;
        }

        public void setmStartPoint(PointF mStartPoint) {
            this.mStartPoint = mStartPoint;
        }

        public PointF getmEndPoint() {
            return mEndPoint;
        }

        public void setmEndPoint(PointF mEndPoint) {
            this.mEndPoint = mEndPoint;
        }

        public PointF getmControlPoint() {
            return mControlPoint;
        }

        public void setmControlPoint(PointF mControlPoint) {
            this.mControlPoint = mControlPoint;
        }
    }

    //三角形
    class Triangle {
        private PointF point1, point2, point3;
        private float alpha;
        private int direction;//移动方向0到360度

        public Triangle(PointF point1, PointF point2, PointF point3, int direction) {
            this.point1 = point1;
            this.point2 = point2;
            this.point3 = point3;
            this.alpha = 1;
            this.direction = direction;
        }

        public void setAlpha(float alpha) {
            this.alpha = alpha;
        }

        public float getAlpha() {
            return this.alpha;
        }

        public PointF getPoint1() {
            return point1;
        }

        public PointF getPoint2() {
            return point2;
        }

        public PointF getPoint3() {
            return point3;
        }

        public void setPoint1(PointF point1) {
            this.point1 = point1;
        }

        public void setPoint2(PointF point2) {
            this.point2 = point2;
        }

        public void setPoint3(PointF point3) {
            this.point3 = point3;
        }

        public int getDirection() {
            return direction;
        }

        public void setDirection(int direction) {
            this.direction = direction;
        }
    }

    //包含贝塞尔对象的三角形,三角形每个点都有一个贝塞尔对象
    class TriangleBezier{
        private Bezier mBezier;
        private int mDegree;//角度 0~360
        private Triangle mTriangle;//包含一个三角形

        public TriangleBezier(Bezier bezier,Triangle triangle,int degree){
            this.mBezier = bezier;
            this.mTriangle = triangle;
            this.mDegree = degree;
        }

        public Bezier getmBezier() {
            return mBezier;
        }

        public void setmBezier(Bezier mBezier) {
            this.mBezier = mBezier;
        }

        public int getmDegree() {
            return mDegree;
        }

        public void setmDegree(int mDegree) {
            this.mDegree = mDegree;
        }

        public Triangle getmTriangle() {
            return mTriangle;
        }

        public void setmTriangle(Triangle mTriangle) {
            this.mTriangle = mTriangle;
        }
    }
}
