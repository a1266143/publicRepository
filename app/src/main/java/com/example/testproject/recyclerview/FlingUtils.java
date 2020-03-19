package com.example.testproject.recyclerview;

import android.content.Context;
import android.hardware.SensorManager;
import android.view.ViewConfiguration;

/**
 * 松手滑动辅助工具类
 * created by xiaojun at 2020/3/16
 */
public class FlingUtils {

    /**
     * 根据松手后的滑动速度计算出fling的距离
     *
     * @param velocity
     * @return
     */
    public static double getSplineFlingDistance(Context context,int velocity) {
        final double l = getSplineDeceleration(context,velocity);
        final double decelMinusOne = DECELERATION_RATE - 1.0;
        return mFlingFriction * getPhysicalCoeff(context) * Math.exp(DECELERATION_RATE / decelMinusOne * l);
    }

    /**
     * 根据距离计算出速度
     *
     * @param distance
     * @return
     */
    public static int getVelocity(double distance) {
        final double decelMinusOne = DECELERATION_RATE - 1.0;
        double aecel = Math.log(distance / (mFlingFriction * mPhysicalCoeff)) * decelMinusOne / DECELERATION_RATE;
        return Math.abs((int) (Math.exp(aecel) * (mFlingFriction * mPhysicalCoeff) / INFLEXION));
    }

    /**
     * --------------fling辅助类---------------
     */
    private static final float INFLEXION = 0.35f; // Tension lines cross at (INFLEXION, 1)
    private static float mFlingFriction = ViewConfiguration.getScrollFriction();
    private static float DECELERATION_RATE = (float) (Math.log(0.78) / Math.log(0.9));
    private static float mPhysicalCoeff = 0;

    private static double getSplineDeceleration(Context context,int velocity) {
        final float ppi = context.getResources().getDisplayMetrics().density * 160.0f;
        float mPhysicalCoeff = SensorManager.GRAVITY_EARTH // g (m/s^2)
                * 39.37f // inch/meter
                * ppi
                * 0.84f; // look and feel tuning


        return Math.log(INFLEXION * Math.abs(velocity) / (mFlingFriction * mPhysicalCoeff));
    }

    private static float getPhysicalCoeff(Context context) {
        if (mPhysicalCoeff == 0) {
            final float ppi = context.getResources().getDisplayMetrics().density * 160.0f;
            mPhysicalCoeff = SensorManager.GRAVITY_EARTH // g (m/s^2)
                    * 39.37f // inch/meter
                    * ppi
                    * 0.84f; // look and feel tuning
        }
        return mPhysicalCoeff;
    }

}
