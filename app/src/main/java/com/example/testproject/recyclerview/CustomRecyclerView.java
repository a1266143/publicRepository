package com.example.testproject.recyclerview;

import android.content.Context;
import android.util.AttributeSet;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

/**
 * 自定义RecyclerView校正fling
 * created by xiaojun at 2020/3/16
 */
public class CustomRecyclerView extends RecyclerView {
    public CustomRecyclerView(@NonNull Context context) {
        super(context);
    }

    public CustomRecyclerView(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public CustomRecyclerView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    /**
     * 松手滑动
     *
     * @param velocityX 横向滑动系数
     * @param velocityY 竖向滑动系数
     * @return
     */
    @Override
    public boolean fling(int velocityX, int velocityY) {
        //将速度减速，获取减速后的速度
        int wantVelocityX = (int) (velocityX * 0.4f);
        //1.获取新速度下滑动的距离
        int distance = (int) FlingUtils.getSplineFlingDistance(getContext(), wantVelocityX);
        //2.根据上面滑动的距离计算出最靠近哪个Item的中心位置，并生成新的滑动距离
        int newDistance = getProperDistance(distance);
        //3.根据上面的newDistance计算出新的滑动速度
        int newVelocityX = FlingUtils.getVelocity(newDistance);
        //4.返回给系统新的速度 以 滑动到正确的item
        return super.fling(newVelocityX, velocityY);
    }

    //TODO 获取适当的距离
    private int getProperDistance(int originalDistance) {

        return 0;
    }
}
