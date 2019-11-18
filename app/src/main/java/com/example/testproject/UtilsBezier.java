package com.example.testproject;

import android.graphics.PointF;

import androidx.annotation.IntRange;

import java.util.ArrayList;
import java.util.List;

/**
 * 贝塞尔曲线工具类
 * 参考文章:
 * https://juejin.im/post/5c3988516fb9a049d1325c83
 * https://www.jianshu.com/p/55c721887568
 *
 * created by xiaojun at 2019/11/18
 */
public class UtilsBezier {

    private static final int X_TYPE = 0,Y_TYPE = 1;

    /**
     * 构建贝塞尔曲线，具体点数由 参数frame 决定
     *
     * @param controlPointList 控制点的坐标
     * @param frame            帧数
     * @return
     */
    public List<PointF> buildBezierPoint(List<PointF> controlPointList,
                                                int frame) {
        List<PointF> pointList = new ArrayList<>();

        float delta = 1f / frame;

        // 阶数，阶数=绘制点数-1
        int order = controlPointList.size() - 1;

        // 循环递增
        for (float u = 0; u <= 1; u += delta) {
            pointList.add(new PointF(calculatePointCoordinate(UtilsBezier.X_TYPE, u, order, 0, controlPointList),
                    calculatePointCoordinate(UtilsBezier.Y_TYPE, u, order, 0, controlPointList)));
        }

        return pointList;

    }

    /**
     * 计算坐标 [贝塞尔曲线的核心关键]
     *
     * @param type             {@link #X_TYPE} 表示x轴的坐标， {@link #Y_TYPE} 表示y轴的坐标
     * @param u                当前的比例
     * @param k                阶数
     * @param p                当前坐标（具体为 x轴 或 y轴）
     * @param controlPointList 控制点的坐标
     * @return
     */
    public float calculatePointCoordinate(@IntRange(from = X_TYPE, to = Y_TYPE) int type,
                                                 float u,
                                                 int k,
                                                 int p,
                                                 List<PointF> controlPointList) {

        /**
         * 公式解说：（p表示坐标点，后面的数字只是区分）
         * 场景：有一条线p1到p2，p0在中间，求p0的坐标
         *      p1◉--------○----------------◉p2
         *            u    p0
         *
         * 公式：p0 = p1+u*(p2-p1) 整理得出 p0 = (1-u)*p1+u*p2
         */

        // 一阶贝塞尔，直接返回
        if (k == 1) {

            float p1;
            float p2;

            // 根据是 x轴 还是 y轴 进行赋值
            if (type == X_TYPE) {
                p1 = controlPointList.get(p).x;
                p2 = controlPointList.get(p + 1).x;
            } else {
                p1 = controlPointList.get(p).y;
                p2 = controlPointList.get(p + 1).y;
            }

            return (1 - u) * p1 + u * p2;

        } else {

            /**
             * 这里应用了递归的思想：
             * 1阶贝塞尔曲线的端点 依赖于 2阶贝塞尔曲线
             * 2阶贝塞尔曲线的端点 依赖于 3阶贝塞尔曲线
             * ....
             * n-1阶贝塞尔曲线的端点 依赖于 n阶贝塞尔曲线
             *
             * 1阶贝塞尔曲线 则为 真正的贝塞尔曲线存在的点
             */
            return (1 - u) * calculatePointCoordinate(type, u, k - 1, p, controlPointList)
                    + u * calculatePointCoordinate(type, u, k - 1, p + 1, controlPointList);

        }

    }



    /**
     * B(t) = (1 - t)^2 * P0 + 2t * (1 - t) * P1 + t^2 * P2, t ∈ [0,1]
     *
     * @param t  曲线长度比例
     * @param p0 起始点
     * @param p1 控制点
     * @param p2 终止点
     * @return t对应的点
     */
    public PointF CalculateBezierPointForQuadratic(float t, PointF p0, PointF p1, PointF p2) {
        PointF sPoint = new PointF();
        float temp = 1 - t;
        sPoint.x = temp * temp * p0.x + 2 * t * temp * p1.x + t * t * p2.x;
        sPoint.y = temp * temp * p0.y + 2 * t * temp * p1.y + t * t * p2.y;
        return sPoint;
    }

    /**
     * B(t) = P0 * (1-t)^3 + 3 * P1 * t * (1-t)^2 + 3 * P2 * t^2 * (1-t) + P3 * t^3, t ∈ [0,1]
     *
     * @param t  曲线长度比例
     * @param p0 起始点
     * @param p1 控制点1
     * @param p2 控制点2
     * @param p3 终止点
     * @return t对应的点
     */
    public PointF CalculateBezierPointForCubic(float t, PointF p0, PointF p1, PointF p2, PointF p3) {
        PointF sPoint = new PointF();
        float temp = 1 - t;
        sPoint.x = p0.x * temp * temp * temp + 3 * p1.x * t * temp * temp + 3 * p2.x * t * t * temp + p3.x * t * t * t;
        sPoint.y = p0.y * temp * temp * temp + 3 * p1.y * t * temp * temp + 3 * p2.y * t * t * temp + p3.y * t * t * t;
        return sPoint;
    }

}
