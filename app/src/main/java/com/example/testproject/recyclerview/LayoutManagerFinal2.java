package com.example.testproject.recyclerview;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.graphics.Color;
import android.graphics.Rect;
import android.util.Log;
import android.util.SparseArray;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;

import androidx.annotation.Nullable;
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
    private ValueAnimator mAnimator;
    private RecyclerView.Recycler mRecycler;
    private OnItemSelectedAdapter mListener;
    //当前屏幕的Rect
    private Rect mRectScreen = new Rect(0, 0, getRecyclerViewWidth(), getRecyclerViewHeight());


    //当前开始的位移量
    private int mStartOffsetX;

    private final int DIRECTION_LEFT_TO_RIGHT = 1;
    private final int DIRECTION_RIGHT_TO_LEFT = 2;

    @Override
    public boolean canScrollHorizontally() {
        return true;
    }

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

    @Override
    public void onAdapterChanged(@Nullable RecyclerView.Adapter oldAdapter, @Nullable RecyclerView.Adapter newAdapter) {
        super.onAdapterChanged(oldAdapter, newAdapter);
        //当设置新的Adapter后，可以移除所有View
        //注意:
        //removeAllViews调用之后会回调onLayoutChildren，并且屏幕上会没有View
        // 因为remove了,所以onlayoutchildren的逻辑应该是:
        //执行一个全新的布局操作
        removeAllViews();
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
        if (getItemCount() == 0) {
            detachAndScrapAttachedViews(recycler);
            return;
        }
        //recyclerView结构未改变就不重新布局
        if (!state.didStructureChange())
            return;
//        Log.e("xiaojun", "onLayoutChildren");
        mRecycler = recycler;

        //保存上一次所有items的数量
        int itemsLastCount = mItems.size();

        //---------------------------------------------测量-----------------------------------------------
        //如果这次是第一次初始化，或者NotifyDataSetChange或者setAdapter，需要重新测量所有item，更新总长度mTotalItemWidth,总长度会被用于计算滑动到边界判断
        if (getItemCount() != itemsLastCount) {
            mItems.clear();//清除之前保存的所有item
            View child = recycler.getViewForPosition(0);
            addView(child);
            measureChild(child, 0, 0);
            int width = getDecoratedMeasuredWidth(child);
            int height = getDecoratedMeasuredHeight(child);
            mWidth = width;
            mStartOffsetX = getRecyclerViewWidth() / 2 - mWidth / 2;
            detachAndScrapView(child, recycler);//测量完成之后就将这个View放回缓存器
            int offsetX = 0;
            //保存所有item的位置(所有的子Item的位置都需要加上初始偏移量)
            for (int i = 0; i < getItemCount(); i++) {
                Rect rect = new Rect(offsetX + mStartOffsetX, 0, offsetX + width + mStartOffsetX, height);
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
            int totalItemCount = getItemCount();//当前新的Adapter中的数量
            //如果新的数据集的总数量 > 屏幕能容纳的最大子View数量
            if (totalItemCount >= getScreenViewMaxCount()) {
                if (totalItemCount < itemsLastCount) {
                    //如果之前选中的位置已经超过了 最新 item 数量的最大index，
                    if (mPositionSelectedItem > totalItemCount - 1) {
                        mPositionSelectedItem = totalItemCount - 1;
                        //移动到最右边能移动的最大距离:需要加上初始偏移量
                        mTotalOffsetX = mTotalItemWidth - getRecyclerViewWidth() + 2 * mStartOffsetX;
                    }
                }
                //如果 当前item总数量 和 之前保存的item的总数量相当或者 大于，mPositionSelectedItem和mTotalOffsetX就不用更改
                else {

                }
            }
            //如果新的数据集总量 < 屏幕能容纳的最大子View数量
            else {
                //如果之前选中的位置已经超过了 最新 item 数量的最大index，
                if (mPositionSelectedItem > totalItemCount - 1) {
                    mPositionSelectedItem = totalItemCount - 1;
                    //移动到最右边能移动的最大距离:需要加上初始偏移量
                    mTotalOffsetX = mTotalItemWidth - getRecyclerViewWidth() + 2 * mStartOffsetX;
                }
                /*else{
                    //还原
                    //不能移动，默认选中第一个item
//                    mTotalOffsetX = 0;
//                    mPositionSelectedItem = 0;
                }*/
            }
        }
        //屏幕上没有View，执行崭新的重新布局View
        else {
            mTotalOffsetX = 0;
            mPositionSelectedItem = 0;
        }
        //离屏缓存
        detachAndScrapAttachedViews(recycler);
        layoutChildren(recycler);
        if (mListener != null) {
            mListener.onItemPositionChange(mPositionSelectedItem, STATE.NOTIFY_AND_NEWADAPTER);
            mListener.onItemPositionChangeFinally(mPositionSelectedItem, STATE.NOTIFY_AND_NEWADAPTER);
        }
    }

    //布局应该显示在屏幕上的View，根据当前位移的距离
    private void layoutChildren(RecyclerView.Recycler recycler) {
        Rect rectRecyclerView = getRecyclerViewRect();
        for (int i = 0; i < mItems.size(); i++) {
            Rect rect = mItems.get(i);
            //相交的child才显示出来
            if (Rect.intersects(rectRecyclerView, rect)) {
                View child = recycler.getViewForPosition(i);
                addView(child);
                measureChild(child, 0, 0);
                layoutDecorated(child, rect.left - mTotalOffsetX, rect.top, rect.right - mTotalOffsetX, rect.bottom);
            }
        }
        getCurrentSelectedItemPosition();
    }

    @Override
    public void onScrollStateChanged(int state) {
        super.onScrollStateChanged(state);
        if (RecyclerView.SCROLL_STATE_IDLE == state) {
            //滑动结束后选中的Position:
            if (mListener != null) {
                mListener.onItemPositionChange(mPositionSelectedItem, STATE.SCROLL_IDLE);
                mListener.onItemPositionChangeFinally(mPositionSelectedItem, STATE.SCROLL_IDLE);
            }
        }
    }

    /**
     * 获取移动了{@link #mTotalOffsetX}距离的Rect
     *
     * @return
     */
    private Rect getRecyclerViewRect() {
        mRectScreen.set(mTotalOffsetX, 0, mTotalOffsetX + getRecyclerViewWidth(), getRecyclerViewHeight());
        return mRectScreen;
    }

    public void scrollToPositionCustom(int position) {
        if (mAnimator != null && mAnimator.isRunning())
            mAnimator.cancel();
        Rect rectRecyclerView = getRecyclerViewRect();
        int centerLineX = (rectRecyclerView.right - rectRecyclerView.left) / 2 + rectRecyclerView.left;
        Rect rectSelected = mItems.get(position);
        int centerSeleceted = (rectSelected.right - rectSelected.left) / 2 + rectSelected.left;

        mAnimator = ValueAnimator.ofFloat(mTotalOffsetX, mItems.get(position).left - mStartOffsetX);
        mAnimator.setDuration(500);
        mAnimator.setInterpolator(new AccelerateDecelerateInterpolator());

        int direction = centerSeleceted < centerLineX ? DIRECTION_LEFT_TO_RIGHT : DIRECTION_RIGHT_TO_LEFT;
        mAnimator.addUpdateListener(animation -> {
            float value = (float) animation.getAnimatedValue();
            mTotalOffsetX = (int) value;
            recyclerAndLayoutViews(mRecycler, direction);
            if (mListener != null)
                mListener.onItemPositionChange(mPositionSelectedItem, STATE.MANUAL_CLICK);
        });
        mAnimator.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                if (mListener != null) {
                    mListener.onItemPositionChange(mPositionSelectedItem, STATE.MANUAL_CLICK);
                    mListener.onItemPositionChangeFinally(mPositionSelectedItem, STATE.MANUAL_CLICK);
                }
            }
        });
        mAnimator.start();
    }


    @Override
    public int scrollHorizontallyBy(int dx, RecyclerView.Recycler recycler, RecyclerView.State state) {
        //校正位移距离dx
        int realDx = correctionDx(dx);
        mTotalOffsetX += realDx;
        int direction = realDx > 0 ? DIRECTION_RIGHT_TO_LEFT : DIRECTION_LEFT_TO_RIGHT;
        recyclerAndLayoutViews(recycler, direction);
        return realDx;
    }

    /**
     * 矫正位移的距离
     *
     * @param dx
     */
    private int correctionDx(int dx) {
        int realDx = dx;
        Rect rectRecyclerView = getRecyclerViewRect();
        //←
        if (dx > 0) {
            if (rectRecyclerView.left + dx > mTotalItemWidth + mStartOffsetX * 2 - getRecyclerViewWidth()) {
                realDx = mTotalItemWidth - getRecyclerViewWidth() - rectRecyclerView.left + mStartOffsetX * 2;
            }
        }
        //→
        else if (dx < 0) {
            if (rectRecyclerView.left + dx < 0) {
                realDx = -rectRecyclerView.left;
            }
        }
        return realDx;
    }

    /**
     * 缓存view
     *
     * @param recycler
     */
    private void recyclerAndLayoutViews(RecyclerView.Recycler recycler, int direction) {
        int positionFirst = getPosition(getChildAt(0));
        int positionLast = getPosition(getChildAt(getChildCount() - 1));

        //移动过mTotalOffsetX过后的RecyclerViewRECT
        Rect rectRecyclerView = getRecyclerViewRect();
        //←  dx > 0
        if (direction == DIRECTION_RIGHT_TO_LEFT) {
            for (int i = getChildCount() - 1; i >= 0; i--) {
                View child = getChildAt(i);
                Rect rect = mItems.get(getPosition(child));
                if (!Rect.intersects(rect, rectRecyclerView)) {
//                    Log.e("xiaojun", "xiaojun111:removeChildPosition=" + getPosition(child));
                    removeAndRecycleView(child, recycler);
                } else {
                    layoutDecorated(child, rect.left - mTotalOffsetX, rect.top, rect.right - mTotalOffsetX, rect.bottom);
                }
            }
        } else if (direction == DIRECTION_LEFT_TO_RIGHT) {
            for (int i = getChildCount() - 1; i >= 0; i--) {
                View child = getChildAt(i);
                Rect rect = mItems.get(getPosition(child));
                //如果移动过后的RecyclerView Rect不相交，就移除当前child
                if (!Rect.intersects(rectRecyclerView, rect)) {
//                    Log.e("xiaojun", "xiaojun111:removeChildPosition=" + getPosition(child));
                    removeAndRecycleView(child, recycler);
                } else {
                    //移动子View
                    layoutDecorated(child, rect.left - mTotalOffsetX, rect.top, rect.right - mTotalOffsetX, rect.bottom);
                }
            }
        }

        addItem(recycler, direction, positionFirst, positionLast);
    }

    private void addItem(RecyclerView.Recycler recycler, int direction, int positionFirst, int positionLast) {
//        detachAndScrapAttachedViews(recycler);
        //移动过后的RecyclerViewRect
        Rect rectRecyclerView = getRecyclerViewRect();
        //←
        if (direction == DIRECTION_RIGHT_TO_LEFT) {
            //从第一个到最后一个重新布局
            /*for (int i = positionFirst; i < getItemCount(); i++) {
                Rect rect = mItems.get(i);
                if (Rect.intersects(rectRecyclerView,rect)){
                    View child = recycler.getViewForPosition(i);
                    measureChild(child, 0, 0);
                    addView(child);
                    layoutDecorated(child, rect.left - mTotalOffsetX, rect.top, rect.right - mTotalOffsetX, rect.bottom);
                }
            }*/
            for (int i = positionLast + 1; i < getItemCount(); i++) {
                Rect rect = mItems.get(i);
                if (Rect.intersects(rectRecyclerView, rect)) {
//                    Log.e("xiaojun","xiaojun111:addView:"+i);
                    View child = recycler.getViewForPosition(i);
                    measureChild(child, 0, 0);
                    addView(child);
                    layoutDecorated(child, rect.left - mTotalOffsetX, rect.top, rect.right - mTotalOffsetX, rect.bottom);
                }
            }
        }
        //→
        else if (direction == DIRECTION_LEFT_TO_RIGHT) {
            /*for (int i = positionLast; i >= 0 ; i--) {
                Rect rect = mItems.get(i);
                if (Rect.intersects(rectRecyclerView, rect)) {
                    Log.e("xiaojun","xiaojun111:addView:"+i);
                    View child = recycler.getViewForPosition(i);
                    measureChild(child, 0, 0);
                    addView(child, 0);
                    layoutDecorated(child, rect.left - mTotalOffsetX, rect.top, rect.right - mTotalOffsetX, rect.bottom);
                }
            }*/

            for (int i = positionFirst - 1; i >= 0; i--) {
                Rect rect = mItems.get(i);
                if (Rect.intersects(rectRecyclerView, rect)) {
//                    Log.e("xiaojun","xiaojun111:addView:"+i);
                    View child = recycler.getViewForPosition(i);
                    measureChild(child, 0, 0);
                    addView(child, 0);
                    layoutDecorated(child, rect.left - mTotalOffsetX, rect.top, rect.right - mTotalOffsetX, rect.bottom);
                }
            }
        }

        //设置当前的最靠近中间线的item
        getCurrentSelectedItemPosition();
    }

    private int getCurrentSelectedItemPosition() {
        int positionCenter = 0;
        //冒泡排序找到最小值
        Rect rectRecyclerView = getRecyclerViewRect();
        //中线的x轴坐标
        int xCenter = (rectRecyclerView.right - rectRecyclerView.left) / 2 + rectRecyclerView.left;
        for (int i = 0; i < getChildCount(); i++) {
            View child = getChildAt(i);
            Rect rect = mItems.get(getPosition(child));
            if (rect.left <= xCenter && rect.right > xCenter) {
                getChildAt(i).setBackgroundColor(Color.RED);
                positionCenter = i;
            } else {
                getChildAt(i).setBackgroundColor(Color.TRANSPARENT);
            }
        }
        int positionCenterReal = getPosition(getChildAt(positionCenter));
        mPositionSelectedItem = positionCenterReal;
        Log.e("xiaojun", "当前选中Position=" + mPositionSelectedItem);
        return positionCenterReal;
    }

    /**
     * 设置被选中监听器
     *
     * @param listener
     */
    public void setOnItemSelectedListener(OnItemSelectedAdapter listener) {
        this.mListener = listener;
    }

    /**
     * 回调适配器，用户可以按需实现某个功能
     */
    public static abstract class OnItemSelectedAdapter {
        /**
         * 正在改变，会返回状态
         *
         * @param position
         * @param state_selected
         */
        void onItemPositionChange(int position, STATE state_selected) {}

        /**
         * 最终被选中
         *
         * @param position
         */
        void onItemPositionChangeFinally(int position, STATE state) {}
    }

    /**
     * 选中状态
     */
    public enum STATE {
        SCROLL_IDLE,//用户滑动导致的被选中回调
        MANUAL_CLICK,//手动点击被选中回调
        NOTIFY_AND_NEWADAPTER;//NotifyDataSetChange和重新设置Adapter导致的回调
    }

}
