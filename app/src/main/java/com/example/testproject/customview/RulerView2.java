package com.example.testproject.customview;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.media.audiofx.Visualizer;
import android.util.AttributeSet;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Scroller;

import androidx.annotation.Nullable;

import java.util.ArrayList;
import java.util.List;

/**
 * 可以滑动的尺子View2
 * created by xiaojun at 2019/11/11
 */
public class RulerView2 extends View {

    //刻度画笔和文字画笔
    private Paint mPaintDividing, mPaintText, mPaintLine;
    private int mWidth, mHeight, mWidthCenter, mHeightCenter;

    private List<String> mDatas = new ArrayList<>();

    public RulerView2(Context context) {
        this(context, null);
    }

    public RulerView2(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public RulerView2(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    private float mHeightText;//Bottom-Top
    private float mBottomText;//文字Bottom
    private float mTextSize = 44;//文字尺寸

    private void init(Context context) {
        mPaintDividing = new Paint(Paint.ANTI_ALIAS_FLAG);
        mPaintDividing.setStyle(Paint.Style.FILL);
        mPaintDividing.setStrokeWidth(10);
        mPaintDividing.setColor(Color.WHITE);

        mPaintLine = new Paint(Paint.ANTI_ALIAS_FLAG);
        mPaintLine.setStyle(Paint.Style.STROKE);
        mPaintLine.setColor(Color.parseColor("#e7ff8c"));

        mPaintText = new Paint(Paint.ANTI_ALIAS_FLAG);
        mPaintText.setTextSize(mTextSize);
        mPaintText.setColor(Color.parseColor("#ff33b5e5"));
        mPaintText.setTextAlign(Paint.Align.CENTER);
        calculateTextHeight();

        mScroller = new Scroller(context);
        mGestureDetector = new GestureDetector(context, new MyGestureDetector());

        //初始化测试数据
        for (int i = 0; i < 10; i++) {
            mDatas.add((i + ""));
        }
    }

    private void calculateTextHeight() {
        mPaintText.measureText("abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789");
        Paint.FontMetrics fontMetrics = mPaintText.getFontMetrics();
        mHeightText = Math.abs(fontMetrics.bottom - fontMetrics.top);
        mBottomText = fontMetrics.bottom;
    }

    class MyGestureDetector implements GestureDetector.OnGestureListener {

        @Override
        public boolean onDown(MotionEvent e) {
            mScroller.abortAnimation();
            return false;
        }

        @Override
        public void onShowPress(MotionEvent e) {

        }

        @Override
        public boolean onSingleTapUp(MotionEvent e) {
            return false;
        }

        @Override
        public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
            return false;
        }

        @Override
        public void onLongPress(MotionEvent e) {

        }

        @Override
        public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
            //注意:第三个参数传递速度的时候注意加上-号，因为我们移动的是画布上面的蒙板，刚好相反
            mScroller.setFriction(0.1f);//设置摩擦力
            mScroller.fling(getScrollX(), 0, -(int) velocityX, 0, 0, (mDatas.size() - 1) * 200, 0, 0);
            invalidate();
            return false;
        }
    }

    @Override
    public void computeScroll() {
        if (mScroller.computeScrollOffset()) {
            scrollTo(mScroller.getCurrX(), mScroller.getCurrY());
            invalidate();
        }
    }

    private float lastX, lastY;
    private Scroller mScroller;
    private GestureDetector mGestureDetector;

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        mGestureDetector.onTouchEvent(event);
        float x = event.getX();
        float y = event.getY();
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                lastX = x;
                lastY = y;
                break;
            case MotionEvent.ACTION_MOVE:
                float offsetX = x - lastX;
                float offsetY = y - lastY;
                scrollBy(-(int) offsetX, 0);
                lastX = x;
                lastY = y;
                break;
            case MotionEvent.ACTION_CANCEL:
            case MotionEvent.ACTION_UP:
                Log.e("xiaojun", "scrollX=" + getScrollX());
//                mScroller.startScroll(getScrollX(),0,-getScrollX(),0,2000);//回到原点(屏幕中线)位置
//                invalidate();
                break;
        }
        return true;
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        this.mWidth = w;
        this.mHeight = h;
        this.mWidthCenter = w / 2;
        this.mHeightCenter = h / 2;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        drawCircleDividing(canvas);//画圆形刻度
        drawCenterDivding(canvas);//画中间的线
    }

    private void drawCircleDividing(Canvas canvas) {
        float distanceDividingCircle = 40;
        float circleRadius = 5;//圆球半径
        int numOfDividingCircle = 4;//刻度值之间小圆球的个数

        float totalOffsetX = 0;

        float offsetXPeriod = getScrollX() % (distanceDividingCircle * 5);
        int indexFront = (int) (getScrollX() / (distanceDividingCircle * 5));//索引
        float ratio = offsetXPeriod / 200.f;

        for (int i = 0; i < mDatas.size(); i++) {
            //计算缩放比例
            if (indexFront >= 0 && offsetXPeriod >= 0) {
                if (indexFront == i) {
                    mPaintText.setTextSize(mTextSize + 56 * (1 - ratio));
                } else if (indexFront < mDatas.size() - 1 && i == indexFront + 1) {
                    mPaintText.setTextSize(mTextSize + 56 * ratio);
                } else {
                    mPaintText.setTextSize(mTextSize);
                }
            } else if (indexFront == 0 && offsetXPeriod < 0) {
                if (indexFront == i) {
                    mPaintText.setTextSize(mTextSize + 56 * (1-Math.abs(ratio)));
                } else {
                    mPaintText.setTextSize(mTextSize);
                }
            } else {
                mPaintText.setTextSize(mTextSize);
            }


            //重新计算文字高度
            calculateTextHeight();
            //画刻度值文字
            canvas.drawText(mDatas.get(i), mWidthCenter + totalOffsetX, mHeightCenter - mBottomText + mHeightText / 2.f, mPaintText);
            //画小圆球
            if (i < mDatas.size() - 1) {
                for (int j = 0; j < numOfDividingCircle; j++) {
                    totalOffsetX += distanceDividingCircle;
                    canvas.drawCircle(mWidthCenter + totalOffsetX, mHeightCenter, circleRadius, mPaintDividing);
                }
                totalOffsetX += distanceDividingCircle;
            }

        }
    }

    private void drawDividing(Canvas canvas) {
        float distanceDividing = 40;//每个刻度之间的距离
        float lengthLongDividing = 50;//长刻度长度
        float lengthShortDividing = lengthLongDividing * 2.f / 3;//短刻度长度

        int numOfShortDividing = 4;//长刻度之间的短刻度个数

        float totalLength = 0;

        for (int i = 0; i < mDatas.size(); i++) {
            canvas.drawLine(mWidthCenter + totalLength, 0, mWidthCenter + totalLength, lengthLongDividing, mPaintDividing);//画长刻度
            canvas.drawText(mDatas.get(i), (int) (mWidthCenter + totalLength), lengthLongDividing + 50, mPaintText);
            if (i < mDatas.size() - 1) {//画了最后一个长刻度后就不用画后面的短刻度了
                for (int j = 0; j < numOfShortDividing; j++) {
                    totalLength += distanceDividing;
                    canvas.drawLine(mWidthCenter + totalLength, 0, mWidthCenter + totalLength, lengthShortDividing, mPaintDividing);//画短刻度
                }
                totalLength += distanceDividing;
            }
        }
    }

    private void drawDividText(Canvas canvas) {

    }

    private void drawCenterDivding(Canvas canvas) {
        canvas.drawLine(mWidthCenter + getScrollX(), 0, mWidthCenter + getScrollX(), mHeight, mPaintLine);
    }


}
