package com.example.testproject;

import java.util.Random;

/**
 * 工具类
 * created by xiaojun at 2019/11/15
 */
public class Utils {

    private Random mRandom = new Random();

    /**
     * 随机返回True或者False
     * @return
     */
    public boolean getRandomTrueOrFalse(){
//        int result = mRandom.nextInt(100);
//        return result>=50;
        return true;
    }

    /**
     * 生成边界值之内的一个随机数
     * @param bound
     * @return
     */
    public int getRandomNumber(int bound){
        return mRandom.nextInt(bound);
    }

    private int mDegree = 0;
    public int getDegree(){
        return (++mDegree)%360;
    }

    /**
     * 从两个值中获取较大的值
     * @param value1
     * @param value2
     * @return
     */
    public float getMaxValue(float value1,float value2){
        if (value1>=value2)
            return value1;
        return value2;
    }

    /**
     * 从两个值中获取较小的值
     * @param value1
     * @param value2
     * @return
     */
    public float getMinValue(float value1,float value2){
        if (value1<value2)
            return value1;
        return value2;
    }

    private void sleep(int millsecond) {
        try {
            Thread.currentThread().sleep(millsecond);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
