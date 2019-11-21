package com.example.testproject.customview;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.BlurMaskFilter;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PointF;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.util.AttributeSet;
import android.view.View;

import androidx.annotation.Nullable;

import com.example.testproject.R;
import com.example.testproject.Utils;
import com.example.testproject.UtilsBezier;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class ComplexView extends View {

    private Paint mPaint, mPaintDst;
    private float mWidth, mHeight, mWidthCenter, mHeightCenter, mDiagonal;//对角线长度
    Path mPath = new Path();
    private List<TriangleBezier> mListTriangleBezier = new ArrayList<>();
    private Utils mUtils = new Utils();
    private List<AnimationView.Triangle> mListTriangle = new ArrayList<>();
    private UtilsBezier mUtilsBezier = new UtilsBezier();

    public ComplexView(Context context) {
        this(context, null);
    }

    public ComplexView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ComplexView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }


    private void init() {
        this.mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        this.mPaint.setColor(Color.RED);
        this.mPaint.setStyle(Paint.Style.STROKE);
        this.mPaint.setStrokeWidth(10);
        this.mPaint.setStrokeCap(Paint.Cap.ROUND);
//        this.mPaint.setMaskFilter(new BlurMaskFilter(5, BlurMaskFilter.Blur.SOLID));//设置外发光

        this.mPaintDst = new Paint(Paint.ANTI_ALIAS_FLAG);
        this.mPaintDst.setColor(Color.GREEN);
        this.mPaintDst.setStyle(Paint.Style.STROKE);
        this.mPaintDst.setStrokeWidth(5);


//        this.setLayerType(LAYER_TYPE_SOFTWARE, null);

        mPorterDuffXfermode = new PorterDuffXfermode(PorterDuff.Mode.SRC_IN);

        mBitmapDst = BitmapFactory.decodeResource(getResources(), R.drawable.test);

        mPaintSrc2 = new Paint(Paint.ANTI_ALIAS_FLAG);
        mPaintSrc2.setColor(Color.RED);
    }


    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        this.mWidth = w;
        this.mHeight = h;
        this.mWidthCenter = w / 2.f;
        this.mHeightCenter = h / 2.f;
        this.mDiagonal = (float) Math.sqrt(Math.pow(mWidth, 2) + Math.pow(mHeight, 2));

        this.mBitmapSrc = Bitmap.createBitmap((int) mWidth, (int) mHeight, Bitmap.Config.ARGB_8888);
    }

    private PorterDuffXfermode mPorterDuffXfermode;

    @Override
    protected void onDraw(Canvas canvas) {
        //随机生成贝塞尔三角形
//        randomGenerateTriangleBezier();
        //移动生成的贝塞尔三角形
//        moveGenerateTriangleBezier();
        int layerID = canvas.saveLayer(0, 0, mWidth, mHeight, mPaint);//新建一个图层


        //画贝塞尔三角形
//        drawTriangleBeziers(canvas);
        //画逐渐变长的矩形,Dst图像
        drawChangeRect(canvas);
        mPaint.setXfermode(mPorterDuffXfermode);
        //画Src图像
        drawDst(canvas);
        mPaint.setXfermode(null);
        canvas.restoreToCount(layerID);
        invalidate();
    }

    private Bitmap mBitmapSrc2;
    private int mSrcLength = 1;
    private Canvas mCanvasSrc2 = new Canvas();
    private Paint mPaintSrc2;

    private void drawChangeRect(Canvas canvas){
        mBitmapSrc2 = Bitmap.createBitmap(mSrcLength,(int)mHeight, Bitmap.Config.ARGB_8888);
        mCanvasSrc2.drawColor(Color.BLACK,PorterDuff.Mode.CLEAR);
        mCanvasSrc2.setBitmap(mBitmapSrc2);
        mCanvasSrc2.drawRect(0,0,mSrcLength,mHeight,mPaintSrc2);
        canvas.drawBitmap(mBitmapSrc2,0,0,mPaint);
        mSrcLength +=1;
    }

    private Bitmap mBitmapDst;
    private void drawDst(Canvas canvas) {
        canvas.drawBitmap(mBitmapDst, 0, 0, mPaint);
    }

    //设置每个三角形对应位置上的透明度
    //注意：透明度的值应该为该View所在矩形的对角线的一半为母
    private void setTriangleAlpha(Triangle triangle) {
        PointF point1 = triangle.getPoint1();
        double ratio = Math.abs(point1.y / mWidthCenter);
        if (ratio > 1)
            ratio = 1;
        triangle.setAlpha((float) (1 - ratio));
    }

    //随机生成贝塞尔三角形
    private void randomGenerateTriangleBezier() {
        if (mUtils.getRandomTrueOrFalse()) {
            //生成贝塞尔对象
            PointF startPoint = new PointF(0, 0);
            PointF endPoint = new PointF(0, -mWidthCenter);
            PointF controlPoint = new PointF(-mWidthCenter / 2f, -mWidthCenter / 2f);
            Bezier bezier = new Bezier();
            bezier.setmStartPoint(startPoint);
            bezier.setmEndPoint(endPoint);
            bezier.setmControlPoint(controlPoint);
            bezier.setU(0);
            PointF pointOnTheBezier = mUtilsBezier.CalculateBezierPointForQuadratic(bezier.getU(), startPoint, controlPoint, endPoint);//生成贝塞尔曲线上对应的点
            bezier.setmPointOnTheBezier(pointOnTheBezier);
            //生成三角形
            PointF point1 = new PointF(bezier.getmPointOnTheBezier().x, bezier.getmPointOnTheBezier().y - mUtils.getRandomNumber(12));
            PointF point2 = new PointF(bezier.getmPointOnTheBezier().x - mUtils.getRandomNumber(30), bezier.getmPointOnTheBezier().y + mUtils.getRandomNumber(20));
            PointF point3 = new PointF(bezier.getmPointOnTheBezier().x + mUtils.getRandomNumber(30), bezier.getmPointOnTheBezier().y + mUtils.getRandomNumber(20));
            Triangle triangle = new Triangle(point1, point2, point3, 0);//这里不控制direction，由TriangleBezier对象控制绘制角度
            TriangleBezier triangleBezier = new TriangleBezier(bezier, triangle, mUtils.getRandomNumber(360));
            triangleBezier.setR(mUtils.getRandomNumber(255));
            triangleBezier.setG(mUtils.getRandomNumber(255));
            triangleBezier.setB(mUtils.getRandomNumber(255));
            mListTriangleBezier.add(triangleBezier);
        }
    }

    //移动贝塞尔曲线的比例，然后改变对应三角形的坐标
    private void moveGenerateTriangleBezier() {
        Iterator<TriangleBezier> iterator = mListTriangleBezier.iterator();
        while (iterator.hasNext()) {
            TriangleBezier triangleBezier = iterator.next();
            Bezier bezier = triangleBezier.getmBezier();
            float u = bezier.getU() + 0.01f;
            if (u >= 1)
                u = 1;
            bezier.setU(u);
            //重新计算在贝塞尔曲线上的点的坐标(更新贝塞尔曲线上的点)
            PointF pointOnTheBezier = mUtilsBezier.CalculateBezierPointForQuadratic(bezier.getU(), bezier.getmStartPoint(), bezier.getmControlPoint(), bezier.getmEndPoint());
            //更新三角形的坐标
            Triangle triangle = triangleBezier.getmTriangle();
            float offsetX = bezier.getmPointOnTheBezier().x - pointOnTheBezier.x;
            float offsetY = bezier.getmPointOnTheBezier().y - pointOnTheBezier.y;
            PointF point1 = triangle.getPoint1();
            PointF point2 = triangle.getPoint2();
            PointF point3 = triangle.getPoint3();
            point1.set(point1.x + offsetX, point1.y + offsetY);
            point2.set(point2.x + offsetX, point2.y + offsetY);
            point3.set(point3.x + offsetX, point3.y + offsetY);
            //更新bezier对象
            bezier.setmPointOnTheBezier(pointOnTheBezier);
            //设置透明度的值
            setTriangleAlpha(triangle);
        }
    }

    private Canvas mCanvasSrc = new Canvas();
    private Bitmap mBitmapSrc;

    private void drawTriangleBeziers(Canvas canvas) {
        mBitmapSrc = Bitmap.createBitmap((int)mWidth,(int)mHeight, Bitmap.Config.ARGB_8888);
        mCanvasSrc.setBitmap(mBitmapSrc);
//        mBitmapSrc = Bitmap.createBitmap((int)mWidth,(int)mHeight, Bitmap.Config.ARGB_8888);
//        mCanvasSrc = new Canvas(mBitmapSrc);
        Iterator<TriangleBezier> iterator = mListTriangleBezier.iterator();
        while (iterator.hasNext()) {
            TriangleBezier triangleBezier = iterator.next();
            Triangle triangle = triangleBezier.getmTriangle();
            mCanvasSrc.save();
            mCanvasSrc.translate(mWidthCenter, mHeightCenter);
            mCanvasSrc.rotate(triangleBezier.getmDegree());
            mPath.reset();
            mPath.moveTo(triangle.point1.x, -triangle.point1.y);
            mPath.lineTo(triangle.point2.x, -triangle.point2.y);
            mPath.lineTo(triangle.point3.x, -triangle.point3.y);
            mPath.close();
            //设置画笔透明度和RGB颜色
            mPaintDst.setColor(Color.argb((int) (255 * triangle.getAlpha()), triangleBezier.getR(), triangleBezier.getG(), triangleBezier.getB()));
//            mCanvasSrc.drawLine(triangle.point1.x, triangle.point1.y, triangle.point2.x, triangle.point2.y, mPaint);
            mCanvasSrc.drawPath(mPath, mPaintDst);
            mCanvasSrc.restore();
            removeTriangleBezierOutOfScreen(triangle, iterator);
        }
        canvas.drawBitmap(mBitmapSrc, 0, 0, mPaint);
    }

    private void removeTriangleBezierOutOfScreen(Triangle triangle, Iterator<TriangleBezier> iterator) {
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
        }
    }


    //贝塞尔曲线(二阶)
    class Bezier {
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
    class TriangleBezier {
        private Bezier mBezier;
        private int mDegree;//角度 0~360
        private Triangle mTriangle;//包含一个三角形
        private int r;//颜色
        private int g;
        private int b;

        public TriangleBezier(Bezier bezier, Triangle triangle, int degree) {
            this.mBezier = bezier;
            this.mTriangle = triangle;
            this.mDegree = degree;
            this.r = 0;
            this.g = 255;
            this.b = 0;
        }

        public int getR() {
            return r;
        }

        public void setR(int r) {
            this.r = r;
        }

        public int getG() {
            return g;
        }

        public void setG(int g) {
            this.g = g;
        }

        public int getB() {
            return b;
        }

        public void setB(int b) {
            this.b = b;
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
