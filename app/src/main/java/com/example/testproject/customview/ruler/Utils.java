package com.example.testproject.customview.ruler;


import java.util.List;

public class Utils {

    /**
     * 找出集合中最小的数
     * @param list
     * @return 返回最小数所在的索引
     */
    public static int getMinValueIndex(List<Float> list){
        float minValue = list.get(0);//定义第一个数为最小值
        int index = 0;
        for (int i = 0; i < list.size(); i++) {
            float value = list.get(i);
            if (value<minValue){
                minValue = value;
                index = i;
            }
        }
        return index;
    }
}
