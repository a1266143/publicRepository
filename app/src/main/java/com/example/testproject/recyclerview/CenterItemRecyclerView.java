package com.example.testproject.recyclerview;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

/**
 * 自定义RecyclerView,配合CustomLayoutManagerRecycler2可以实现每次滑动item都居中
 * created by xiaojun at 2020/3/23
 */
public class CenterItemRecyclerView extends RecyclerView {

    public CenterItemRecyclerView(@NonNull Context context) {
        super(context);
    }

    public CenterItemRecyclerView(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public CenterItemRecyclerView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    public void onScrollStateChanged(int state) {
        super.onScrollStateChanged(state);
        Log.e("xiaojun","state="+state);
    }


    @Override
    public void scrollToPosition(int position) {
        super.scrollToPosition(position);

    }

    /**
     * 滑动方法
     * @param velocityX x轴滑动系数
     * @param velocityY
     * @return
     */
    @Override
    public boolean fling(int velocityX, int velocityY) {
        int newVelocityX = (int) (velocityX*0.2f);
        CustomLayoutManagerRecycler2 layoutManager = (CustomLayoutManagerRecycler2) getLayoutManager();
        //根据滑动系数算出fling的距离
        double distance = FlingUtils.getSplineFlingDistance(getContext(),newVelocityX);
        //根据速度算出新的distance
        double distanceNew = layoutManager.getProperDistance(newVelocityX,distance);
        //根据距离算出新的速度
        int finalVelocityX = FlingUtils.getVelocity(distanceNew);
        finalVelocityX = velocityX>0?finalVelocityX:-finalVelocityX;
        return super.fling(finalVelocityX, velocityY);
    }





}
