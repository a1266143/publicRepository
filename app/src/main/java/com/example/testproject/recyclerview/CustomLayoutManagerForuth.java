package com.example.testproject.recyclerview;

import android.graphics.Rect;
import android.util.Log;
import android.util.SparseArray;
import android.view.View;
import android.view.ViewGroup;

import androidx.recyclerview.widget.RecyclerView;

/**
 * LayoutManager第四次尝试
 * 功能:
 * 1.实现左右滑动
 * 2.实现左右滑动边界拦截
 * 3.实现回收
 * 4.实现复用
 * created by xiaojun at 2020/3/20
 */
public class CustomLayoutManagerForuth extends RecyclerView.LayoutManager {

    private int mTotalWidth;//子Item总共宽度
    private int mOffsetX;//移动的距离
    private SparseArray<Rect> mItemRects = new SparseArray<>();

    @Override
    public RecyclerView.LayoutParams generateDefaultLayoutParams() {
        return new RecyclerView.LayoutParams(RecyclerView.LayoutParams.WRAP_CONTENT, RecyclerView.LayoutParams.WRAP_CONTENT);
    }

    @Override
    public void onLayoutChildren(RecyclerView.Recycler recycler, RecyclerView.State state) {
        if (getItemCount() == 0){
            detachAndScrapAttachedViews(recycler);
            return;
        }
        detachAndScrapAttachedViews(recycler);//将所有View分离屏幕

        View child = recycler.getViewForPosition(0);
        measureChildWithMargins(child, 0, 0);
        int width = getDecoratedMeasuredWidth(child);
        int height = getDecoratedMeasuredHeight(child);
//        int visibleCount = (int) Math.ceil(1.0 * getWidth() / width);
        int visibleCount = getItemCount();


        //存储每个Item的具体位置
        int offsetX = 0;
        for (int i = 0; i < getItemCount(); i++) {
            Rect rect = new Rect(offsetX, 0, offsetX + width, height);
            mItemRects.put(i, rect);
            offsetX += width;
        }

        //布局每个子View
        for (int i = 0; i < visibleCount; i++) {
            Rect rect = mItemRects.get(i);
            View view = recycler.getViewForPosition(i);
            addView(view);
            measureChildWithMargins(view, 0, 0);//先measure再layout
            layoutDecorated(view, rect.left, rect.top, rect.right, rect.bottom);
        }

        mTotalWidth = Math.max(offsetX, getWidth());
    }

    //向 ← 滑动:dx>0
    //向 → 滑动:dx<0
    @Override
    public int scrollHorizontallyBy(int dx, RecyclerView.Recycler recycler, RecyclerView.State state) {

        //处理边界拦截
        int realdx = dealWithEdge(dx);

        for (int i = 0; i <= getChildCount()-1; i++) {
            //回收Item
            recyclerItem(i, realdx, recycler);
        }
//        addItem(realdx,recycler);
        offsetChildrenHorizontal(-realdx);//滑动
        mOffsetX += realdx;
        return realdx;
    }

    private int dealWithEdge(int dx) {
        int realDx = dx;
        //←
        if (dx > 0) {
            if (mOffsetX + dx > mTotalWidth - getWidth())
                realDx = mTotalWidth - getWidth() - mOffsetX;
        }
        //→
        else if (dx < 0) {
            if (mOffsetX + dx < 0)
                realDx = -mOffsetX;
        }
        return realDx;
    }

    private void recyclerItem(int position, int dx, RecyclerView.Recycler recycler) {
        View child = getChildAt(position);
        //← 回收左边的Item
        if (dx > 0) {
            /*if (getDecoratedRight(child) - dx <= 0) {
                removeAndRecycleView(child, recycler);
            }*/
        }
        //→ 回收右边的Item
        else if (dx < 0) {
            if (getDecoratedLeft(child) - dx > getWidth()) {
                removeAndRecycleView(child, recycler);
            }
        }
    }

    private void addItem( int dx, RecyclerView.Recycler recycler) {
        Log.e("xiaojun","getChildCount()="+getChildCount());
        //← 添加右边的Item
        if (dx >= 0) {
            View viewLast = getChildAt(getChildCount()-1);
            int realPosition =getPosition(viewLast)+1;
            int allNum = getItemCount();
            for (int i = realPosition; i <= allNum-1; i++) {
                Rect rect = mItemRects.get(i);
                if (rect.left <= mOffsetX + getWidth()){
                    View view = recycler.getViewForPosition(i);
                    addView(view);
                    measureChildWithMargins(view,0,0);
                    layoutDecoratedWithMargins(view,rect.left-mOffsetX,rect.top,rect.right-mOffsetX,rect.bottom);
                }
            }
        }
        //→ 添加左边的Item
        /*else{
            for (int i = (getPosition(getChildAt(0))-1); i >= 0 ; i--) {
                Rect rect = mItemRects.get(i);
                if (rect.right >= mOffsetX){
                    View view = recycler.getViewForPosition(i);
                    addView(view,0);
                    measureChildWithMargins(view,0,0);
                    layoutDecoratedWithMargins(view,rect.left-mOffsetX,rect.top,rect.right-mOffsetX,rect.bottom);
                }
            }
        }*/
    }

    @Override
    public boolean canScrollHorizontally() {
        return true;
    }
}
