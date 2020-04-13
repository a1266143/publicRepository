package com.example.testproject.recyclerview;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.graphics.Rect;
import android.util.Log;
import android.util.SparseArray;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.LinearInterpolator;

import androidx.recyclerview.widget.RecyclerView;


/**
 * 自定义Layoutmanager
 * 目标：
 * <p>
 * 1.全新处理OnLayoutChildren，以前只处理了初始化的onLayoutChildren，主要包括如下方面
 * (1)处理初始化OnLayoutChildren
 * (2)处理NotifyDataSetChange导致的onLayoutChildren被调用
 * (3)处理setAdapter(newAdapter)导致的onLayoutChildren被调用
 * <p>
 * 2.处理未调用fling的时候滑动到正确位置
 * <p>
 * 3.处理点击某个Item自动滑动到那个Item
 * <p>
 * created by xiaojun at 2020/4/11
 */
public class CustomLayoutManagerNew extends RecyclerView.LayoutManager {

    private SparseArray<Rect> mRects = new SparseArray<>();
    private int mTotalWidth;
    //当前在屏幕上的第一个View的位置
    private int mPositionFirstChild;
    //当前总共移动的offsetX
    private int mTotalOffsetX;
    //屏幕上总共可以布局多少View
    private int mItemsMaxNumOfScreen;

    private RecyclerView.Recycler mRecycler;

    @Override
    public void onLayoutChildren(RecyclerView.Recycler recycler, RecyclerView.State state) {
        //初始化状态
        initLayoutChildren(recycler, state);
        layoutChildren(recycler);
        mRecycler = recycler;
    }

    @Override
    public void onScrollStateChanged(int state) {
        super.onScrollStateChanged(state);
        if (state == RecyclerView.SCROLL_STATE_IDLE) {
            Log.e("xiaojun", "mPositionFirst=" + mPositionFirstChild);
        }
    }

    public void smoothScrollToPosition2(int position){
        if (position >= getItemCount() || position < 0)
            return;
        Rect rect = mRects.get(position);
        int direction = -1;
        //应该从左向右滑动
        if (mTotalOffsetX > rect.left){
            direction = DIRECTION_FROM_LEFT_TO_RIGHT;
        }
        //应该从右向左滑动
        else if (mTotalOffsetX < rect.left){
            direction = DIRECTION_FROM_RIGHT_TO_LEFT;
        }

        ValueAnimator animator = ValueAnimator.ofInt(mTotalOffsetX,rect.left);
        animator.setInterpolator(new LinearInterpolator());
        animator.setDuration(300);
        int finalDirection = direction;
        animator.addUpdateListener(animation -> {
            mTotalOffsetX  = (int) animation.getAnimatedValue();
            Log.e("xiaojun","mTotalOffsetX="+mTotalOffsetX);
            scroll2(finalDirection,mRecycler);
        });
        animator.start();
    }

    public void smoothScrollToPosition(int position) {
        if (position >= getItemCount() || position < 0)
            return;
        Rect rect = mRects.get(position);
        Rect rectCurrentFirst = mRects.get(mPositionFirstChild);
        //计算出当前的偏移量和 需要滑动到的rect的left的偏移量
        int offsetX = rect.left - mTotalOffsetX;
        int currentOffsetX = mTotalOffsetX;
        ValueAnimator animator = ValueAnimator.ofInt(0, offsetX);
        animator.setDuration(300);
        animator.setInterpolator(new LinearInterpolator());
        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                int offset = (int) animation.getAnimatedValue();
                Log.e("xiaojun", "offset=" + offset);
                scrollHorizontallyBy(offset, mRecycler, null);
//                mTotalOffsetX = currentOffsetX;
//                requestLayout();
            }
        });
        animator.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
//                mTotalOffsetX += offsetX;
            }
        });
        animator.start();
    }

    @Override
    public void smoothScrollToPosition(RecyclerView recyclerView, RecyclerView.State state, int position) {
        super.smoothScrollToPosition(recyclerView, state, position);
        smoothScrollToPosition2(position);
    }

    /**
     * 布局子View
     */
    private void layoutChildren(RecyclerView.Recycler recycler) {
        int numOfLayoutChildren = Math.min(getItemCount() - mPositionFirstChild, mItemsMaxNumOfScreen);
        for (int i = mPositionFirstChild, j = 0; i < mRects.size() && j < numOfLayoutChildren; i++, j++) {
            Rect rect = mRects.get(i);
            View view = recycler.getViewForPosition(i);
            addView(view);
            measureChildWithMargins(view, 0, 0);
            layoutDecorated(view, rect.left, rect.top, rect.right, rect.bottom);
        }
    }

    /**
     * 刚设置children的状态
     *
     * @param recycler 缓存器
     * @param state    当前RecyclerView状态
     */
    private void initLayoutChildren(RecyclerView.Recycler recycler, RecyclerView.State state) {
        //缓存到缓存器中
        detachAndScrapAttachedViews(recycler);
        int itemCount = getItemCount();
        View childRandom = recycler.getViewForPosition(0);
        measureChildWithMargins(childRandom, 0, 0);
        int width = getDecoratedMeasuredWidth(childRandom);
        int height = getDecoratedMeasuredHeight(childRandom);
        int offsetX = 0;
        //计算所有child的总宽度
        for (int i = 0; i < itemCount; i++) {
            //保存每个item的位置
            Rect rect = new Rect(offsetX, 0, width + offsetX, height);
            offsetX += width;
            mRects.put(i, rect);
        }
        mTotalWidth = Math.max(getWidth(), offsetX);

        mItemsMaxNumOfScreen = (int) Math.ceil(1. * getWidth() / width);
        Log.e("xiaojun", "屏幕上最多可以容纳:" + mItemsMaxNumOfScreen + "个Item");
    }


    @Override
    public int scrollHorizontallyBy(int dx, RecyclerView.Recycler recycler, RecyclerView.State state) {
        super.scrollHorizontallyBy(dx,recycler,state);
        //拦截左右边界
        int realDx = interceptEdge(dx);
        if (realDx == 0)
            return 0;
        mTotalOffsetX += realDx;
        //回收布局
        recyclerLayout(realDx < 0 ? DIRECTION_FROM_LEFT_TO_RIGHT : DIRECTION_FROM_RIGHT_TO_LEFT, recycler);

        //重新布局哪些应该被显示在界面上
//        layoutChildOnScreen(realDx, recycler, state);
        //重新布局
        layoutChildOnScreenWithDetach(realDx < 0 ? DIRECTION_FROM_LEFT_TO_RIGHT : DIRECTION_FROM_RIGHT_TO_LEFT, recycler);
//        offsetChildrenHorizontal(-realDx);
        return realDx;
    }

    private final int DIRECTION_FROM_LEFT_TO_RIGHT = 0;
    private final int DIRECTION_FROM_RIGHT_TO_LEFT = 1;

    /**
     * 布局所有子View和回收相应子View
     * 根据mTotalOffsetX来判断当前
     */
    private void layoutChildrenOnScreenAndRecycle(RecyclerView.Recycler recycler, int direction) {
        Rect rectRecyclerView = getRecyclerViewRectNoOffset();
        //在屏幕上的第一个Item的真实位置
        int position = -1;
        //回收所有子View
        for (int i = getChildCount() - 1; i >= 0; i--) {
            View child = getChildAt(i);
            position = getPosition(child);
            Rect rect = mRects.get(position);
            if (!Rect.intersects(rect, rectRecyclerView))
                removeAndRecycleView(child, recycler);
        }
        if (position != -1)
            layoutAllItems(recycler, direction, rectRecyclerView, position);
    }

    /**
     * 布局所有Item
     *
     * @param recycler
     * @param direction
     * @param rectRecyclerView
     * @param position
     */
    private void layoutAllItems(RecyclerView.Recycler recycler, int direction, Rect rectRecyclerView, int position) {

    }

    //布局单个Item
    private void layoutSingleItem(RecyclerView.Recycler recycler, int direction) {

    }

    private void scroll2(int direction, RecyclerView.Recycler recycler){
        recyclerLayout(direction,recycler);
        layoutChildOnScreenWithDetach(direction,recycler);
    }

    /**
     * 重新布局应该被显示在界面上的View
     *
     * @param dx
     * @param recycler
     * @param state
     */
    private void layoutChildOnScreen(int dx, RecyclerView.Recycler recycler, RecyclerView.State state) {
        Rect rectRecyclerView = getRecyclerViewRect(dx);
        //←
        //从界面上的最后一个View的下一个开始，一直到最后一个Item，判断是否需要添加到界面上
        if (dx > 0) {
            View childLast = getChildAt(getChildCount() - 1);
            int realPositionLastChild = getPosition(childLast);
            for (int i = realPositionLastChild + 1; i < getItemCount(); i++) {
                Rect rect = mRects.get(i);
                if (Rect.intersects(rect, rectRecyclerView)) {
                    View child = recycler.getViewForPosition(i);
                    addView(child);
                    measureChildWithMargins(child, 0, 0);
                    layoutDecoratedWithMargins(child, rect.left - mTotalOffsetX, rect.top, rect.right - mTotalOffsetX, rect.bottom);
                }
            }
        }
        //→
        //从第0个开始到当前屏幕最后一个Item之间都应该判断是否需要显示
        else if (dx < 0) {
            View childFirst = getChildAt(0);
            int maxPosition = getPosition(childFirst) - 1;
            for (int i = maxPosition; i >= 0; i--) {
                Rect rect = mRects.get(i);
                if (Rect.intersects(rect, rectRecyclerView)) {
                    View child = recycler.getViewForPosition(i);
                    addView(child, 0);
                    measureChildWithMargins(child, 0, 0);
                    layoutDecoratedWithMargins(child, rect.left - mTotalOffsetX, rect.top, rect.right - mTotalOffsetX, rect.bottom);
                }
            }
        }
    }

    private void layoutChildOnScreenWithDetach(int direction, RecyclerView.Recycler recycler) {
        View childFirst = getChildAt(0);
        View childLast = getChildAt(getChildCount() - 1);
        //将屏幕上的View离屏，下面重新绘制所有Child
        detachAndScrapAttachedViews(recycler);
        Rect rectRecyclerView = getRecyclerViewRectNoOffset();
        if (direction == DIRECTION_FROM_RIGHT_TO_LEFT) {
            int realPositionFirst = getPosition(childFirst);
            for (int i = realPositionFirst; i < getItemCount(); i++) {
                Rect rect = mRects.get(i);
                if (Rect.intersects(rect, rectRecyclerView)) {
                    View view = recycler.getViewForPosition(i);
                    addView(view);
                    measureChildWithMargins(view, 0, 0);
                    layoutDecoratedWithMargins(view, rect.left - mTotalOffsetX, rect.top, rect.right, rect.bottom);
                }
            }
        } else if (direction == DIRECTION_FROM_LEFT_TO_RIGHT) {
            int realPositionLast = getPosition(childLast);
            for (int i = realPositionLast; i >= 0; i--) {
                Rect rect = mRects.get(i);
                if (Rect.intersects(rect, rectRecyclerView)) {
                    View view = recycler.getViewForPosition(i);
                    addView(view, 0);
                    measureChildWithMargins(view, 0, 0);
                    layoutDecoratedWithMargins(view, rect.left - mTotalOffsetX, rect.top, rect.right - mTotalOffsetX, rect.bottom);
                }
            }
        }
        if (getChildAt(0) != null)
            mPositionFirstChild = getPosition(getChildAt(0));
    }

    /**
     * 回收滑动出界面的item
     *
     * @param recycler 缓存器
     *                 只需要判断当前屏幕上显示的所有View是否具备被回收的条件
     */
    private void recyclerLayout(int direction, RecyclerView.Recycler recycler) {
        //→滑动
        Rect rectRecyclerView = getRecyclerViewRectNoOffset();
        for (int i = getChildCount() - 1; i >= 0; i--) {
            View child = getChildAt(i);
            int realPosition = getPosition(child);
            Rect rect = mRects.get(realPosition);
            //dx < 0
            if (direction == DIRECTION_FROM_LEFT_TO_RIGHT) {
                if (rect.left > rectRecyclerView.right)
                    removeAndRecycleView(child, recycler);
            }
            //dx > 0
            else if (direction == DIRECTION_FROM_RIGHT_TO_LEFT) {
                if (rect.right < rectRecyclerView.left)
                    removeAndRecycleView(child, recycler);
            }
        }

        /*if (dx < 0) {
            for (int i = getChildCount() - 1; i >= 0; i--) {
                View child = getChildAt(i);
                int realPosition = getPosition(child);
                Rect rect = mRects.get(realPosition);
                if (rect.left > rectRecyclerView.right)
                    removeAndRecycleView(child, recycler);
            }
        }
        //←滑动
        //从屏幕上第1个到最后一个都应该判断哪些应该被回收
        else if (dx > 0) {
            for (int i = getChildCount() - 1; i >= 0; i--) {
                View child = getChildAt(i);
                int realPosition = getPosition(child);
                Rect rect = mRects.get(realPosition);
                if (rect.right < rectRecyclerView.left)
                    removeAndRecycleView(child, recycler);
            }
        }*/

    }

    /**
     * 拦截左右边界
     *
     * @param dx → 为正
     * @return
     */
    private int interceptEdge(int dx) {
        int realDx = dx;
        if (mTotalOffsetX + dx < 0)
            realDx = -mTotalOffsetX;
        else if (mTotalOffsetX + dx > mTotalWidth - getWidth())
            realDx = mTotalWidth - getWidth() - mTotalOffsetX;
        return realDx;
    }

    /**
     * 获取滑动过后的RecyclerView所在的Rect
     *
     * @param dx
     * @return
     */
    private Rect getRecyclerViewRect(int dx) {
        return new Rect(mTotalOffsetX + dx, 0, getWidth() + mTotalOffsetX + dx, getHeight());
    }

    private Rect getRecyclerViewRectNoOffset() {
        return new Rect(mTotalOffsetX, 0, getWidth() + mTotalOffsetX, getHeight());
    }

    @Override
    public RecyclerView.LayoutParams generateDefaultLayoutParams() {
        return new RecyclerView.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
    }

    @Override
    public boolean canScrollHorizontally() {
        //可以左右滑动
        return true;
    }

}
