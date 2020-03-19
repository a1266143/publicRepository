package com.example.testproject.recyclerview;

import android.graphics.Rect;
import android.util.Log;
import android.util.SparseArray;
import android.view.View;

import androidx.recyclerview.widget.RecyclerView;

/**
 * 实现自定义LayoutManager
 * 目标:
 * 0.实现滑动到最左侧就停止，最右侧也停止   （已完成）
 * 1.实现View的回收和复用 （正在进行中...）
 * 2.实现初始状态第一个View显示在RecyclerView的中间
 * 3.手指滑动离开后移动到某个Item的中间
 * 相关函数说明:
 * recycler.getViewForPosition:从缓存中拿出HolderView
 * getChildCount:获取屏幕上显示的View的数量
 * getChildAt(int ):获取屏幕上对应的View
 * getItemCount:获取Adapter中的所有View的数量
 * childView.getDecoratedRight:获取 以当前RecyclerView左上角(0,0)为原点，到childView所在Rect的rect.right的距离
 *
 * created by xiaojun at 2020/3/16
 */
public class CustomLayoutManagerAgain extends RecyclerView.LayoutManager {

    @Override
    public RecyclerView.LayoutParams generateDefaultLayoutParams() {
        //让ItemView自己进行布局参数的大小设定
        return new RecyclerView.LayoutParams(RecyclerView.LayoutParams.WRAP_CONTENT, RecyclerView.LayoutParams.WRAP_CONTENT);
    }

    //可以横向滑动
    @Override
    public boolean canScrollHorizontally() {
        return true;
    }

    private int mSumDx;//总共滑动的X轴距离

    //横向滑动
    //滑动时，需要处理以下情况:
    //1.将滑动到屏幕外面的View放到缓存中
    //2.将新来的View添加到RecyclerView的空白区域
    //向→滑动dx为负 ，向←滑动dx为正
    //相关函数说明:

    @Override
    public int scrollHorizontallyBy(int dx, RecyclerView.Recycler recycler, RecyclerView.State state) {
        Log.e("xioajun", "dx=" + dx);
        //判断是否还能往→滑动
        if (dx + mSumDx < 0)
            dx = -mSumDx;
        //判断是否还能往←滑动
        //判断依据:滑动总长度是否 大于 (总Item长度-recyclerView一屏的长度)
        //滑动到最右边，就不能继续往右滑了
        if (dx + mSumDx > mTotalWidth - getRecyclerViewWidth())
            dx = mTotalWidth - getRecyclerViewWidth() - mSumDx;

        //------------测试-----------------
        //获取屏幕上第一个View所在的真实位置
        int position = getPosition(getChildAt(0));
        Log.e("xioajun","getchildAt(0).getPosition="+position+",getChildCount="+getChildCount());

        //-----------------------------------------回收和复用-------------------------------------------
        //向   ←   滑动
        if (dx > 0) {
            //==========回收和移除左边的View==========
            for (int i = 0; i < getChildCount(); i++) {
                View childView = getChildAt(i);
                //移除缓存相关View,注意：移除View的同时也需要添加View
                if (getDecoratedRight(childView) - dx < 0 ) {//getDecoratedRight表示获取当前子View的右边界，相对于RecyclerView左边框(x坐标为0)的距离
                    //将childView标记为废弃，RecyclerView将会回收并且不显示
                    removeAndRecycleView(childView, recycler);
                }
            }
            //============添加右边新的View===========
            addItemViewWhenScrollRightToLeft(getScreenRect(dx, mSumDx), recycler, mSumDx);
        }
        //向   →   滑动 dx < 0
        else if (dx < 0){
            /*for (int i = 0; i < (getChildCount() - 1); i++) {
                View childView = getChildAt(i);
                int left = getDecoratedLeft(childView);
                if (left-dx>getRecyclerViewWidth()){
                    removeAndRecycleView(childView,recycler);
                }
            }
            int left = getDecoratedLeft(getChildAt((getChildCount()-1)));*/
//            Log.e("xiaojun","最右边的View的DecoratedLeft="+left+",dx="+dx+",recyclerViewWidth="+getRecyclerViewWidth());
            //=======回收和移除右边的View=====
            for (int i = getChildCount()-1; i >= 0; i--) {
                View childView = getChildAt(i);//获取显示在屏幕上的最后一个View
                //如果左边框大于RecyclerView的宽度就回收
                if (getDecoratedLeft(childView)-dx>getRecyclerViewWidth()){
                    Log.e("xiaojun","removeAndRecyclerView");
                    removeAndRecycleView(childView,recycler);//标记为
                }
            }
            //=======添加RecyclerView左边相关View=======
            addItemViewWhenScrollLeftToRight(getScreenRect(dx,mSumDx),recycler,mSumDx);
        }

        //开始滑动
        offsetChildrenHorizontal(-dx);
        //往→移动为负，往←移动为正
        mSumDx += dx;
        Log.e("xiaojun", "dxRepair=" + dx + ",mSumDx=" + mSumDx);
        return dx;
    }

    //从左向右滑动添加左边的View
    private void addItemViewWhenScrollLeftToRight(Rect rectScreen, RecyclerView.Recycler recycler,int scrollX){
        View firstChildViewOnScreen = getChildAt(0);
        int positionFirstChild = getPosition(firstChildViewOnScreen);
        int positionNeedAdd = positionFirstChild - 1;//获取真实的Adapter中的index
        Log.e("xiaojun","positionFirstChild="+positionFirstChild+",positionNeedAdd="+positionNeedAdd);
        int mount = 0;
        for (int i = positionNeedAdd; i >= 0; i--) {
            Rect rect = mItemRects.get(i);
            if (Rect.intersects(rectScreen,rect)){
                mount++;
                Log.e("xiaojun","正在添加View:"+mount);
                View child = recycler.getViewForPosition(i);
                addView(child,0);
                measureChildWithMargins(child,0,0);
                layoutDecorated(child,rect.left-scrollX,rect.top,rect.right-scrollX,rect.bottom);
            }
        }
    }

    /**
     * 当滑动的时候添加Item 从右向左滑动
     * @param rectScreen RecyclerView所在的Rect
     * @param recycler
     * @param scrollX 当前滑动的距离
     */
    private void addItemViewWhenScrollRightToLeft(Rect rectScreen, RecyclerView.Recycler recycler, int scrollX) {
        View lastChildViewOnScreen = getChildAt(getChildCount() - 1);//获取屏幕上最后一个Position
        int positionLastChild = getPosition(lastChildViewOnScreen);//获取在Adapter中的真实Position,用于在mItemRects中拿到Rect信息
        int positionNeedAdd = positionLastChild + 1;
        int count = 0;
        for (int i = positionNeedAdd; i <= getItemCount() - 1; i++) {
            Rect rect = mItemRects.get(i);
            if (Rect.intersects(rectScreen, rect)) {
                count++;
                Log.e("xiaojun","正在添加View:"+count);
                View child = recycler.getViewForPosition(i);
                addView(child);
                measureChildWithMargins(child, 0, 0);
                //layout函数坐标是从RecyclerView(0,0)开始的
                layoutDecorated(child, rect.left - scrollX, rect.top, rect.right - scrollX, rect.bottom);
            }
        }
    }

    private Rect mScreenRect = new Rect();

    private Rect getScreenRect(int dx, int totalMoveX) {
        mScreenRect.left = totalMoveX + dx;
        mScreenRect.top = 0;
        mScreenRect.right = totalMoveX + getRecyclerViewWidth() + dx;
        mScreenRect.bottom = getHeight() - getPaddingTop() - getPaddingBottom();
        return mScreenRect;
    }

    private int mTotalWidth;


    private int mItemWidth, mItemHeight;

    //保存每个Item的位置
    private SparseArray<Rect> mItemRects = new SparseArray<>();

    //布局子View
    @Override
    public void onLayoutChildren(RecyclerView.Recycler recycler, RecyclerView.State state) {
        Log.e("xiaojun", "onLayoutChildren");
        //先将Item从屏幕上剥离，放入缓存(recycler)中
        detachAndScrapAttachedViews(recycler);
        //如果没有item，就返回
        if (getItemCount() == 0)
            return;

        //获取一屏能放下几个Item:RecyclerView宽度 除以 一个Item的宽度
        View childView = recycler.getViewForPosition(0);//获取显示在屏幕上的第一个Item
        measureChildWithMargins(childView, 0, 0);
        mItemWidth = getDecoratedMeasuredWidth(childView);
        mItemHeight = getDecoratedMeasuredHeight(childView);
        int visibleCount = getRecyclerViewWidth() / mItemWidth;//屏幕上显示的item数量

        Log.e("xiaojun", "visibleCount=" + visibleCount + ",getRecyclerViewWidth=" + getRecyclerViewWidth()
                + ",mItemWidth=" + mItemWidth);

        int offsetX = 0;
        //缓存每个Item的位置
        for (int i = 0; i < getItemCount(); i++) {
            Rect rect = new Rect(offsetX, 0, offsetX + mItemWidth, mItemHeight);
            mItemRects.put(i, rect);
            offsetX += mItemWidth;
        }

        //开始布局
        for (int i = 0; i < visibleCount; i++) {
            Rect rect = mItemRects.get(i);
            View view = recycler.getViewForPosition(i);
            addView(view);
            measureChildWithMargins(view, 0, 0);
            layoutDecorated(view, rect.left, rect.top, rect.right, rect.bottom);
        }

        mTotalWidth = Math.max(offsetX, getRecyclerViewWidth());
    }

    //获取recyclerview铺满一屏的宽度
    private int getRecyclerViewWidth() {
        Log.e("xiaojun", "getWidth()=" + getWidth() + ",getPaddingLeft()=" + getPaddingLeft() + ",getPaddingRight()=" + getPaddingRight() + ",mTotalWidth=" + mTotalWidth);
        return getWidth() - getPaddingLeft() - getPaddingRight();
    }

}
