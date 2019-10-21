package com.example.testproject.scroller;

import android.content.Context;
import android.graphics.Color;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Scroller;

import androidx.annotation.Nullable;

/**
 * 可以拖动(Drag)的View，松手的时候会恢复原位置
 * //视图坐标系:坐标原点位于父视图的左上角(0,0)
 * //Android坐标系:坐标原点位于屏幕左上角(0,0)
 * //getLeft:本View位于对应父View左边的距离(视图坐标系)
 * //getRayX:触摸到本View的位置对应屏幕的绝对坐标(Android坐标系)
 * //getX:触摸点到本View左边的距离(视图坐标系)
 * created by xiaojun at 2019/10/21
 */
public class DragView extends View {
    public DragView(Context context) {
        this(context, null);
    }

    public DragView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public DragView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    private void init(Context context) {
        setBackgroundColor(Color.RED);
        mScroller = new Scroller(context);
    }

    private Scroller mScroller;

    @Override
    public void computeScroll() {
        super.computeScroll();
        if (mScroller.computeScrollOffset()){
            ((View) getParent()).scrollTo(mScroller.getCurrX(), mScroller.getCurrY());
            invalidate();
        }
    }

    private float mLastX, mLastY;

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        float x = event.getX();
        float y = event.getY();
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                mLastX = event.getX();
                mLastY = event.getY();
                break;
            case MotionEvent.ACTION_MOVE:
                float offsetX = x - mLastX;
                float offsetY = y - mLastY;
                ((View) getParent()).scrollBy(-(int) offsetX, -(int) offsetY);
                break;
            case MotionEvent.ACTION_UP:
                View viewGroup = (View) getParent();
                mScroller.startScroll(viewGroup.getScrollX(), viewGroup.getScrollY(),
                        -viewGroup.getScrollX(), -viewGroup.getScrollY(), 1000);
                invalidate();
                break;
        }
        return true;
    }
}
