package com.example.testproject.recyclerview;

import android.graphics.Rect;
import android.util.Log;
import android.util.SparseArray;
import android.view.View;
import android.view.ViewGroup;

import androidx.recyclerview.widget.RecyclerView;

/**
 * 自定义LayoutManager第三次尝试
 * 功能:
 * 1.实现垂直滑动(已实现)
 * 2.实现上边界限制滑动(已实现)
 * 3.实现下边界限制滑动(已实现)
 * 4.实现View的回收
 * 5.实现View的复用
 * created by xiaojun at 2020/3/19
 */
public class CustomLayoutManagerThird extends RecyclerView.LayoutManager {

    private int mTotalHeight;//子View总共的高度
    private int mTotalMoveY;//手指总共移动的距离
    private Rect mRectRecyclerView = new Rect();//RecyclerView所在的Rect
    private SparseArray<Rect> mItemRects = new SparseArray<>();//保存每个Item的位置信息

    @Override
    public RecyclerView.LayoutParams generateDefaultLayoutParams() {
        return new RecyclerView.LayoutParams(RecyclerView.LayoutParams.WRAP_CONTENT, RecyclerView.LayoutParams.WRAP_CONTENT);
    }

    //布局子View
    @Override
    public void onLayoutChildren(RecyclerView.Recycler recycler, RecyclerView.State state) {
        Log.e("xiaojun","onLayoutChildren");
        int offsetY = 0;
        mTotalHeight = 0;
        if (getItemCount() == 0)
            return;
        //将所有HolderView从屏幕上剥离
        detachAndScrapAttachedViews(recycler);
        //初始化一屏数据(每个Item数据的高度必须一样，这样才能算出一屏需要初始化多少个数据)
        View view = recycler.getViewForPosition(0);//从缓存中拿出一个数据
        measureChildWithMargins(view, 0, 0);
        int width = getDecoratedMeasuredWidth(view);
        int height = getDecoratedMeasuredHeight(view);
        int visibleCount = getHeight() / height;//一屏中可容纳的Item数量
        //保存每个item的位置
        for (int i = 0; i < getItemCount(); i++) {
            Rect rect = new Rect(0, offsetY, width, offsetY + height);
            mItemRects.put(i, rect);
            offsetY += height;
            mTotalHeight += height;
        }
        //开始布局子View
        for (int i = 0; i < visibleCount; i++) {
            Rect rect = mItemRects.get(i);
            View child = recycler.getViewForPosition(i);
            addView(child);
            measureChildWithMargins(child, 0, 0);
            layoutDecorated(child, rect.left, rect.top, rect.right, rect.bottom);
        }
    }

    //垂直滑动
    //dy>0表示 ↑ 滑动
    //dy<0表示 ↓ 滑动
    @Override
    public int scrollVerticallyBy(int dy, RecyclerView.Recycler recycler, RecyclerView.State state) {
        int realDy = dy;
        Log.e("xiaojun", "mTotalMoveY=" + mTotalMoveY + ",mTotalHeight=" + mTotalHeight + ",getHeight=" + getHeight());
        //判断到顶(dy<0)
        if (mTotalMoveY + dy < 0) {
            realDy = -mTotalMoveY;
        }
        //滑动到底判断(dy>0)
        else if (mTotalMoveY + dy > (mTotalHeight - getHeight())) {
            realDy = mTotalHeight - getHeight() - mTotalMoveY;
        }

        recyclerMethod(realDy, recycler);

        reuse(realDy,recycler);

        offsetChildrenVertical(-realDy);//垂直平移容器内的子Item
        mTotalMoveY += realDy;
        return realDy;
    }

    /**
     * 复用
     */
    private void reuse(int dy, RecyclerView.Recycler recycler) {
        View lastView = getChildAt(getChildCount() - 1);
        int positionLastViewNext = getPosition(lastView) + 1;
        if (positionLastViewNext <= getItemCount() - 1) {
            Rect rect = mItemRects.get(positionLastViewNext);//拿出对应的View rect
            if (rect.intersect(getScreenRect(dy))) {
                View child = recycler.getViewForPosition(positionLastViewNext);
                addView(child);
                measureChildWithMargins(child, 0, 0);
                layoutDecorated(child, rect.left, rect.top - mTotalMoveY, rect.right, rect.bottom - mTotalMoveY);
            }
        }
    }

    /**
     * 回收
     */
    private void recyclerMethod(int dy, RecyclerView.Recycler recycler) {
        for (int i = 0; i < getChildCount() - 1; i--) {
            View child = getChildAt(i);
            if (child == null)
                continue;
            //从下往上滑动
            if (dy > 0) {
                //如果已经划出屏幕外面，就回收
                if (getDecoratedBottom(child) - dy < 0) {
                    removeAndRecycleView(child, recycler);
                }
            }
        }
    }

    /**
     * 获取RecyclerView所在的Rect
     *
     * @param dy 移动多少像素
     * @return
     */
    private Rect getScreenRect(int dy) {
        mRectRecyclerView.left = 0;
        mRectRecyclerView.top = mTotalMoveY + dy;
        mRectRecyclerView.right = getWidth();
        mRectRecyclerView.bottom = getHeight() + mTotalMoveY + dy;
        return mRectRecyclerView;
    }

    @Override
    public boolean canScrollVertically() {
        return true;
    }
}
