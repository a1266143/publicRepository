package com.example.testproject.recyclerview;

import android.graphics.Rect;
import android.util.Log;
import android.util.SparseArray;
import android.view.View;

import androidx.recyclerview.widget.RecyclerView;

public class CustomLayoutManagerFifth extends RecyclerView.LayoutManager {

    private int mOffsetY;
    private int mTotalHeight;
    private Rect mRectRecyclerView = new Rect();
    private SparseArray<Rect> mItemRects = new SparseArray<>();

    @Override
    public RecyclerView.LayoutParams generateDefaultLayoutParams() {
        return new RecyclerView.LayoutParams(RecyclerView.LayoutParams.WRAP_CONTENT, RecyclerView.LayoutParams.WRAP_CONTENT);
    }

    @Override
    public boolean canScrollVertically() {
        return true;
    }


    //初始化布局
    @Override
    public void onLayoutChildren(RecyclerView.Recycler recycler, RecyclerView.State state) {
        if (getItemCount() == 0) {//没有Item，界面空着吧
            detachAndScrapAttachedViews(recycler);
            return;
        }
        detachAndScrapAttachedViews(recycler);


        View randomView = recycler.getViewForPosition(0);
        measureChildWithMargins(randomView, 0, 0);
        int width = getDecoratedMeasuredWidth(randomView);
        int height = getDecoratedMeasuredHeight(randomView);
        int visibleCount = (int) Math.ceil(1.0 * getHeight() / height);

        int offsetY = 0;

        //存储每个Item的位置
        for (int i = 0; i < getItemCount(); i++) {
            Rect rect = new Rect(0, offsetY, width, offsetY + height);
            mItemRects.put(i, rect);
            offsetY += height;
        }


        //将初始化显示的Item显示出来
        for (int i = 0; i < visibleCount; i++) {
            Rect rect = mItemRects.get(i);
            View childNeedVisible = recycler.getViewForPosition(i);
            addView(childNeedVisible);
            measureChildWithMargins(childNeedVisible, 0, 0);
            layoutDecoratedWithMargins(childNeedVisible, rect.left, rect.top, rect.right, rect.bottom);
        }


        mTotalHeight = offsetY;
    }

    //↑ dy > 0
    //↓ dy < 0
    @Override
    public int scrollVerticallyBy(int dy, RecyclerView.Recycler recycler, RecyclerView.State state) {
        if (getChildCount() <= 0)
            return dy;

        //拦截边界,返回真正需要滑动的距离
        int offsetY = interceptEdge(dy);
        //回收
        recyclerItem(offsetY, recycler);
        //复用
        reuse(offsetY, recycler, getRecyclerViewRect(offsetY));
        //滑动
        mOffsetY += offsetY;
        offsetChildrenVertical(-offsetY);
        return offsetY;
    }

    //获取RecyclerView所在的Rect,注意，是假设移动dy过后的Rect
    private Rect getRecyclerViewRect(int dy) {
        mRectRecyclerView.left = 0;
        mRectRecyclerView.top = mOffsetY + dy;
        mRectRecyclerView.right = getWidth();
        mRectRecyclerView.bottom = getHeight() + mOffsetY + dy;
        return mRectRecyclerView;
    }

    private void reuse(int dy, RecyclerView.Recycler recycler, Rect recyclerviewRect) {
        // ↑ 下边增加Item
        if (dy >= 0) {
            View viewLast = getChildAt(getChildCount() - 1);
            int viewRealNeededPosition = getPosition(viewLast) + 1;
            for (int i = viewRealNeededPosition; i < getItemCount(); i++) {
                Rect rect = mItemRects.get(i);
                if (Rect.intersects(recyclerviewRect, rect)) {//如果和RecyclerViewRect相交，就添加到界面上
                    View view = recycler.getViewForPosition(i);
                    addView(view);
                    measureChildWithMargins(view, 0, 0);
                    layoutDecorated(view, rect.left, rect.top - mOffsetY, rect.right, rect.bottom - mOffsetY);
                } else {
                    break;
                }
            }
        }
        // ↓ 上边增加Item
        else {
            View viewFirst = getChildAt(0);
            int viewNeedPosition = getPosition(viewFirst) - 1;
            for (int i = viewNeedPosition; i >= 0; i--) {
                Rect rect = mItemRects.get(i);
                if (Rect.intersects(recyclerviewRect, rect)) {
                    View view = recycler.getViewForPosition(i);
                    addView(view, 0);
                    measureChildWithMargins(view, 0, 0);
                    layoutDecoratedWithMargins(view, rect.left, rect.top - mOffsetY, rect.right, rect.bottom - mOffsetY);
                } else {
                    break;
                }
            }
        }
    }

    private void recyclerItem(int dy, RecyclerView.Recycler recycler) {
        //!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!请特别注意下面这种写法!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
        //TODO THIS FUCKER
        //for(int i=0;i<getChildCount();i++)
        //因为下面的removeAndRecyclerView的操作会导致getChildCount()方法的数量减少
        //所以在极快速滑动的时候会导致某个View不能被remove掉
        //!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
        //判断界面上的所有View是否需要回收
        for (int i = getChildCount() - 1; i >= 0; i--) {
            View childOnScreen = getChildAt(i);
            // ↑ 移除上面的View
            if (dy > 0) {
                if (getDecoratedBottom(childOnScreen) - dy < 0)
                    removeAndRecycleView(childOnScreen, recycler);
            }
            // ↓ 移除下面的View
            else if (dy < 0) {
                if (getDecoratedTop(childOnScreen) - dy > getHeight())
                    removeAndRecycleView(childOnScreen, recycler);
            }
        }
    }

    private int interceptEdge(int dy) {
        int offsetY = dy;
        //如果滑动到最顶部
        if (mOffsetY + dy < 0) {
            offsetY = -mOffsetY;
        } else if (mOffsetY + dy > mTotalHeight - getHeight()) {
            //如果滑动到最底部
            offsetY = mTotalHeight - getHeight() - mOffsetY;
        }
        return offsetY;
    }
}
