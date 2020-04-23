package com.example.testproject.recyclerview;

import android.graphics.Rect;
import android.util.Log;
import android.util.SparseArray;
import android.view.View;

import androidx.recyclerview.widget.RecyclerView;

public class LayoutManagerFinal2 extends RecyclerView.LayoutManager {

    //存储当前所有Item的位置
    private SparseArray<Rect> mItems = new SparseArray<>();
    //当前所有item的总宽度
    private int mTotalItemWidth;
    //一个Item的宽度
    private int mWidth;
    //当前选中的item
    private int mPositionSelectedItem;
    //当前位移的总距离
    private int mTotalOffsetX;
    //当前屏幕的Rect
    private Rect mRectScreen = new Rect(0, 0, getRecyclerViewWidth(), getRecyclerViewHeight());

    //获取屏幕上能容纳的最大子View个数
    private int getScreenViewMaxCount() {
        return (int) Math.ceil(1.f * getRecyclerViewWidth() / mWidth);
    }

    private int getRecyclerViewWidth() {
        return getWidth();
    }

    private int getRecyclerViewHeight() {
        return getHeight();
    }

    @Override
    public RecyclerView.LayoutParams generateDefaultLayoutParams() {
        return new RecyclerView.LayoutParams(RecyclerView.LayoutParams.WRAP_CONTENT, RecyclerView.LayoutParams.WRAP_CONTENT);
    }

    /**
     * 1.初始化
     * 2.notifydatasetchange
     * 3.setAdapter
     *
     * @param recycler
     * @param state
     */
    @Override
    public void onLayoutChildren(RecyclerView.Recycler recycler, RecyclerView.State state) {
        Log.e("xiaojun", "onlayoutChildren");
        if (getItemCount() == 0) {
            detachAndScrapAttachedViews(recycler);
            return;
        }

        //---------------------------------------------测量-----------------------------------------------
        //如果总Item数量和保存的Rect的数量相等，就不用测量，否则就测量
        if (getItemCount() != mItems.size()) {
            mItems.clear();//清除之前保存的所有item
            View child = recycler.getViewForPosition(0);
            addView(child);
            measureChild(child, 0, 0);
            int width = getDecoratedMeasuredWidth(child);
            int height = getDecoratedMeasuredHeight(child);
            mWidth = width;
            detachAndScrapView(child, recycler);//测量完成之后就将这个View放回缓存器
            int offsetX = 0;
            //保存所有item的位置
            for (int i = 0; i < getItemCount(); i++) {
                Rect rect = new Rect(offsetX, 0, offsetX + width, height);
                mItems.put(i, rect);
                offsetX += width;
            }
            mTotalItemWidth = offsetX;
        }
        //数据集变化(1.notifyDataSetChange)
        //1.如果最新的数据集的个数 < 之前的数据集个数 :
        //(1)需要判断 mPositionSelected 当前选中的View的Position是否大于 最新数据集 的最大index
        //(2)需要判断 mTotalOffsetX 当前位移的总距离是否 超过 最大可移动总距离
        if (getChildCount() != 0) {
            int totalItemCount = getItemCount();
            //如果新的数据集的总数量 > 屏幕能容纳的最大子View数量
            if (totalItemCount >= getScreenViewMaxCount()) {
                if (totalItemCount < mItems.size()) {
                    //如果之前选中的位置已经超过了 最新 item 数量的最大index，
                    if (mPositionSelectedItem > totalItemCount - 1) {
                        mPositionSelectedItem = totalItemCount - 1;
                        //移动到最右边能移动的最大距离
                        mTotalOffsetX = mTotalItemWidth - getRecyclerViewWidth();
                    }
                }
                //如果 当前item总数量 和 之前保存的item的总数量相当或者 大于，mPositionSelectedItem和mTotalOffsetX就不用更改
            }
            //如果新的数据集总量 < 屏幕能容纳的最大子View数量
            else {
                //还原
                //不能移动，默认选中第一个item
                mTotalOffsetX = 0;
                mPositionSelectedItem = 0;
            }
        }

        //离屏缓存
        detachAndScrapAttachedViews(recycler);

        layoutChildren(recycler);

    }

    //布局应该显示在屏幕上的View，根据当前位移的距离
    private void layoutChildren(RecyclerView.Recycler recycler) {
        Rect rectRecyclerView = getScreenRectWithTotalOffsetX();
        int visibleCount = getScreenViewMaxCount();
        Log.e("xiaojun", "visibleCount=" + visibleCount);
        for (int i = 0, visibleNum = 0; i < mItems.size() && visibleNum < visibleCount; i++) {
            Rect rect = mItems.get(i);
            View child = recycler.getViewForPosition(i);
            //相交的child才显示出来
            if (Rect.intersects(rectRecyclerView, rect)) {
                addView(child);
                measureChild(child, 0, 0);
                layoutDecorated(child, rect.left - mTotalOffsetX, rect.top, rect.right - mTotalOffsetX, rect.bottom);
                visibleNum++;
            }
        }
    }

    /**
     * 获取移动了{@link #mTotalOffsetX}距离的Rect
     *
     * @return
     */
    private Rect getScreenRectWithTotalOffsetX() {
        mRectScreen.set(mTotalOffsetX, 0, mTotalOffsetX + getRecyclerViewWidth(), getRecyclerViewHeight());
        return mRectScreen;
    }

    @Override
    public boolean canScrollHorizontally() {
        return true;
    }

    private int DIRECTION_LEFT_TO_RIGHT = 1;
    private int DIRECTION_RIGHT_TO_LEFT = 2;

    @Override
    public int scrollHorizontallyBy(int dx, RecyclerView.Recycler recycler, RecyclerView.State state) {
        mTotalOffsetX += dx;
        int direction = dx > 0 ? DIRECTION_RIGHT_TO_LEFT : DIRECTION_LEFT_TO_RIGHT;

        if (getChildCount() == 0)
            return 0;
        View childFirst = getChildAt(0);
        View childLast = getChildAt(getChildCount() - 1);
        int positionFirst = getPosition(childFirst);
        int positionLast = getPosition(childLast);
        recyclerViews(recycler, direction, positionFirst, positionLast);
        addItem(recycler, direction, positionFirst, positionLast);
//        offsetChildrenHorizontal(-dx);
        Log.e("xiaojun", "当前位移距离:" + mTotalOffsetX);
        return dx;
    }

    /**
     * 缓存view
     *
     * @param recycler
     */
    private void recyclerViews(RecyclerView.Recycler recycler, int direction, int positionFirst, int positionLast) {
        Rect rectRecyclerView = getScreenRectWithTotalOffsetX();
        Log.e("xiaojun", "RecyclerView的rect.left=" + rectRecyclerView.left + ",mTotalOffsetX=" + mTotalOffsetX);
        //←  dx > 0
        if (direction == DIRECTION_RIGHT_TO_LEFT) {
            for (int i = 0; i < getChildCount(); i++) {
                View child = getChildAt(i);
                Rect rect = mItems.get(getPosition(child));
                if (rect.right <= rectRecyclerView.left) {
                    removeAndRecycleView(child, recycler);
                } else {
                    layoutDecorated(child, rect.left - mTotalOffsetX, rect.top, rect.right - mTotalOffsetX, rect.bottom);
                }
            }
            /*for (int i = positionFirst; i < getChildCount(); i++) {
                Log.e("xiaojun", "getchildCount=" + getChildCount());
                Rect rect = mItems.get(i);
                View child = recycler.getViewForPosition(i);
                if (rect.right <= rectRecyclerView.left) {
                    removeAndRecycleView(child, recycler);
                } else {
                    layoutDecorated(child, rect.left - mTotalOffsetX, rect.top, rect.right - mTotalOffsetX, rect.bottom);
                    requestLayout();
                }
            }*/
        } else if (direction == DIRECTION_LEFT_TO_RIGHT) {
            for (int i = 0; i < getChildCount(); i++) {
                View child = getChildAt(i);
                Rect rect = mItems.get(getPosition(child));
                if (rect.left >= rectRecyclerView.right) {
                    removeAndRecycleView(child, recycler);
                } else {
                    layoutDecorated(child, rect.left - mTotalOffsetX, rect.top, rect.right - mTotalOffsetX, rect.bottom);
                }
            }
            /*for (int i = positionLast; i >= 0; i--) {
                Rect rect = mItems.get(i);
                View child = recycler.getViewForPosition(i);
                if (rect.left >= rectRecyclerView.right) {
                    removeAndRecycleView(child, recycler);
                } else {
                    layoutDecorated(child, rect.left - mTotalOffsetX, rect.top, rect.right - mTotalOffsetX, rect.bottom);
                }
            }*/
        }
    }

    private void addItem(RecyclerView.Recycler recycler, int direction, int positionFirst, int positionLast) {
        Rect rectRecyclerView = getScreenRectWithTotalOffsetX();
        //←
        if (direction == DIRECTION_RIGHT_TO_LEFT) {
            for (int i = positionLast + 1; i < getItemCount(); i++) {
                Rect rect = mItems.get(i);
                if (Rect.intersects(rectRecyclerView, rect)) {
                    View child = recycler.getViewForPosition(i);
                    addView(child);
                    measureChild(child, 0, 0);
                    layoutDecorated(child, rect.left - mTotalOffsetX, rect.top, rect.right - mTotalOffsetX, rect.bottom);
                }
            }
        }
        //→
        else if (direction == DIRECTION_LEFT_TO_RIGHT) {
            for (int i = positionFirst - 1; i >= 0; i--) {
                Rect rect = mItems.get(i);
                if (Rect.intersects(rectRecyclerView, rect)) {
                    View child = recycler.getViewForPosition(i);
                    addView(child, 0);
                    measureChild(child, 0, 0);
                    layoutDecorated(child, rect.left - mTotalOffsetX, rect.top, rect.right - mTotalOffsetX, rect.bottom);
                }
            }
        }
    }

}
