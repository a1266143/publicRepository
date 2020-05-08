package com.example.testproject.customview;

import android.content.Context;

public class Utils {

    public static int dp2px(Context context,float dpValue){
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dpValue * scale + 0.5f);
    }
}
