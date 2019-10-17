package com.example.testproject.cakeview;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Point;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Xfermode;
import android.util.AttributeSet;
import android.view.View;

import androidx.annotation.Nullable;

import java.util.List;

/**
 * 饼状图
 * 分成四份
 * created by xiaojun at 2019/10/15
 */
public class CakeView extends View {

    private Paint mPaint, mPaintRatio;
    private int mCenterX, mCenterY;
    private int mLineLength = 300;
    private List<Float> mDataList;//比例集合
    private double mCenterYLength;
    private double mCenterXLength;
    private int mTempLength;
    private Bitmap mSrcImg,mDstImg;
    private Path mPath = new Path();

    public CakeView(Context context) {
        this(context, null);
    }

    public CakeView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public CakeView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mPaint.setColor(Color.parseColor("#F9C370"));
        mPaint.setStrokeWidth(2);
        mPaint.setStyle(Paint.Style.STROKE);
        mPaintRatio = new Paint(Paint.ANTI_ALIAS_FLAG);
        mPaintRatio.setColor(Color.YELLOW);
        mPaintRatio.setStyle(Paint.Style.FILL);
        calculateLength();
        mTempLength = mLineLength / 4;
        setLayerType(View.LAYER_TYPE_SOFTWARE, null);
    }

    private void refreshImg(){
        mSrcImg = buildHouse();
        mDstImg = drawRatioImg();
    }

    public void setDataList(List<Float> list) {
        this.mDataList = list;
        invalidate();
    }

    private int mWidth,mHeight;

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        this.mCenterX = w / 2;
        this.mCenterY = h / 2;
        this.mWidth = w;
        this.mHeight = h;
        refreshImg();
    }

    private Xfermode mXfermode = new PorterDuffXfermode(PorterDuff.Mode.SRC_IN);

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        int layerID = canvas.saveLayer(0,0,mWidth,mHeight,mPaintRatio,Canvas.ALL_SAVE_FLAG);
        canvas.drawBitmap(mDstImg,0,0,mPaintRatio);
        mPaintRatio.setXfermode(mXfermode);
        canvas.drawBitmap(mSrcImg,0,0,mPaintRatio);
        mPaint.setXfermode(null);
        canvas.restoreToCount(layerID);
        drawBackground(canvas);

    }

    int[] mPaintColors = new int[]{Color.parseColor("#FFAD2D"),Color.parseColor("#FFBC54"),Color.parseColor("#FFC975"),Color.parseColor("#FFDFAD")};

    private Bitmap buildHouse(){
        Bitmap bm = Bitmap.createBitmap(mWidth,mHeight,Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bm);
        canvas.translate(mCenterX,mCenterY);
        canvas.save();
        canvas.rotate(36);
        int lineLength = mLineLength;
        double xLength = lineLength*Math.sin(Math.toRadians(36));
        double yLength = xLength/Math.tan(Math.toRadians(36));
        for (int i = 0; i < 4; i++) {
            mPaintRatio.setColor(mPaintColors[i]);
            for(int j=0;j<5;j++){
                mPath.reset();
                mPath.moveTo(0,0);
                mPath.lineTo((float) xLength, (float) -yLength);
                mPath.lineTo((float)-xLength,(float)-yLength);
                mPath.close();
                canvas.drawPath(mPath,mPaintRatio);
                canvas.rotate(72);
            }
            lineLength -= mTempLength;
            xLength = lineLength*Math.sin(Math.toRadians(36));
            yLength = xLength/Math.tan(Math.toRadians(36));
        }
        canvas.restore();
        return bm;
    }

    private Bitmap drawRatioImg() {
        Bitmap bm = Bitmap.createBitmap(mWidth,mHeight,Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bm);
        canvas.translate(mCenterX,mCenterY);
        canvas.save();
        canvas.rotate(36);
        for (int i = 0; i < 4; i++) {
            reDrawPath(canvas,i,i+1);
            canvas.rotate(72);
        }
        //画最后一个图形
        reDrawPath(canvas,4,0);
        canvas.restore();
        return bm;
    }

    private void reDrawPath(Canvas canvas,int position1,int position2){
        Point[] points = getTwoLocation((int)(mLineLength*mDataList.get(position1)), (int) (mLineLength*mDataList.get(position2)));
        mPath.reset();
        mPath.moveTo(0, 0);
        mPath.lineTo(points[0].x, points[0].y);
        mPath.lineTo(points[1].x, points[1].y);
        mPath.close();
        canvas.drawPath(mPath, mPaintRatio);
    }

    private Point[] getTwoLocation(int length1, int length2) {
        int centerXLengthImg1 = (int) (Math.sin(Math.toRadians(36)) * length1);
        int centerYLengthImg1 = (int) (centerXLengthImg1 / Math.tan(Math.toRadians(36)));
        Point point1 = new Point(-centerXLengthImg1, -centerYLengthImg1);
        int centerXLengthImg2 = (int) (Math.sin(Math.toRadians(36)) * length2);
        int centerYLengthImg2 = (int) (centerXLengthImg2 / Math.tan(Math.toRadians(36)));
        Point point2 = new Point(centerXLengthImg2, -centerYLengthImg2);
        return new Point[]{point1, point2};
    }

    //360/5=72度
    private void drawBackground(Canvas canvas) {
        canvas.translate(mCenterX,mCenterY);
        canvas.save();
        for (int i = 0; i < 5; i++) {
            canvas.drawLine(0, 0, 0, -mLineLength, mPaint);
            canvas.rotate(72);
        }
        canvas.rotate(36);
        for (int i = 0; i < 4; i++) {
            for (int j = 0; j < 5; j++) {
                canvas.drawLine((int) mCenterXLength, (int) -mCenterYLength, (int) -mCenterXLength, (int) -mCenterYLength, mPaint);
                canvas.rotate(72);
            }
            if (i < 3) {
                mLineLength -= mTempLength;
                calculateLength();
            }
        }
        canvas.restore();
        mLineLength = 300;
    }

    //重新计算长度
    private void calculateLength() {
        mCenterXLength = Math.sin(Math.toRadians(36)) * mLineLength;
        mCenterYLength = mCenterXLength / Math.tan(Math.toRadians(36));
    }

}
