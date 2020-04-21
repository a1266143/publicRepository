package com.example.testproject.customview;

import android.graphics.Point;

/**
 * 向量工具类
 * created by xiaojun at 2020/4/21
 */
public class VectorUtils {

    /**
     * 根据两个点求出向量的坐标
     *
     * @param startPoint 向量起始点
     * @param endPoint   向量结束点
     * @return
     */
    private Point vectorFrom2Point(Point startPoint, Point endPoint) {
        Point vectorNew = new Point();
        vectorNew.x = endPoint.x - startPoint.x;
        vectorNew.y = endPoint.y - startPoint.y;
        return vectorNew;
    }


    /**
     * 求向量的模（长度）
     *
     * @param x
     * @param y
     * @return
     */
    private double getVectorLength(double x, double y) {
        return Math.sqrt((Math.pow(x, 2) + Math.pow(y, 2)));
    }

    /**
     * 求向量的点积
     *
     * @param vector1
     * @param vector2
     * @return
     */
    private double dotProduct(Point vector1, Point vector2) {
        return vector1.x * vector2.x + vector1.y * vector2.y;
    }

    /**
     * 求 Left-Peak-Right 角度
     *
     * @param pointPeak  端点
     * @param pointLeft
     * @param pointRight
     */
    public int calculateAngleWithVector(Point pointPeak, Point pointLeft, Point pointRight) {
        //计算 PeakLeft 和 PeakRight 向量
        Point pointPeakLeft = vectorFrom2Point(pointPeak, pointLeft);
        Point pointPeakRight = vectorFrom2Point(pointPeak, pointRight);
        //计算 PeakLeft 和 PeakRight 向量的模
        double lengthPeakLeft = getVectorLength(pointPeakLeft.x, pointPeakLeft.y);
        double lengthPeakRight = getVectorLength(pointPeakRight.x, pointPeakRight.y);
        //计算 PeakLeft 和 PeakRight 的点积
        double crossProduct = dotProduct(pointPeakLeft, pointPeakRight);
        //求出 Left-Peak-Right 的余弦值
        double cosine = crossProduct / (lengthPeakLeft * lengthPeakRight);
        //求出角度
        double radian = Math.acos(cosine);
        double angle = radian * 180 / Math.PI;
        return (int) angle;
    }

}
