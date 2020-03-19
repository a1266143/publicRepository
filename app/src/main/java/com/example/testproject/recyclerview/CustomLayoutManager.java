package com.example.testproject.recyclerview;

import android.view.View;

import androidx.recyclerview.widget.RecyclerView;

/**
 * 自定义LayoutManager
 * created by xiaojun at 2020/3/9
 */
public class CustomLayoutManager extends RecyclerView.LayoutManager {

    private int mItemTotalWidth;//所有子Item的总宽度

    //返回子Item的布局参数
    @Override
    public RecyclerView.LayoutParams generateDefaultLayoutParams() {
        return new RecyclerView.LayoutParams(RecyclerView.LayoutParams.WRAP_CONTENT, RecyclerView.LayoutParams.WRAP_CONTENT);
    }

    //处理子item的布局
    //------------------------------------------------布局子View-------------------------------------------------
    //通过以下步骤摆放View
    //1.addView
    //2.layoutDecorated
    @Override
    public void onLayoutChildren(RecyclerView.Recycler recycler, RecyclerView.State state) {
        int offsetX = 0;
        for (int i = 0; i < getItemCount(); i++) {
            View view = recycler.getViewForPosition(i);//获取子item的View
            addView(view);//添加进此LayoutManager

            //--------------进行子Item的位置摆放----------------
            measureChildWithMargins(view, 0, 0);
            int width = getDecoratedMeasuredWidth(view);//获取item+decoration的总宽度，包含装饰decoration的宽度，如果只想测量得到View的测量宽度，请使用getMeasuredWidth();
            int height = getDecoratedMeasuredHeight(view);//获取item+decoration的总高度
            layoutDecorated(view, offsetX, 0, offsetX + width, height);//开始进行item的摆放
            offsetX += width;

            //计算所有子Item的总宽度
            //如果子View未填充满RecyclerView，总宽度就是RecyclerView的宽度
            mItemTotalWidth = Math.max(offsetX, getHorizontalSpace());
        }
    }

    //获取RecyclerView的宽度
    private int getHorizontalSpace() {
        return getWidth() - getPaddingLeft() - getPaddingRight();
    }

    //----------------------添加滑动效果(水平滑动)---------------------------
    @Override
    public boolean canScrollHorizontally() {
        return true;
    }

    int mTotalX;//水平总共移动的距离

    @Override
    public int scrollHorizontallyBy(int dx, RecyclerView.Recycler recycler, RecyclerView.State state) {
        int travel = dx;
        //如果滑动到最左边
        if (mTotalX + dx < 0)
            travel = -mTotalX;
            //如果滑动到最右边
        else if (mTotalX + dx > mItemTotalWidth - getHorizontalSpace())
            travel = mItemTotalWidth - getHorizontalSpace() - mTotalX;
        mTotalX += travel;
        //平移容器内的item
        offsetChildrenHorizontal(-travel);
        return travel;
    }
}
