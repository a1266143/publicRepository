package com.example.testproject.recyclerview;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

/**
 * 自定义RecyclerView,配合CustomLayoutManagerRecycler2可以实现每次滑动item都居中
 * created by xiaojun at 2020/3/23
 */
public class CenterItemRecyclerView extends RecyclerView {

    //惯性滑动的速度阈值，滑动速度必须大于这个值才能进行惯性滑动
    private final int FLING_VALUE_EDGE = 100;
    //当前RecyclerView状态
    private int mState;

    public CenterItemRecyclerView(@NonNull Context context) {
        super(context);
        init();
    }

    public CenterItemRecyclerView(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public CenterItemRecyclerView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init(){
        setOverScrollMode(OVER_SCROLL_NEVER);
    }

    @Override
    public void onChildAttachedToWindow(@NonNull View child) {
        super.onChildAttachedToWindow(child);
    }

    @Override
    public void onScrollStateChanged(int state) {
        super.onScrollStateChanged(state);
        mState = state;
        Log.e("xiaojun","mState="+mState);
        if (mState == RecyclerView.SCROLL_STATE_IDLE){
//            ((CustomLayoutManagerRecycler2) getLayoutManager()).slideCurrentItemCenter();
//            smoothScrollToPosition(4);
            smoothScrollToPosition(((CenterShowLayoutManager) getLayoutManager()).getCurrentPosition());
        }
    }

    /**
     * 惯性滑动方法
     *
     * @param velocityX x轴滑动系数
     * @param velocityY
     * @return
     */
    @Override
    public boolean fling(int velocityX, int velocityY) {
        if (getChildCount()==0)
            return false;
        CenterShowLayoutManager layoutManager = (CenterShowLayoutManager) getLayoutManager();
        int newVelocityX = (int) (velocityX * 0.8f);
        if (newVelocityX < FLING_VALUE_EDGE && newVelocityX > -1 * FLING_VALUE_EDGE) {
            //让LayoutManager自动滑动到当前View的中间
//            layoutManager.slideCurrentItemCenter();
            smoothScrollToPosition(layoutManager.getCurrentPosition());
            return false;
        }
        //根据滑动系数算出fling的距离
        double distance = FlingUtils.getSplineFlingDistance(getContext(), newVelocityX);
        //根据速度算出新的distance
        double distanceNew = layoutManager.getProperDistance(newVelocityX, distance);
        //根据距离算出新的速度
        int finalVelocityX = FlingUtils.getVelocity(distanceNew);
        finalVelocityX = velocityX > 0 ? finalVelocityX : -finalVelocityX;
        return super.fling(finalVelocityX, velocityY);
    }


}
